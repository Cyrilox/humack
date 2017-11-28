package com.universio.humack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.universio.humack.data.Appversion;
import com.universio.humack.data.AppversionDAO;
import com.universio.humack.data.DatabaseAO;
import com.universio.humack.synergology.SynergologyActivity;
import com.universio.humack.synergology.SynergologyHelpActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;

/**
 * Created by Cyril Humbertclaude on 15/06/2015.
 */
public class MainActivity extends AppCompatActivity implements View.OnLayoutChangeListener, NavigationView.OnNavigationItemSelectedListener{

    //Statiques
    public static String app_name;
    public static int DEFAULT_SPACING;
    public static AssetManager ASSET_MANAGER;
    private static DatabaseAO databaseAO;
    private static Settings settings;

    //Données
    private ArrayList<Appversion> appversions;

    //Splashscreen
    private SplashscreenFragment splashscreen;
    private boolean splashscreenOn=false;
    private long splashStayStartTime;
    private Handler autoCloseSplashScreen = null;

    //Barre d'action
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Hashtable<Integer, Boolean> homeUps;
    private Hashtable<Integer, String> homeTitlesDefault, homeTitles;

    //Vue
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu navigationViewMenu;
    private int frameWidth, frameHeight;
    private FrameLayout activitiesFrame;
    private FragmentManager fragmentManager;
    private DeadareaFragment deadareaFragment;
    private View deadareaView;

