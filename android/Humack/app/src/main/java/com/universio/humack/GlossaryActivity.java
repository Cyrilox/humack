package com.universio.humack;

import android.os.Bundle;
import android.view.LayoutInflater;
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
public class GlossaryActivity extends BaseActivity {

    //Glossaire
    private ArrayList<Glossary> glossarys;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setupActivity(R.layout.activity_glossary, R.drawable.icon_glossary);
        super.onCreate(savedInstanceState);
    }

    protected void init(){
        loadDatabase();

        LayoutInflater inflater = getLayoutInflater();

        //Table
        TableLayout tableLayout = (TableLayout)findViewById(R.id.activity_glossary_table);
        int oddBackgroundColor = getResources().getColor(R.color.table_row_odd_background);
        boolean odd = false;
        for(Glossary glossary : glossarys){
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.activity_glossary_row, null);

            TextView termView = (TextView)tableRow.findViewById(R.id.activity_glossary_term);
            termView.setText(glossary.getTerm());
            TextView definitionView = (TextView)tableRow.findViewById(R.id.activity_glossary_definition);
            definitionView.setText(glossary.getDefinition());

            //Style
            if(odd)
                tableRow.setBackgroundColor(oddBackgroundColor);
            odd = !odd;

            tableLayout.addView(tableRow);
        }
    }

    private void loadDatabase(){
        //Glossary
        DatabaseAO databaseAO = BaseActivity.getDatabaseAO();
        GlossaryDAO glossaryDAO = new GlossaryDAO(databaseAO);
        databaseAO.open();
        glossarys = glossaryDAO.getAll(GlossaryDAO.COL_TERM_NAME);
        databaseAO.close();
    }
}
