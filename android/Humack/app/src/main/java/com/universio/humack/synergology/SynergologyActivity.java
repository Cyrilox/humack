package com.universio.humack.synergology;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.universio.humack.ActivityFragment;
import com.universio.humack.MainActivity;
import com.universio.humack.R;
import com.universio.humack.Settings;
import com.universio.humack.Tools;
import com.universio.humack.data.Data;
import com.universio.humack.data.DatabaseAO;
import com.universio.humack.synergology.data.Attitude;
import com.universio.humack.synergology.data.AttitudeDAO;
import com.universio.humack.synergology.data.AttitudeTypeDAO;
import com.universio.humack.synergology.data.BodypartDAO;
import com.universio.humack.synergology.data.MicromovementDAO;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Cyril Humbertclaude on 04/05/2015.
 */
public class SynergologyActivity extends ActivityFragment implements View.OnLayoutChangeListener, ImageFragment.OnInteractionListener, AttitudeFragment.OnInteractionListener{

    //Données
    private ArrayList<Attitude> attitudes;

    //General
    private int currentGroupId;
    private String currentGroupName = null, toast_booka;
    private float ELEVATION_ATTITUDES, ELEVATION_ATTITUDES_ABOVE;

    //Fragments
    private FragmentManager fragmentManager;
    private ImageFragment imageFragment;
    private SubsamplingScaleImageView imageView;
    private int imageViewWidth, imageViewHeight;
    private ImageArea currentImageArea;
    private Hashtable<Integer, AttitudeFragment> attitudeFragments;
    private AttitudeFragment attitudeFragmentVisible;

    //Views and Size
    private FrameLayout synergologyLayout;
    private int layoutWidth, layoutHeight, splitY, attitudeFragmentHeight;
    private float splitYRatio;
    private Rect frameFullsize, frameSmallsize;

    //Animation
    private AnimatorSet animations;
    private boolean animationRunning;

    public static SynergologyActivity newInstance(){
        return new SynergologyActivity();
    }

    /**
     * Called in order to create the view
     * @return The layout resource id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_synergology;
    }

    @Override
    public void init(){
        //General
        setOptionsMenu(R.menu.menu_synergology);
        toast_booka = getResources().getString(R.string.synergology_toast_page_booka);
        ELEVATION_ATTITUDES = getResources().getDimension(R.dimen.elevation_attitudes);
        ELEVATION_ATTITUDES_ABOVE = ELEVATION_ATTITUDES + getResources().getDimension(R.dimen.elevation_default_spacing);

        //Load the database
        loadDatabase();

        //Main layout
        synergologyLayout = (FrameLayout)getView();
        if(synergologyLayout != null)
            synergologyLayout.addOnLayoutChangeListener(this);
        splitYRatio = 0.25f;
        splitY = 200;

        //Fragments
        fragmentManager = mainActivity.getFragmentManager();

        //Body Image Fragment
        imageFragment = ImageFragment.newInstance();
        fragmentManager.beginTransaction().add(R.id.activity_synergology, imageFragment).commit();
        fragmentManager.executePendingTransactions();//Fragment onCreateView called here
        imageFragment.setListener(this);
        imageView = (SubsamplingScaleImageView)getView().findViewById(R.id.fragment_image);

        //Attitude Fragment
        attitudeFragments = new Hashtable<>();
        attitudeFragmentVisible = null;
        currentGroupId = -1;

        //Animation
        animationRunning = false;
    }

    @SuppressWarnings("unchecked")
    private void loadDatabase(){
        //Base unique
        DatabaseAO databaseAO = MainActivity.getDatabaseAO();

        //Accès aux données
        AttitudeTypeDAO attitudeTypeDAO = new AttitudeTypeDAO(databaseAO);
        BodypartDAO bodypartDAO = new BodypartDAO(databaseAO);
        MicromovementDAO micromovementDAO = new MicromovementDAO(databaseAO);
        AttitudeDAO attitudeDAO = new AttitudeDAO(databaseAO);

        //Récuperation des données
        databaseAO.open();
        ArrayList attitudeTypes, bodyparts, micromovements;
        attitudeTypes = attitudeTypeDAO.getAll(null);
        bodyparts = bodypartDAO.getAll(null);
        micromovements = micromovementDAO.getAll(null);

        attitudeDAO.setForeignDatas(attitudeTypes, bodyparts, micromovements);
        attitudes = attitudeDAO.getAll(AttitudeDAO.COL_SUBORDER_NAME);
        databaseAO.close();
    }

    /**
     * Update the sizes of the views
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        layoutWidth = synergologyLayout.getMeasuredWidth();
        layoutHeight = synergologyLayout.getMeasuredHeight();
        splitY = (int)(Math.floor(layoutHeight * splitYRatio));
        attitudeFragmentHeight = layoutHeight - splitY;
        imageViewWidth = imageView.getMeasuredWidth();
        imageViewHeight = imageView.getMeasuredHeight();
        frameFullsize = new Rect(left, top, left + imageViewWidth, top + imageViewHeight);
        frameSmallsize = new Rect(left, top, left + imageViewWidth, top + splitY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item != null ? item.getItemId() : android.R.id.home;
        switch (itemId){
            case R.id.action_help_synergology:
                mainActivity.showActivity(MainActivity.ACTIVITY_SYNERGOLOGY_HELP, false);
                return true;
            case android.R.id.home:
                if(hasPrevious()){
                    goBack();
                    return true;
                }
            default:
                return false;
        }
    }

    @Override
    public boolean onBackPressed() {
        if(hasPrevious()){
            goBack();
            return true;
        }
        return false;
    }

    private boolean hasPrevious(){
        return (currentImageArea != null) && (currentImageArea.getId() != 103);
    }

    private void goBack(){
        ImageArea imageArea=null;
        //Body part or Head to Body
        if(imageFragment.getCurrentAreaImage().equals(ImageFragment.IMAGE_BODY_AREAS) || currentImageArea.getId() == 100)
            imageArea = imageFragment.getImageArea(103);
        //Head part to Head
        else if(imageFragment.getCurrentAreaImage().equals(ImageFragment.IMAGE_HEAD_AREAS) && currentImageArea.getId() != 100)
            imageArea = imageFragment.getImageArea(100);
        //Go to this area
        if(imageArea != null)
            onImageAreaClick(imageArea);
    }

    /**
     * A click on a body part
     * Zoom to the body part
     * If not the head, init corresponding attitudes
     * @param imageArea The imageArea clicked
     */
    @Override
    public void onImageAreaClick(ImageArea imageArea) {
        if(!animationRunning && imageArea != currentImageArea) {
            //Same container for all animations
            animationRunning = true;
            animations = new AnimatorSet();

            //Show the image
            showImage(imageArea);

            //Show attitudes if it's not the head
            showAttitude(imageArea);

            //Start all animations
            animations.setInterpolator(new AccelerateDecelerateInterpolator());
            animations.addListener(new AnimatorListenerAdapter(){
                public void onAnimationEnd(Animator animation){
                    if(attitudeFragmentVisible != null)
                        ViewCompat.setElevation(attitudeFragmentVisible.getView(), ELEVATION_ATTITUDES);
                    animationRunning = false;
                }});
            animations.start();

            //Action Bar
            mainActivity.setActionBarHome(hasPrevious(), currentGroupName);
        }
    }

