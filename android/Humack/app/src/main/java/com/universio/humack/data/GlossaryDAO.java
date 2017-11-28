package com.universio.humack.data;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 06/05/2015.
 */
public class GlossaryDAO extends DataAccessObject {

    private static final int COL_ID_ID = 0, COL_TERM_ID = 1, COL_DEFINITION_ID = 2;
    public static final String COL_TERM_NAME = "term";

    public GlossaryDAO(DatabaseAO databaseAO){
        super(databaseAO, "glossary");
    }

    public ArrayList<Glossary> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<Glossary> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((Glossary)data);
        return datasCasted;
    }

    public Glossary getObjectFromCursor(Cursor cursor){
        int id;
        String term, definition;
        id = cursor.getInt(COL_ID_ID);
        term = cursor.getString(COL_TERM_ID);
        definition = cursor.getString(COL_DEFINITION_ID);

        return new Glossary(id, term, definition);
    }
}