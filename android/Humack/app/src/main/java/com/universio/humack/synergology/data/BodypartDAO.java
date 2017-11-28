package com.universio.humack.synergology.data;

import android.database.Cursor;

import com.universio.humack.data.Data;
import com.universio.humack.data.DataAccessObject;
import com.universio.humack.data.DatabaseAO;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 04/05/2015.
 */
public class BodypartDAO extends DataAccessObject {

    private static final int COL_ID_ID = 0, COL_NAME_ID = 1;

    public BodypartDAO(DatabaseAO databaseAO){
        super(databaseAO, "bodypart");
    }

    public ArrayList<Bodypart> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<Bodypart> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((Bodypart)data);
        return datasCasted;
    }

    public Bodypart getObjectFromCursor(Cursor cursor){
        int id;
        String name;
        id = cursor.getInt(COL_ID_ID);
        name = cursor.getString(COL_NAME_ID);

        return new Bodypart(id, name);
    }
}