    /**
     * Zoom and translate image fragment to the ImageArea
     * @param imageArea The destination area
     */
    private void showImage(ImageArea imageArea){
        Rect toframe, imageFrame;
        float toFrameWidth, toframeHeight, imageFrameWidth, imageFrameHeight, imageFrameHeightVisible, imageFrameCenterX, imageFrameCenterY, toCenterX, toCenterY, toScale, toScaleX, toScaleY, visibleWidth, xShift, yShift;
        boolean scaleFromHeight;

        //Destination frame on the screen
        if(imageArea.getId() == 100 || imageArea.getId() == 103) //Head, show in full size
            toframe = frameFullsize;
        else //Else small size with attitudes
            toframe = frameSmallsize;
        toFrameWidth = toframe.right - toframe.left;
        toframeHeight = toframe.bottom - toframe.top;

        //Areas with the full size
        imageFrame = imageArea.getImageFrame();
        imageFrameWidth = imageFrame.right - imageFrame.left;
        imageFrameHeight = imageFrame.bottom - imageFrame.top;
        imageFrameCenterX = (int)Math.floor(imageFrame.left + imageFrameWidth / 2);
        imageFrameCenterY = (int)Math.floor(imageFrame.top + imageFrameHeight / 2);

        //Scale
        visibleWidth = imageFrameHeight * toFrameWidth / toframeHeight;
        scaleFromHeight = visibleWidth > imageFrameWidth;
        toScaleX = toFrameWidth / imageFrameWidth;
        toScaleY = toframeHeight / imageFrameHeight;
        toScale = scaleFromHeight ? toScaleY : toScaleX;

        imageFrameHeightVisible = imageFrameHeight;
        if(!scaleFromHeight)
            imageFrameHeightVisible *= toScaleY / toScaleX;

        //Translate
        xShift = toFrameWidth == imageViewWidth ? 0 : ImageFragment.REAL_IMAGE_WIDTH * imageFrameWidth * 1.5f / ImageFragment.REAL_IMAGE_WIDTH;
        yShift = toframeHeight == imageViewHeight ? 0 : ImageFragment.REAL_IMAGE_HEIGHT * imageFrameHeightVisible * 1.5f / ImageFragment.REAL_IMAGE_HEIGHT;
        toCenterX = (int)Math.floor(imageFrameCenterX + xShift);
        toCenterY = (int)Math.floor(imageFrameCenterY + yShift);

        PointF toCenter = new PointF(toCenterX, toCenterY);

        imageView.animateScaleAndCenter(toScale, toCenter)
                .withDuration(Settings.getAnimationSpeed())
                .withEasing(SubsamplingScaleImageView.EASE_IN_OUT_QUAD)
                .withInterruptible(false)
                .start();

        //Save new state
        if(imageArea.getId() == 100)
            imageFragment.setCurrentAreaImage(ImageFragment.IMAGE_HEAD_AREAS);
        else if(imageArea.getId() == 11 || imageArea.getId() == 103)
            imageFragment.setCurrentAreaImage(ImageFragment.IMAGE_BODY_AREAS);

        this.currentImageArea = imageArea;
    }

