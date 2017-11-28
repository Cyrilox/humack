package com.universio.humack.synergology;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.universio.humack.ActivityFragment;
import com.universio.humack.MainActivity;
import com.universio.humack.R;
import com.universio.humack.Settings;
import com.universio.humack.data.Data;
import com.universio.humack.data.DatabaseAO;
import com.universio.humack.synergology.data.Attitude;
import com.universio.humack.synergology.data.AttitudeDAO;
import com.universio.humack.synergology.data.BodyGroup;
import com.universio.humack.synergology.data.BodyGroupDAO;
import com.universio.humack.synergology.data.Hemisphere;
import com.universio.humack.synergology.data.HemisphereDAO;
import com.universio.humack.synergology.data.ImageArea;
import com.universio.humack.synergology.data.ImageAreaDAO;
import com.universio.humack.synergology.data.Micromovement;
import com.universio.humack.synergology.data.MicromovementDAO;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Cyril Humbertclaude on 04/05/2015.
 */
public class SynergologyActivity extends ActivityFragment implements View.OnLayoutChangeListener, ImageFragment.OnInteractionListener, AttitudeFragment.OnInteractionListener{

    //Données
    private ArrayList<Attitude> attitudes;
    private ArrayList<BodyGroup> bodyGroups;
    private ArrayList<ImageArea> imageAreas;

    //General
    private BodyGroup currentBodyGroup = null;
    private AttitudeFragment currentAttitudeFragment = null;
    private String toast_booka;

    //Fragments
    private FragmentManager fragmentManager;
    private ImageFragment imageFragment;
    private Hashtable<BodyGroup, AttitudeFragment> attitudeFragments;

    //Views and Size
    private FrameLayout synergologyLayout;
    private int layoutWidth;

    private int layoutHeight;
    private int splitY;
    private int attitudeFragmentHeight;
    private float splitYRatio;

    //Tour guide
    private boolean synergologyGuide1, synergologyGuide2;
    private Snackbar synergologyGuide1Snackbar, synergologyGuide3Snackbar;

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
        //Fragments
        fragmentManager = mainActivity.getFragmentManager();

        //General
        setOptionsMenu(R.menu.menu_synergology);
        currentBodyGroup = null;
        toast_booka = getResources().getString(R.string.synergology_toast_page_booka);

        //Load the database
        loadDatabase();

        //Body Image Fragment
        imageFragment = ImageFragment.newInstance();
        imageFragment.setImageAreas(imageAreas);
        fragmentManager.beginTransaction().add(R.id.activity_synergology, imageFragment).commit();
        fragmentManager.executePendingTransactions();//Fragment onCreateView called here
        imageFragment.setListener(this);

        //Main layout
        synergologyLayout = (FrameLayout)getView();
        if(synergologyLayout != null)
            synergologyLayout.addOnLayoutChangeListener(this);
        splitYRatio = 0.25f;
        splitY = 200;

        //Attitude Fragment
        attitudeFragments = new Hashtable<>();

