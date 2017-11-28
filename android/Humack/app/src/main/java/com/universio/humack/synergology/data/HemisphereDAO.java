package com.universio.humack.synergology.data;

import android.database.Cursor;

import com.universio.humack.data.Data;
import com.universio.humack.data.DataAccessObject;
import com.universio.humack.data.DatabaseAO;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 03/11/2015.
 */
public class HemisphereDAO extends DataAccessObject {

    private static final int COL_ID_ID = 0, COL_NAME_ID = 1;

    public HemisphereDAO(DatabaseAO databaseAO){
        super(databaseAO, "hemisphere");
    }

    public ArrayList<Hemisphere> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<Hemisphere> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((Hemisphere)data);
        return datasCasted;
    }

    public Hemisphere getObjectFromCursor(Cursor cursor){
        int id = cursor.getInt(COL_ID_ID);
        String name = cursor.getString(COL_NAME_ID);

        return new Hemisphere(id, name);
    }
}