    //Update the attitudes fragment and init it
    private void showAttitude(ImageArea imageArea){
        ArrayList<Attitude> attitudesToView = new ArrayList<>();
        Boolean attitudeInGroup;
        int type, bodyPart, groupId;
        AttitudeFragment attitudeFragment=null;
        View attitudeFragmentView = null;

        groupId = imageArea.getId();
        if(groupId != currentGroupId) {
            for (Attitude attitude : attitudes) {
                attitudeInGroup = false;
                //If it's the right type
                type = attitude.getAttitudeType().getId();
                switch (type) {
                    case 1: //On Body
                        bodyPart = attitude.getBodypart().getId();
                        if (bodyPart == groupId) {
                            attitudeInGroup = true;
                            currentGroupName = attitude.getBodypart().getName();
                        }
                        break;
                    case 2: //Handshake
                        if (groupId == 101) {
                            attitudeInGroup = true;
                            currentGroupName = attitude.getAttitudeType().getName();
                        }
                        break;
                    case 3: //Seated
                        if (groupId == 102) {
                            attitudeInGroup = true;
                            currentGroupName = attitude.getAttitudeType().getName();
                        }
                        break;
                    default:
                }
                if (attitudeInGroup)
                    attitudesToView.add(attitude);
            }

            //If we have found attitudes
            if (attitudesToView.size() > 0) {
                //Get the AttitudeFragment from cache
                attitudeFragment = attitudeFragments.get(groupId);
                //If is not present
                if (attitudeFragment == null) {
                    //Create Fragment
                    attitudeFragment = AttitudeFragment.newInstance();
                    attitudeFragment.setListener(this);
                    attitudeFragments.put(groupId, attitudeFragment);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.activity_synergology, attitudeFragment);
                    fragmentTransaction.commit();
                    fragmentManager.executePendingTransactions();//Fragment onCreateView called here

                    attitudeFragmentView = attitudeFragment.getView();
                    if(attitudeFragmentView != null) {
                        attitudeFragmentView.getLayoutParams().width = layoutWidth;
                        attitudeFragmentView.getLayoutParams().height = attitudeFragmentHeight;
                        //Put attitudes into fragment
                        attitudeFragment.init(attitudesToView);
                    }
                }
                if(attitudeFragmentView == null)
                    attitudeFragmentView = attitudeFragment.getView();
                if(attitudeFragmentView != null) {//Init the view
                    attitudeFragmentView.setX(0);
                    attitudeFragmentView.setY(layoutHeight);
                    attitudeFragmentView.bringToFront();
                }
            } else {
                if (groupId == 100)
                    currentGroupName = "Tête";
                else if(groupId == 103)
                    currentGroupName = "";
            }

            //Animation
            ObjectAnimator translatePrevious, hidePrevious, viewNext, translateNext;
            //Hide attitude fragment
            if (attitudeFragmentVisible != null) {
                ViewCompat.setElevation(attitudeFragmentVisible.getView(), ELEVATION_ATTITUDES);
                //Translate to the bottom then hide
                translatePrevious = ObjectAnimator.ofFloat(attitudeFragmentVisible.getView(), "translationY", layoutHeight);
                translatePrevious.setDuration(Settings.getAnimationSpeed());
                hidePrevious = ObjectAnimator.ofInt(attitudeFragmentVisible.getView(), "visibility", View.GONE);
                hidePrevious.setDuration(0);
                animations.play(translatePrevious);
                animations.play(hidePrevious).after(translatePrevious);
            }
            //Show attitude fragment
            if (attitudeFragment != null) {
                ViewCompat.setElevation(attitudeFragmentView, ELEVATION_ATTITUDES_ABOVE);
                //Translate to the top
                viewNext = ObjectAnimator.ofInt(attitudeFragmentView, "visibility", View.VISIBLE);
                viewNext.setDuration(0);
                translateNext = ObjectAnimator.ofFloat(attitudeFragmentView, "translationY", layoutHeight, splitY);
                translateNext.setDuration(Settings.getAnimationSpeed());
                animations.play(viewNext);
                animations.play(translateNext).after(viewNext);
            }

            //Save new state
            if (attitudeFragment != null) {
                attitudeFragmentVisible = attitudeFragment;
                currentGroupId = groupId;
            }else {
                attitudeFragmentVisible = null;
                currentGroupId = -1;
            }
        }
    }

    public void onAttitudeClick(View view) {
        Attitude attitude = Data.getDataById(attitudes, view.getId());
        if(attitude != null)
            if(attitude.hasBooka_page())
                Tools.toast(mainActivity, String.format(toast_booka, attitude.getBooka_page()));
    }
}