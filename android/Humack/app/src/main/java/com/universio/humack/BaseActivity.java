package com.universio.humack;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.universio.humack.data.DatabaseAO;
import com.universio.humack.synergology.SynergologyActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cyril Humbertclaude on 04/05/2015.
 */
public abstract class BaseActivity extends Activity implements ListView.OnItemClickListener{
    //Shared
    private static boolean isSharedInitialized = false;
    public static int DEFAULT_SPACING;

    private static DatabaseAO databaseAO;

    private static ArrayList<Intent> activitiesIntents;
    private static int newPosition;
    private static boolean startActivity;

    private static int[] menuItemsIcon;
    private static String[] menuItemsTitle;
    private static SimpleAdapter menuAdapter;
    private static int menuItemSelectedColor;

    //Unshared
    private boolean initialized = false;
    private boolean upEnabled;
    private int defaultIcon, position;
    private String defaultTitle, title;
    private ActionBar actionBar;

    private int activityLayoutID;

    private DrawerLayout drawerLayout;
    private LinearLayout drawer;
    private ListView drawerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //Shared initialization
        if(!initialized){
            if (!isSharedInitialized){
                initShared();
                isSharedInitialized = true;
            }
            //Unshared initialization
            this.initUnshared();
            //Specific initializations
            init();
            initialized = true;
        }
    }

    protected void setupActivity(int activityLayoutID, int defaultIcon){
        this.activityLayoutID = activityLayoutID;
        this.defaultIcon = defaultIcon;
    }

    private void initShared(){
        //Base de données
        databaseAO = new DatabaseAO(this.getApplicationContext());
        databaseAO.createDatabase();

        //General
        DEFAULT_SPACING = (int)getResources().getDimension(R.dimen.default_spacing);
        menuItemsIcon = new int[]{R.drawable.icon_synergology, R.drawable.icon_glossary, R.drawable.icon_settings, R.drawable.icon_info};
        activitiesIntents = new ArrayList<>();
        activitiesIntents.add(new Intent(this, SynergologyActivity.class));
        activitiesIntents.add(new Intent(this, GlossaryActivity.class));
        activitiesIntents.add(new Intent(this, SettingsActivity.class));
        activitiesIntents.add(new Intent(this, AboutActivity.class));
        startActivity = false;

        //Menu
        menuItemsTitle = getResources().getStringArray(R.array.drawer_menu_titles);
        menuAdapter = getMenuAdapter();
        menuItemSelectedColor = getResources().getColor(R.color.drawer_menu_item_band);

        //Current drawer position
        newPosition = 0;
    }

    private void initUnshared(){
        //ActionBar
        upEnabled = false;
        defaultTitle = (String)getTitle();
        title = defaultTitle;
        actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }

        //Drawer
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer = (LinearLayout)findViewById(R.id.drawer);
        FrameLayout contentFrame = (FrameLayout)findViewById(R.id.content_frame);

        //ActionBar & Drawer Handling
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarHandler(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //Menu
        position = newPosition;
        drawerMenu = (ListView) findViewById(R.id.drawer_menu);
        drawerMenu.setAdapter(menuAdapter);
        drawerMenu.post(new Runnable() {
            @Override
            public void run() {
                selectDrawerItem();
            }
        });
        drawerMenu.setOnItemClickListener(this);

        //Create and add the activity layout to the base layout
        getLayoutInflater().inflate(activityLayoutID, contentFrame);
    }

    protected abstract void init();

    private SimpleAdapter getMenuAdapter(){
        //Menu
        int[] to = {R.id.drawer_menu_item_icon, R.id.drawer_menu_item_title};
        String[] from = {"drawer_menu_item_icon","drawer_menu_item_title"};

        List<HashMap<String,String>> items = new ArrayList<>();
        for(int i=0; i< menuItemsTitle.length; i++){
            HashMap<String, String> item = new HashMap<>();
            item.put(from[0], Integer.toString(menuItemsIcon[i]));
            item.put(from[1], menuItemsTitle[i]);
            items.add(item);
        }

        return new SimpleAdapter(this, items, R.layout.drawer_menu_item, from, to);
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    /*** ActionBar Menu ***/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            toggleDrawer();
            return true;
        }else
            return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items & viceversa
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawer);

        //Home elements: Up, icon and title
        if(drawerOpen){
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setIcon(R.drawable.app_icon);
            actionBar.setTitle(R.string.app_name);
        }else{
            actionBar.setDisplayHomeAsUpEnabled(upEnabled);
            actionBar.setIcon(defaultIcon);
            actionBar.setTitle(title);
        }

        //Menu items
        for(int i=0; i<menu.size(); i++)
            menu.getItem(i).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    /*** Navigation ***/

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        if(this.position != position) {
            newPosition = position;
            startActivity = true;
        }
        drawerLayout.closeDrawer(drawer);
    }

    @Override
    public void onBackPressed() {
        if(!closeDrawer())
            super.onBackPressed();
    }

    protected boolean closeDrawer(){
        if(drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
            return true;
        }else
            return false;
    }

    private void toggleDrawer(){
        if(drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);
        else
            drawerLayout.openDrawer(drawer);
    }

    /*** Get's Set's Has's ***/

    public static DatabaseAO getDatabaseAO() {
        return databaseAO;
    }

    private void selectDrawerItem(){
        View item, itemBand;
        for(int i=0; i<drawerMenu.getCount(); i++){
            item = drawerMenu.getChildAt(i);
            if(item != null) {
                itemBand = item.findViewById(R.id.drawer_menu_item_band);
                if (i == position)
                    itemBand.setBackgroundColor(menuItemSelectedColor);
                else
                    itemBand.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    protected void setActionBarHome(boolean upEnabled, String title){
        this.upEnabled = upEnabled;
        actionBar.setDisplayHomeAsUpEnabled(upEnabled);
        if(title != null) {
            this.title = title;
            actionBar.setTitle(title);
        }else {
            this.title = defaultTitle;
            actionBar.setTitle(defaultTitle);
        }
    }

    /*** Classes & Interface ***/

    private class ActionBarHandler extends ActionBarDrawerToggle{

        public ActionBarHandler(Activity activity, DrawerLayout drawerLayout, int drawerOpen, int drawerClose){
            super(activity, drawerLayout, drawerOpen, drawerClose);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            //Launch and activity
            if(startActivity) {
                startActivity = false;
                startActivity(activitiesIntents.get(newPosition));
            }else{//Or restore the menu
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            super.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            super.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
        }
    }
}
