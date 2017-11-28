package com.universio.humack;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class AboutActivity extends ActivityFragment {

    private String websiteUrl, testGroupUrl;

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
    public void init() {
        //Website links
        websiteUrl = getResources().getString(R.string.activity_about_officialwebsite_url);
        testGroupUrl = getResources().getString(R.string.activity_about_betagroup_url);

        //Textview Html.fromHtml fix
        View rootView = getView();
        if (rootView != null){
            Tools.fromHtml((ViewGroup) rootView);

            //Click
            ImageButton openReleasenote;
            Button openWebsite, openBetaGroup;
            openReleasenote = (ImageButton) rootView.findViewById(R.id.activity_about_releasenote);
            openWebsite = (Button) rootView.findViewById(R.id.activity_about_officialwebsite);
            openBetaGroup = (Button) rootView.findViewById(R.id.activity_about_betagroup);

            openReleasenote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.showReleasenote(BuildConfig.VERSION_CODE, true);
                }
            });
            openWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tools.openUrl(getActivity(), websiteUrl);
                }
            });
            openBetaGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tools.openUrl(getActivity(), testGroupUrl);
                }
            });
        }

        //Fermeture de l'écran de chargement
        mainActivity.closeSplashscreen();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
