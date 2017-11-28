package com.universio.humack.synergology.data;

import android.database.Cursor;

import com.universio.humack.data.Data;
import com.universio.humack.data.DataAccessObject;
import com.universio.humack.data.DatabaseAO;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 25/04/2015.
 */
public class MicromovementDAO extends DataAccessObject {

    private static final int COL_ID_ID = 0, COL_TITLE_ID = 1;

    public MicromovementDAO(DatabaseAO databaseAO){
        super(databaseAO, "micromovement");
    }

    public ArrayList<Micromovement> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<Micromovement> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((Micromovement)data);
        return datasCasted;
    }

    public Micromovement getObjectFromCursor(Cursor cursor){
        int id = cursor.getInt(COL_ID_ID);
        String title = cursor.getString(COL_TITLE_ID);

        return new Micromovement(id, title);
    }
}