package com.universio.humack;

import android.view.View;
import android.view.ViewGroup;


public class AboutActivity extends ActivityFragment {

    public static AboutActivity newInstance(){
        return new AboutActivity();
    }

    /**
     * Called in order to create the view
     * @return The layout resource id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_about;
    }

    @Override
    public void init(){
        View rootView = getView();
        //Textview Html.fromHtml fix
        if(rootView != null)
            Tools.fromHtml((ViewGroup)rootView);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
