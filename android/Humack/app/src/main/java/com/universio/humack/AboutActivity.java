package com.universio.humack;

import android.os.Bundle;
import android.view.ViewGroup;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupActivity(R.layout.activity_about, R.drawable.icon_info);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        //Textview Html.fromHtml fix
        ViewGroup rootViewGroup = (ViewGroup)findViewById(R.id.activity_about);
        Tools.fromHtml(rootViewGroup);
    }
}
