package com.universio.humack;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.universio.humack.data.DatabaseAO;
import com.universio.humack.data.Glossary;
import com.universio.humack.data.GlossaryDAO;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 08/06/2015.
 */
public class GlossaryActivity extends ActivityFragment {

    //Glossaire
    private ArrayList<Glossary> glossarys;

    public static GlossaryActivity newInstance(){
        return new GlossaryActivity();
    }

    /**
     * Called in order to create the view
     * @return The layout resource id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_glossary;
    }

    @Override
    public void init(){
        loadDatabase();
        View rootView = getView();

        LayoutInflater inflater = mainActivity.getLayoutInflater();

        //Table
        TableLayout tableLayout;
        if(rootView != null) {
            tableLayout = (TableLayout) rootView.findViewById(R.id.activity_glossary_table);
            for (Glossary glossary : glossarys) {
                TableRow tableRow = (TableRow) inflater.inflate(R.layout.activity_glossary_row, null);

                TextView termView = (TextView) tableRow.findViewById(R.id.activity_glossary_term);
                termView.setText(glossary.getTerm());
                TextView definitionView = (TextView) tableRow.findViewById(R.id.activity_glossary_definition);
                definitionView.setText(glossary.getDefinition());

                tableLayout.addView(tableRow);
            }
        }

        //Fermeture de l'écran de chargement
        mainActivity.closeSplashscreen();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void loadDatabase(){
        //Glossary
        DatabaseAO databaseAO = MainActivity.getDatabaseAO();
        GlossaryDAO glossaryDAO = new GlossaryDAO(databaseAO);
        databaseAO.open();
        glossarys = glossaryDAO.getAll(GlossaryDAO.COL_TERM_NAME);
        databaseAO.close();
    }
}