    //Activités
    private int currentActivityId;
    private ActivityFragment currentActivity;
    private HashMap<Integer, ActivityFragment> activities;
    private Stack<Integer> activitiesBackstack;
    //Position dans le menu = ID
    public static final int ACTIVITY_SYNERGOLOGY = R.id.drawer_menu_synergology, ACTIVITY_GLOSSARY = R.id.drawer_menu_glossary,
            ACTIVITY_SETTINGS = R.id.drawer_menu_settings, ACTIVITY_ABOUT = R.id.drawer_menu_about, ACTIVITY_SYNERGOLOGY_HELP = R.id.activity_synergology_help;
    private static final int ACTIVITIES_COUNT=5, MAIN_ACTIVITY=ACTIVITY_SYNERGOLOGY;
    private boolean animationRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Fragments
        fragmentManager = getFragmentManager();
        //Page de chargement
        showSplashscreen();
        //Initialisation
        init();
        //Activité par défault
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                showActivity(MAIN_ACTIVITY, true);
            }
        });
        //App version and release note
        updateAppVersion();
    }

    private void init(){
        //Statiques
        app_name = getResources().getString(R.string.app_name);
        DEFAULT_SPACING = (int)getResources().getDimension(R.dimen.default_spacing);
        ASSET_MANAGER = getAssets();
        settings = new Settings(getSharedPreferences("user_preferences", 0), getResources());

        //Database
        databaseAO = DatabaseAO.getInstance(this.getApplicationContext());
        databaseAO.createDatabase();
        loadDatabase();

        //Vues
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.addOnLayoutChangeListener(this);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewMenu = navigationView.getMenu();
        activitiesFrame = (FrameLayout)findViewById(R.id.activities_frame);

        //ActionBar & Drawer Handling
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        //Barre d'action
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
            toolbar.setNavigationContentDescription(R.string.action_bar_home);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOptionsItemSelected(null);//Home or Up click
                }
            });

            actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }

        //ActionBar
        homeUps = new Hashtable<>();
        homeTitlesDefault = new Hashtable<>();
        for(int i=0; i<ACTIVITIES_COUNT; i++){
            int activityId = -1;
            switch(i){
                case 0: activityId = ACTIVITY_SYNERGOLOGY;
                    homeTitlesDefault.put(activityId, getString(R.string.title_activity_synergology)); break;
                case 1: activityId = ACTIVITY_GLOSSARY;
                    homeTitlesDefault.put(activityId, getString(R.string.title_activity_glossary)); break;
                case 2: activityId = ACTIVITY_SETTINGS;
                    homeTitlesDefault.put(activityId, getString(R.string.title_activity_settings)); break;
                case 3: activityId = ACTIVITY_ABOUT;
                    homeTitlesDefault.put(activityId, getString(R.string.title_activity_about)); break;
                case 4: activityId = ACTIVITY_SYNERGOLOGY_HELP;
                    homeTitlesDefault.put(activityId, getString(R.string.title_activity_synergology_help)); break;
                default: Log.w("_MainActivity", ".init| i " + i + " is not mapped.");
            }
            if(activityId != -1)
                homeUps.put(activityId, false);
        }
        homeTitles = new Hashtable<>(homeTitlesDefault);

        //Activités
        currentActivityId = -1;
        currentActivity = null;
        activities = new HashMap<>();
        activitiesBackstack = new Stack<>();
        animationRunning = false;

        //Dead zone on top of activities
        deadareaFragment = DeadareaFragment.newInstance();
        fragmentManager.beginTransaction().add(R.id.activities_frame, deadareaFragment).commit();
        fragmentManager.executePendingTransactions();//Fragment onCreateView called here
    }

    @SuppressWarnings("unchecked")
    private void loadDatabase(){
        //Accès aux données
        AppversionDAO appversionDAO = new AppversionDAO(databaseAO);

        //Récuperation des données
        databaseAO.open();

        appversions = appversionDAO.getAll(AppversionDAO.COL_CODE_NAME);

        databaseAO.close();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        frameWidth = activitiesFrame.getMeasuredWidth();
        frameHeight = activitiesFrame.getMeasuredHeight();
    }

    public void showActivity(int activityId, boolean instant){
        if(!animationRunning && currentActivityId != activityId) {
            animationRunning = true;

            //Récuperation ou création de l'activité
            ActivityFragment activity = activities.get(activityId);
            if (activity == null) {
                switch(activityId){
                    case ACTIVITY_SYNERGOLOGY:
                        activity = SynergologyActivity.newInstance();
                        break;
                    case ACTIVITY_SYNERGOLOGY_HELP:
                        activity = SynergologyHelpActivity.newInstance();
                        break;
                    case ACTIVITY_GLOSSARY:
                        activity = GlossaryActivity.newInstance();
                        break;
                    case ACTIVITY_SETTINGS:
                        activity = SettingsActivity.newInstance();
                        break;
                    case ACTIVITY_ABOUT:
                        activity = AboutActivity.newInstance();
                        break;
                    default:
                        Log.w("_MainActivity", ".showActivity| activityId " + activityId + " is not existing.");
                }
                if (activity != null) {
                    //Save the activity
                    activities.put(activityId, activity);
                    fragmentManager.beginTransaction().add(R.id.activities_frame, activity).commit();
                    fragmentManager.executePendingTransactions();//Fragment onCreateView called here
                    //Init the Fragment
                    activity.init();
                    //Set size to full screen and hide at the left
                    View view = activity.getView();
                    if (view != null) {
                        //Tailles
                        view.getLayoutParams().width = frameWidth;
                        view.getLayoutParams().height = frameHeight;
                        //Position en dehors de l'écran
                        switch (activity.getOffscreenLocation()){
                            case ActivityFragment.OFFSCREEN_LOCATION_TOP:
                                view.setX(0);
                                view.setY(-frameHeight);
                                break;
                            case ActivityFragment.OFFSCREEN_LOCATION_RIGHT:
                                view.setX(frameWidth);
                                view.setY(0);
                                break;
                            case ActivityFragment.OFFSCREEN_LOCATION_BOTTOM:
                                view.setX(0);
                                view.setY(frameHeight);
                                break;
                            default:
                                view.setX(-frameWidth);
                                view.setY(0);
                        }
                    }
                }
            }

            //Affichage et masquage des activités
            if (activity != null) {
                AnimatorSet animations;
                ObjectAnimator translatePrevious, translateNextX, translateNextY;
                View activityView, currentActivityView;

                //Animation
                animations = new AnimatorSet();
                animations.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        animationRunning = false;
                        if(!splashscreenOn)
                            currentActivity.onVisible();
                    }
                });

                //SHOW new from offscreen
                activityView = activity.getView();
                //Premier plan
                if(activityView != null)
                    activityView.bringToFront();
                //Translation
                translateNextX = ObjectAnimator.ofFloat(activityView, "translationX", 0);
                translateNextY = ObjectAnimator.ofFloat(activityView, "translationY", 0);
                translateNextX.setDuration(instant ? 0 : Settings.getAnimationDrawerSpeed());
                translateNextY.setDuration(instant ? 0 : Settings.getAnimationDrawerSpeed());
                //Ajout à l'animation
                animations.play(translateNextX);
                animations.play(translateNextY);

                //HIDE current to offscreen
                if (currentActivity != null) {
                    currentActivityView = currentActivity.getView();
                    //Translate
                    switch (currentActivity.getOffscreenLocation()){
                        case ActivityFragment.OFFSCREEN_LOCATION_TOP:
                            translatePrevious = ObjectAnimator.ofFloat(currentActivityView, "translationY", -frameHeight);
                            break;
                        case ActivityFragment.OFFSCREEN_LOCATION_RIGHT:
                            translatePrevious = ObjectAnimator.ofFloat(currentActivityView, "translationX", frameWidth);
                            break;
                        case ActivityFragment.OFFSCREEN_LOCATION_BOTTOM:
                            translatePrevious = ObjectAnimator.ofFloat(currentActivityView, "translationY", frameHeight);
                            break;
                        default:
                            translatePrevious = ObjectAnimator.ofFloat(currentActivityView, "translationX", -frameWidth);
                    }
                    translatePrevious.setDuration(instant ? 0 : Settings.getAnimationDrawerSpeed());
                    //Ajout à l'animation
                    animations.play(translatePrevious);

                    currentActivity.onHiding();
                }

                animations.setInterpolator(new AccelerateDecelerateInterpolator());

                //Start animation
                animations.start();

                //Update state
                currentActivityId = activityId;
                currentActivity = activity;
                setActionBarHome();
                invalidateOptionsMenu();
                //Rustine
                int currentActivityMenuId = (currentActivityId == ACTIVITY_SYNERGOLOGY_HELP)  ? ACTIVITY_SYNERGOLOGY : currentActivityId;
                navigationViewMenu.findItem(currentActivityMenuId).setChecked(true);

                activitiesBackstack.removeElement(activityId);
                activitiesBackstack.push(activityId);
            }
            //Zone morte
            if(deadareaView == null)
                deadareaView = deadareaFragment.getView();
            if(deadareaView != null)
                deadareaView.bringToFront();
        }
    }

    private void showSplashscreen(){
        if(!splashscreenOn) {
            splashscreenOn = true;
            //Création et ajout
            splashscreen = new SplashscreenFragment();
            fragmentManager.beginTransaction().add(R.id.splashscreen_layout, splashscreen).commit();

            //Animation d'apparition
            long fadeInTime = (long) getResources().getInteger(R.integer.splashscreen_fadein);
            splashStayStartTime = System.currentTimeMillis() + fadeInTime;
            View view = splashscreen.getView();
            if (view != null) {
                ImageView icon = (ImageView) view.findViewById(R.id.fragment_splashscreen_icon);
                AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                fadeIn.setDuration(fadeInTime);
                fadeIn.setFillAfter(true);
                icon.startAnimation(fadeIn);
            }

            //Close it after 7 second in case of an error of the Activity
            if(autoCloseSplashScreen != null)
                autoCloseSplashScreen.removeCallbacksAndMessages(null);
            autoCloseSplashScreen = new Handler();
            autoCloseSplashScreen.postDelayed(new Runnable() {
                public void run() {
                    closeSplashscreen();
                }
            }, 7000);
        }
    }

    //The first ActivityFragment must call this method in order to close the splashscreen
    public void closeSplashscreen(){
        if(splashscreenOn) {
            //Autoclose ending
            if(autoCloseSplashScreen != null) {
                autoCloseSplashScreen.removeCallbacksAndMessages(null);
                autoCloseSplashScreen = null;
            }
            //Temps minimum visible
            long stayTime, minStayTime, fadeOutDelay;
            stayTime = System.currentTimeMillis() - splashStayStartTime;
            minStayTime = (long) getResources().getInteger(R.integer.splashscreen_minstay);
            fadeOutDelay = Math.max(0, minStayTime - stayTime)+100;

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //Animation de disparition
                    long fadeOutTime = (long) getResources().getInteger(R.integer.splashscreen_fadeout);
                    View view = splashscreen.getView();
                    if (view != null) {
                        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                        fadeOut.setDuration(fadeOutTime);
                        fadeOut.setFillAfter(true);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //Effacement du fragment
                                fragmentManager.beginTransaction().remove(splashscreen).commit();
                                fragmentManager.executePendingTransactions();
                                splashscreen = null;
                                splashscreenOn = false;
                                currentActivity.onVisible();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        view.startAnimation(fadeOut);
                    }
                }
            }, fadeOutDelay);
        }
    }

    //Update the current app version and show the release note dialog a single time
    private void updateAppVersion() {
        int lastVersion, currentVersion;
        lastVersion = Settings.getLastVersion();
        currentVersion = BuildConfig.VERSION_CODE;
        if (lastVersion != currentVersion) {
            if(currentVersion > lastVersion)
                showReleasenote(lastVersion+1, false);
            Settings.setLastVersion(currentVersion);
        }
    }

    public void showReleasenote(int fromVersion, boolean showAppversion){
        //Changements
        String appversionChanges = "";
        boolean firstAdd = true;
        for(Appversion appversion : appversions)
            if(appversion.hasChanges() && appversion.getCode() >= fromVersion) {
                if(!firstAdd)
                    appversionChanges += Tools.getLineSeparator();
                appversionChanges += appversion.getChanges();
                firstAdd = false;
            }

        //Dialogue
        ReleasenoteDialog releasenoteDialog = ReleasenoteDialog.newInstance();
        if(!appversionChanges.equals(""))
            releasenoteDialog.setAppversionChanges(appversionChanges);
        fragmentManager.beginTransaction().add(R.id.releasenote_layout, releasenoteDialog).commit();
        fragmentManager.executePendingTransactions();
        releasenoteDialog.showDialog(showAppversion);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentActivity != null && currentActivity.hasOptionsMenu()) {
            getMenuInflater().inflate(currentActivity.getOptionsMenu(), menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return currentActivity != null && currentActivity.hasOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(currentActivity == null || !currentActivity.onOptionsItemSelected(item))
            toggleDrawer();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!closeDrawer() && !activitiesBackstack.empty() && !animationRunning && !currentActivity.onBackPressed()) {
            if (activitiesBackstack.size() > 1) {
                activitiesBackstack.pop();
                showActivity(activitiesBackstack.peek(), false);
            } else if (activitiesBackstack.peek() != MAIN_ACTIVITY) {
                activitiesBackstack.pop();
                showActivity(MAIN_ACTIVITY, false);
            }
        }
    }

    private boolean closeDrawer(){
        if(drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
            return true;
        }else
            return false;
    }

    private void toggleDrawer(){
        if(drawerLayout.isDrawerOpen(navigationView))
            drawerLayout.closeDrawer(navigationView);
        else
            drawerLayout.openDrawer(navigationView);
    }

    public void setActionBarHome(){
        setActionBarHome(homeUps.get(currentActivityId), homeTitles.get(currentActivityId));
    }

    public void setActionBarHome(boolean upEnabled, String title) {
        actionBar.setDisplayHomeAsUpEnabled(upEnabled);
        if(!upEnabled)
            toolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        this.homeUps.put(currentActivityId, upEnabled);
        if (title != null){
            if(title.equals(""))
                title = this.homeTitlesDefault.get(currentActivityId);
            toolbar.setTitle(title);
            this.homeTitles.put(currentActivityId, title);
        }else
            toolbar.setTitle(app_name);
    }

    public static DatabaseAO getDatabaseAO() {
        return databaseAO;
    }

    @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
        if(!animationRunning) {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            showActivity(menuItem.getItemId(), false);
            return true;
        }else
            return false;
    }
}
