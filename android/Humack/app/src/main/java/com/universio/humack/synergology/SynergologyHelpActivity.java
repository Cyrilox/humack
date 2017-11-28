package com.universio.humack.synergology;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.universio.humack.BaseActivity;
import com.universio.humack.R;
import com.universio.humack.Tools;

/**
 * Created by Cyril Humbertclaude on 08/05/2015.
 */
public class SynergologyHelpActivity extends BaseActivity {
    private String websiteUrl, booksUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupActivity(R.layout.activity_synergology_help, R.drawable.icon_synergology);
        super.onCreate(savedInstanceState);
    }

    protected void init(){
        setActionBarHome(true, null);
        //Website links
        websiteUrl = getResources().getString(R.string.synergology_help_learnmore_website_url);
        booksUrl = getResources().getString(R.string.synergology_help_learnmore_books_url);
        //Textview Html.fromHtml fix
        ViewGroup rootViewGroup = (ViewGroup)findViewById(R.id.activity_synergology_help);
        Tools.fromHtml(rootViewGroup);
    }

    public void openWebsite(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
        startActivity(browserIntent);
    }

    public void openBooks(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(booksUrl));
        startActivity(browserIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!closeDrawer())
            finish();
        return true;
    }
}
