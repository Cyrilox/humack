package com.universio.humack.data;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 28/12/2015.
 */
public class AppversionDAO extends DataAccessObject {

    private static final int COL_ID_ID = 0, COL_CODE_ID = 1, COL_NAME_ID = 2, COL_CHANGES_ID = 3;
    public static final String COL_CODE_NAME = "code";

    public AppversionDAO(DatabaseAO databaseAO){
        super(databaseAO, "appversion");
    }

    public ArrayList<Appversion> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<Appversion> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((Appversion)data);
        return datasCasted;
    }

    public Appversion getObjectFromCursor(Cursor cursor){
        int id, code;
        String name, changes;
        id = cursor.getInt(COL_ID_ID);
        code = cursor.getInt(COL_CODE_ID);
        name = cursor.getString(COL_NAME_ID);
        changes = cursor.isNull(COL_CHANGES_ID) ? null : cursor.getString(COL_CHANGES_ID);

        return new Appversion(id, code, name, changes);
    }
}