        //Tour guide
        if(Settings.isSynergologyGuide()) {
            synergologyGuide1 = true;
            synergologyGuide2 = true;
        }else{
            synergologyGuide1 = false;
            synergologyGuide2 = false;
            Settings.setSynergologyGuide(true);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadDatabase(){
        //Base unique
        DatabaseAO databaseAO = MainActivity.getDatabaseAO();

        //Accès aux données
        HemisphereDAO hemisphereDAO = new HemisphereDAO(databaseAO);
        MicromovementDAO micromovementDAO = new MicromovementDAO(databaseAO);
        BodyGroupDAO bodyGroupDAO = new BodyGroupDAO(databaseAO);
        ImageAreaDAO imageAreaDAO = new ImageAreaDAO(databaseAO);
        AttitudeDAO attitudeDAO = new AttitudeDAO(databaseAO);

        //Récuperation des données
        databaseAO.open();

        ArrayList<Hemisphere> hemispheres = hemisphereDAO.getAll(null);
        ArrayList<Micromovement> micromovements = micromovementDAO.getAll(null);
        bodyGroups = bodyGroupDAO.getAll(null);
        imageAreaDAO.setForeignDatas(bodyGroups);
        imageAreas = imageAreaDAO.getAll(null);
        attitudeDAO.setForeignDatas(bodyGroups, micromovements, hemispheres);
        attitudes = attitudeDAO.getAll(AttitudeDAO.COL_SUBORDER_NAME);

        databaseAO.close();

        //Chargement objet des Attitude dans les BodyGroup
        for(Attitude attitude : attitudes)
            attitude.getBodyGroup().addAttitude(attitude);

        //Chargement objet des ImageArea dans les BodyGroup
        for(ImageArea imageArea : imageAreas)
            imageArea.getBodyGroup().setImageArea(imageArea);
    }

    @Override
    public void onVisible() {
        //Guide 1 - Ouverture
        if(!synergologyGuide1) {
            synergologyGuide1Snackbar = Snackbar.make(synergologyLayout, R.string.tourguide_synergology_1, Snackbar.LENGTH_INDEFINITE);
            synergologyGuide1Snackbar.show();
        }
    }

    @Override
    public void onHiding() {
        //Guide
        if(synergologyGuide1Snackbar != null)
            synergologyGuide1Snackbar.dismiss();
    }

    /**
     * Update the sizes of the views
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //Si les dimensions ont changées
        if(synergologyLayout.getMeasuredWidth() != layoutWidth || synergologyLayout.getMeasuredHeight() != layoutHeight) {
            //Nouvelles dimensions
            layoutWidth = synergologyLayout.getMeasuredWidth();
            layoutHeight = synergologyLayout.getMeasuredHeight();
            splitY = (int) (Math.floor(layoutHeight * splitYRatio));
            attitudeFragmentHeight = layoutHeight - splitY;
            //Mise à jour des esclaves
            imageFragment.updateSizes(splitY);
            for (AttitudeFragment attitudeFragment : attitudeFragments.values())
                attitudeFragment.updateSizes(splitY, layoutHeight);
        }
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

    /**
     * Find if it's possible to go to a group above by zooming out
     * @return True if it's possible
     */
    private boolean hasPrevious(){
        return (currentBodyGroup != null) && (currentBodyGroup.getId() != 22);
    }

    /**
     * Switch to the head or body group, zoom out
     */
    private void goBack(){
        BodyGroup previousBodyGroup=null;
        int id = currentBodyGroup.getId();
        //Body part / Head to Body
        if(id > 10 && id != 22)
            previousBodyGroup = Data.getDataById(bodyGroups, 22);
        //Head part to Head
        else if(id <= 10)
            previousBodyGroup = Data.getDataById(bodyGroups, 21);
        //Go to this area
        if(previousBodyGroup != null)
            switchToGroup(previousBodyGroup, previousBodyGroup.getImageArea());
    }

    /**
     * Trigger switchToGroup with the BodyGroup of the ImageArea
     * @param imageArea The imageArea clicked
     */
    @Override
    public void onImageAreaClick(ImageArea imageArea) {
        switchToGroup(imageArea.getBodyGroup(), imageArea);
    }

    /**
     * A click on a body part
     * Zoom to the body part
     * Show attitudes related
     * @param bodyGroup The BodyGroup to switch
     * @param imageArea The ImageArea to see
     */
    private void switchToGroup(BodyGroup bodyGroup, ImageArea imageArea) {
        //Show the image area - 1er pour asynchrone
        imageFragment.showImageArea(imageArea);

        //Change current area
        currentBodyGroup = bodyGroup;
        mainActivity.setActionBarHome(hasPrevious(), currentBodyGroup.getName());

        //Show attitudes of the area
        currentAttitudeFragment = null;
        if (currentBodyGroup.hasAttitudes()) {
            //Création / récuperation du fragment
            currentAttitudeFragment = openAttitudes(currentBodyGroup);

            if(currentAttitudeFragment != null) {
                //Affichage de l'attitude
                currentAttitudeFragment.changeVisibility(true, -1);

                //Guide 1 - Fermeture si des attitudes sont affichées
                if (!synergologyGuide1) {
                    synergologyGuide1 = true;
                    synergologyGuide1Snackbar.dismiss();
                    synergologyGuide1Snackbar = null;
                }
            }
        }

        //Hide all other attitudes
        for (AttitudeFragment attitudeFragment : attitudeFragments.values())
            if (currentAttitudeFragment == null || attitudeFragment != currentAttitudeFragment)
                attitudeFragment.changeVisibility(false, -1);

        //Affichage du guide à la fin de la transition
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //Guide 2 - Ouverture
                if (!synergologyGuide2 && currentBodyGroup.hasAttitudes()) {
                    synergologyGuide2 = true;
                    Snackbar.make(synergologyLayout, R.string.tourguide_synergology_2, Snackbar.LENGTH_INDEFINITE).show();
                    //Guide 2 - Fermeture
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //Guide 3 - Ouverture
                            synergologyGuide3Snackbar = Snackbar.make(synergologyLayout, R.string.tourguide_synergology_3, Snackbar.LENGTH_INDEFINITE);
                            synergologyGuide3Snackbar.show();
                            //Guide 3 - Fermeture
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    synergologyGuide3Snackbar.dismiss();
                                    synergologyGuide3Snackbar = null;
                                }
                            }, 10000);
                        }
                    }, 10000);
                }
            }
        }, Settings.getAnimationSpeed());
    }

    /**
     * Close the current group by updating ActionBar and nulling group & current fragment
     * But not closing the fragment
     */
    private void closeCurrentGroup(){
        currentBodyGroup = null;
        currentAttitudeFragment = null;
        mainActivity.setActionBarHome(hasPrevious(), "");
    }

    /**
     * Create and store the attitude fragment related to the group
     * or return directly the previously created one
     */
    private AttitudeFragment openAttitudes(BodyGroup bodyGroup){
        AttitudeFragment attitudeFragment = null;

        //If the fragment is already created
        if(attitudeFragments.containsKey(bodyGroup))
            attitudeFragment = attitudeFragments.get(bodyGroup);
        else{
            //If we have attitudes
            if (bodyGroup.hasAttitudes()) {
                //Create Fragment
                attitudeFragment = AttitudeFragment.newInstance();
                attitudeFragment.setListener(this);
                //Add fragment
                attitudeFragments.put(bodyGroup, attitudeFragment);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.activity_synergology, attitudeFragment);
                fragmentTransaction.commit();
                fragmentManager.executePendingTransactions();//Fragment onCreateView called here

                View attitudeFragmentView = attitudeFragment.getView();
                if(attitudeFragmentView != null) {
                    //Set size and position
                    attitudeFragmentView.getLayoutParams().width = layoutWidth;
                    attitudeFragmentView.getLayoutParams().height = attitudeFragmentHeight;
                    attitudeFragmentView.setX(0);
                    attitudeFragmentView.setY(layoutHeight);
                    //Init attitudes after view is known
                    attitudeFragment.updateSizes(splitY, layoutHeight);
                    attitudeFragment.init(bodyGroup);
                }
            }
        }

        return attitudeFragment;
    }

    public void onImageLoaded(){
        //Fermeture de l'écran de chargement
        mainActivity.closeSplashscreen();
    }

    /**
     * Close the current group
     */
    @Override
    public void onTouched() {
        //Fragment d'attitudes
        if(currentAttitudeFragment != null)
            currentAttitudeFragment.changeVisibility(false, -1);

        //Close group after changing visiblity of fragment
        closeCurrentGroup();
    }

    /**
     * Called when the fragment is closing
     */
    @Override
    public void onFragmentClosing(BodyGroup bodyGroup) {
        if(bodyGroup == currentBodyGroup)
            closeCurrentGroup();
    }

    /**
     * Inform the user of the location of the attitude in the retlated book
     * @param id The attitude id
     */
    public void onAttitudeClick(int id) {
        /**Attitude attitude = Data.getDataById(attitudes, id);
        if(attitude != null)
            if (attitude.hasBooka_page())
                Tools.toast(mainActivity, String.format(toast_booka, attitude.getBooka_page()));
         */
    }
}