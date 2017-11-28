package com.universio.humack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewCompat;
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
import android.widget.FrameLayout;

import com.universio.humack.data.DatabaseAO;
import com.universio.humack.synergology.SynergologyActivity;
import com.universio.humack.synergology.SynergologyHelpActivity;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnLayoutChangeListener, NavigationView.OnNavigationItemSelectedListener{

    //Statiques
    public static String app_name;
    public static int DEFAULT_SPACING;
    private static float ELEVATION_ACTIVITY, ELEVATION_ACTIVITY_ABOVE;
    public static AssetManager ASSET_MANAGER;
    private static DatabaseAO databaseAO;
    private static Settings settings;

    //Barre d'action
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Hashtable<Integer, Boolean> homeUps;
    private Hashtable<Integer, String> homeTitlesDefault, homeTitles;

    //Vue
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int layoutWidth, layoutHeight, frameWidth, frameHeight;
    private FrameLayout activitiesFrame;
    private FragmentManager fragmentManager;

    //Menu
    private boolean drawerOpen;

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
        init();
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                showActivity(MAIN_ACTIVITY, true);
            }
        });
    }

    private void init(){
        //Statiques
        app_name = getResources().getString(R.string.app_name);
        DEFAULT_SPACING = (int)getResources().getDimension(R.dimen.default_spacing);
        ELEVATION_ACTIVITY = getResources().getDimension(R.dimen.elevation_activity);
        ELEVATION_ACTIVITY_ABOVE = ELEVATION_ACTIVITY + getResources().getDimension(R.dimen.elevation_default_spacing);
        ASSET_MANAGER = getAssets();
        databaseAO = DatabaseAO.getInstance(this.getApplicationContext());
        databaseAO.createDatabase();
        settings = new Settings(getSharedPreferences("user_preferences", 0), getResources());

        //Navigation
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.addOnLayoutChangeListener(this);
        drawerOpen = false;
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        activitiesFrame = (FrameLayout)findViewById(R.id.activities_frame);

        //ActionBar & Drawer Handling
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarHandler(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        //Barre d'action
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_black);
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
        fragmentManager = getFragmentManager();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        layoutWidth = drawerLayout.getMeasuredWidth();
        layoutHeight = drawerLayout.getMeasuredHeight();
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
                        view.getLayoutParams().width = frameWidth;
                        view.getLayoutParams().height = frameHeight;
                        view.setX(-frameWidth);
                        view.setY(0);
                    }
                }
            }

            //Affichage et masquage des activités
            if (activity != null) {
                AnimatorSet animations;
                ObjectAnimator translatePrevious, hidePrevious, viewNext, translateNext;
                View activityView, currentActivityView;

                //Animation
                animations = new AnimatorSet();
                animations.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(currentActivity.getView(), ELEVATION_ACTIVITY);
                        animationRunning = false;
                    }
                });

                //Show new from the left
                activityView = activity.getView();
                if(activityView != null) {
                    activityView.bringToFront();
                    ViewCompat.setElevation(activityView, ELEVATION_ACTIVITY_ABOVE);
                }
                viewNext = ObjectAnimator.ofInt(activityView, "visibility", View.VISIBLE);
                viewNext.setDuration(0);
                translateNext = ObjectAnimator.ofFloat(activityView, "translationX", -frameWidth, 0);
                translateNext.setDuration(instant ? 0 : Settings.getAnimationDrawerSpeed());
                animations.play(viewNext);
                animations.play(translateNext).after(viewNext);
                //Hide current to the left
                if (currentActivity != null) {
                    currentActivityView = currentActivity.getView();
                    ViewCompat.setElevation(currentActivityView, ELEVATION_ACTIVITY);

                    translatePrevious = ObjectAnimator.ofFloat(currentActivityView, "translationX", -frameWidth);
                    translatePrevious.setDuration(instant ? 0 : Settings.getAnimationDrawerSpeed());
                    hidePrevious = ObjectAnimator.ofInt(currentActivityView, "visibility", View.GONE);
                    hidePrevious.setDuration(0);
                    animations.play(translatePrevious);
                    animations.play(hidePrevious).after(translatePrevious);
                }

                animations.setInterpolator(new AccelerateDecelerateInterpolator());

                //Start animation
                animations.start();

                //Update state
                currentActivityId = activityId;
                currentActivity = activity;
                setActionBarHome();
                invalidateOptionsMenu();

                activitiesBackstack.removeElement(activityId);
                activitiesBackstack.push(activityId);
            }
        }
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
            toolbar.setNavigationIcon(R.drawable.ic_menu_black);
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

    /*** Classes & Interface ***/
    private class ActionBarHandler extends ActionBarDrawerToggle{

        public ActionBarHandler(Activity activity, DrawerLayout activityMain, int drawerOpen, int drawerClose){
            super(activity, activityMain, drawerOpen, drawerClose);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            drawerOpen = false;
            super.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            drawerOpen = true;
            super.onDrawerOpened(drawerView);
        }
    }
}
