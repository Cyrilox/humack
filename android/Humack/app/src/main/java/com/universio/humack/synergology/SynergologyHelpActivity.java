package com.universio.humack.synergology;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.universio.humack.ActivityFragment;
import com.universio.humack.R;
import com.universio.humack.Tools;

/**
 * Created by Cyril Humbertclaude on 08/05/2015.
 */
public class SynergologyHelpActivity extends ActivityFragment {
    private String websiteUrl, booksUrl;

    public static SynergologyHelpActivity newInstance(){
        return new SynergologyHelpActivity();
    }

    /**
     * Called in order to create the view
     * @return The layout resource id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_synergology_help;
    }

    @Override
    public void init(){
        //Website links
        websiteUrl = getResources().getString(R.string.synergology_help_learnmore_website_url);
        booksUrl = getResources().getString(R.string.synergology_help_learnmore_books_url);
        //Textview Html.fromHtml fix
        View rootView = getView();
        if(rootView != null) {
            Tools.fromHtml((ViewGroup) rootView);
            //Click
            Button openWebsite, openBooks;
            openWebsite = (Button)rootView.findViewById(R.id.activity_synergology_help_website);
            openBooks = (Button)rootView.findViewById(R.id.activity_synergology_help_books);
            openWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebsite(view);
                }
            });
            openBooks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBooks(view);
                }
            });
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void openWebsite(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
        startActivity(browserIntent);
    }

    public void openBooks(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(booksUrl));
        startActivity(browserIntent);
    }
}
