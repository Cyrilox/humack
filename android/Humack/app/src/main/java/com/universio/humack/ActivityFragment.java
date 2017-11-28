package com.universio.humack;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Cyril Humbertclaude on 08/07/2015.
 */
abstract public class ActivityFragment extends Fragment{
    protected MainActivity mainActivity = null;
    public static final int OFFSCREEN_LOCATION_LEFT = 0, OFFSCREEN_LOCATION_TOP = 1,OFFSCREEN_LOCATION_RIGHT = 2,OFFSCREEN_LOCATION_BOTTOM = 3;
    protected int optionsMenu = -1, offscreenLocation = OFFSCREEN_LOCATION_LEFT;

    /**
     * Called in order to create the view
     * @return The layout resource id
     */
    protected abstract int getLayoutResource();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mainActivity = (MainActivity)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be attached to MainActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainActivity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(), container, false);
    }

    /**
     * Called after onCreateView, when UI is created
     */
    abstract public void init();

    /**
     * Called each time the ActivityFragment is visible, after transition is finished
     */
    public void onVisible(){}

    /**
     * Called each time the ActivityFragment start to be hidden, before transition
     */
    public void onHiding(){}

    /**
     * Same method used in Activity but here to be dispatched into Fragment
     * @return True if back has been handled, thus to do nothing after that method
     */
    abstract public boolean onBackPressed();

    public int getOptionsMenu() {
        return optionsMenu;
    }

    protected void setOptionsMenu(int optionsMenu) {
        this.optionsMenu = optionsMenu;
    }

    public boolean hasOptionsMenu(){
        return optionsMenu!=-1;
    }

    public int getOffscreenLocation() {
        return offscreenLocation;
    }
}
