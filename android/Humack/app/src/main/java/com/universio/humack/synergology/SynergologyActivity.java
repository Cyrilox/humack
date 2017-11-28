package com.universio.humack.synergology;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.universio.humack.BaseActivity;
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
public class SynergologyActivity extends BaseActivity implements View.OnLayoutChangeListener, ImageFragment.OnInteractionListener{
    //Données
    private ArrayList<Attitude> attitudes;

    //General
    private int currentGroupId;
    private String currentGroupName = null, toast_booka;

    //Fragments
    private FragmentManager fragmentManager;
    private ImageFragment imageFragment;
    private ImageView imageView;
    private int imageViewWidth, imageViewHeight, centeringSpaceWidth, centeringSpaceHeight;
    private ImageArea currentImageArea;
    private Hashtable<Integer, AttitudeFragment> attitudeFragments;
    private AttitudeFragment attitudeFragmentVisible;
    private int attitudeFragmentHeight;

    //Views and Size
    private FrameLayout synergologyLayout;
    private int layoutWidth, layoutHeight, splitY;
    private Rect frameFullsize, frameSmallsize;

    //Aide
    private Intent synergologyHelpActivity;

    //Animation
    private AnimatorSet animations;
    private boolean animationRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupActivity(R.layout.activity_synergology, R.drawable.icon_synergology);
        super.onCreate(savedInstanceState);
    }

    protected void init(){
        //General
        toast_booka = getResources().getString(R.string.synergology_toast_page_booka);

        //Universal Image Loader
        //Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration universalImaLoaConfig = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(universalImaLoaConfig);

        //Load the database
        loadDatabase();

        //Main layout
        synergologyLayout = (FrameLayout)findViewById(R.id.activity_synergology);
        synergologyLayout.addOnLayoutChangeListener(this);
        synergologyHelpActivity = new Intent(this, SynergologyHelpActivity.class);
        splitY = 200;

        //Fragments
        fragmentManager = getFragmentManager();

        //Body Image Fragment
        imageFragment = (ImageFragment)fragmentManager.findFragmentById(R.id.fragment_image);
        imageFragment.setAssetManager(getAssets());
        imageView = (ImageView)imageFragment.getView();

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
        DatabaseAO databaseAO = BaseActivity.getDatabaseAO();

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
        splitY = (int)(Math.floor(layoutHeight * 25 / 100));
        frameFullsize = new Rect(left, top, left + layoutWidth, top + layoutHeight);
        frameSmallsize = new Rect(left, top, left + layoutWidth, top + splitY);
        imageViewWidth = imageView.getMeasuredWidth();
        imageViewHeight = imageView.getMeasuredHeight();
        attitudeFragmentHeight = layoutHeight - splitY;
        centeringSpaceWidth = (layoutWidth - imageViewWidth) / 2;
        centeringSpaceHeight = (layoutHeight - imageViewHeight) / 2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_synergology, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Si on ferme le drawer
        if(closeDrawer())
            return true; //on ne fait que cela
        else{ //sinon
            switch (item.getItemId()) {
                case R.id.action_help_synergology://on lance l'aide sur la synergologie
                    startActivity(synergologyHelpActivity);
                    return true;
                case android.R.id.home://ou on dézoom si possible
                    if(hasPrevious()) {
                        goBack();
                        return true;
                    }
                default://dans les autres cas
                    return super.onOptionsItemSelected(item);//toggle du drawer
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(!closeDrawer())
            if(hasPrevious())
                goBack();
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
                    animationRunning = false;
                }});
            animations.start();

            //Action Bar
            setActionBarHome(hasPrevious(), currentGroupName);
        }
    }

    /**
     * Zoom and translate image fragment to the ImageArea
     * @param imageArea The destination area
     */
    private void showImage(ImageArea imageArea){
        Rect toframe, imageFrame;
        int toFrameWidth, toframeHeight, imageFrameWidth, imageFrameHeight, toLayoutWidth, toLayoutFrameWidth, toLayoutHeight;
        int toImageLeft, toImageTop, toImageWidth, toImageHeight, translationX, translationY;

        //Destination frame on the screen
        if(imageArea.getId() == 100 || imageArea.getId() == 103) //Head
            //Show in full size
            toframe = frameFullsize;
        else //Else small size with attitudes
            toframe = frameSmallsize;
        toFrameWidth = toframe.right - toframe.left;
        toframeHeight = toframe.bottom - toframe.top;

        //Areas with the full size
        imageFrame = imageArea.getImageFrame();
        imageFrameWidth = imageFrame.right - imageFrame.left;
        imageFrameHeight = imageFrame.bottom - imageFrame.top;

        //Layout and image destination
        toLayoutHeight = (int)Math.floor(ImageFragment.REAL_IMAGE_HEIGHT * toframeHeight / imageFrameHeight);
        toLayoutWidth = (int)Math.floor(ImageFragment.REAL_IMAGE_WIDTH * toLayoutHeight / ImageFragment.REAL_IMAGE_HEIGHT);
        toLayoutFrameWidth = (int)Math.floor(toLayoutWidth * imageFrameWidth / ImageFragment.REAL_IMAGE_WIDTH);
        if(toLayoutFrameWidth > toFrameWidth) {
            toLayoutWidth = (int) Math.floor(ImageFragment.REAL_IMAGE_WIDTH * toFrameWidth / imageFrameWidth);
            toLayoutHeight = (int) Math.floor(ImageFragment.REAL_IMAGE_HEIGHT * toLayoutWidth / ImageFragment.REAL_IMAGE_WIDTH);
        }

        toImageLeft = imageFrame.left * toLayoutWidth / ImageFragment.REAL_IMAGE_WIDTH;
        toImageTop = imageFrame.top * toLayoutHeight / ImageFragment.REAL_IMAGE_HEIGHT;
        toImageWidth = toLayoutWidth * imageFrameWidth / ImageFragment.REAL_IMAGE_WIDTH;
        toImageHeight = toLayoutHeight * imageFrameHeight / ImageFragment.REAL_IMAGE_HEIGHT;

        translationX = - toImageLeft + (toFrameWidth - toImageWidth) / 2 - centeringSpaceWidth;
        translationY = - toImageTop + (toframeHeight - toImageHeight) / 2 - centeringSpaceHeight;

        //Scale values
        float toXScale, toYScale;
        toXScale = (float)toLayoutWidth / imageViewWidth;
        toYScale = (float)toLayoutHeight / imageViewHeight;

        //Animation
        PropertyValuesHolder scaleX, scaleY, translateX, translateY;
        scaleX = PropertyValuesHolder.ofFloat("scaleX", toXScale);
        scaleY = PropertyValuesHolder.ofFloat("scaleY", toYScale);
        translateX = PropertyValuesHolder.ofFloat("translationX", translationX);
        translateY = PropertyValuesHolder.ofFloat("translationY", translationY);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageView, scaleX, scaleY, translateX, translateY);
        animator.setDuration(Settings.getAnimationSpeed());
        animations.play(animator);
        
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
                    attitudeFragments.put(groupId, attitudeFragment);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.activity_synergology, attitudeFragment);
                    fragmentTransaction.commit();
                    fragmentManager.executePendingTransactions();
                }
                attitudeFragmentView = attitudeFragment.getView();
                if(attitudeFragmentView != null) {
                    attitudeFragmentView.getLayoutParams().width = layoutWidth;
                    attitudeFragmentView.getLayoutParams().height = attitudeFragmentHeight;
                    //Put attitudes into fragment
                    attitudeFragment.init(attitudesToView, imageArea.getColor());
                    //Init the view
                    attitudeFragmentView.setX(0);
                    attitudeFragmentView.setY(layoutHeight);
                    attitudeFragmentView.bringToFront();
                }
            } else {
                if (groupId == 100)
                    currentGroupName = "Tête";
                else if(groupId == 103)
                    currentGroupName = null;
            }

            //Animation
            ObjectAnimator translatePrevious = null, hidePrevious = null, viewNext = null, translateNext = null;
            if (attitudeFragmentVisible != null) {
                //Translate to the bottom then hide
                translatePrevious = ObjectAnimator.ofFloat(attitudeFragmentVisible.getView(), "translationY", layoutHeight);
                translatePrevious.setDuration(Settings.getAnimationSpeed());
                hidePrevious = ObjectAnimator.ofInt(attitudeFragmentVisible.getView(), "visibility", View.GONE);
                hidePrevious.setDuration(0);
            }
            if (attitudeFragment != null) {
                //Translate to the top
                viewNext = ObjectAnimator.ofInt(attitudeFragmentView, "visibility", View.VISIBLE);
                viewNext.setDuration(0);
                translateNext = ObjectAnimator.ofFloat(attitudeFragmentView, "translationY", layoutHeight, splitY);
                translateNext.setDuration(Settings.getAnimationSpeed());
            }

            if (translateNext != null) {//Show Next
                animations.play(viewNext);
                animations.play(translateNext).after(viewNext);
            }
            if (translatePrevious != null) {//Hide Previous
                animations.play(translatePrevious);
                animations.play(hidePrevious).after(translatePrevious);
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
                Tools.toast(this, String.format(toast_booka, attitude.getBooka_page()));
    }

    private boolean hasPrevious(){
        return (currentImageArea != null) && (currentImageArea.getId() != 103);
    }
}