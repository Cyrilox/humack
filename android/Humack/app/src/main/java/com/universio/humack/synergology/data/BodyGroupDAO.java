package com.universio.humack.synergology.data;

import android.database.Cursor;

import com.universio.humack.data.Data;
import com.universio.humack.data.DataAccessObject;
import com.universio.humack.data.DatabaseAO;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 18/09/2015.
 */
public class BodyGroupDAO extends DataAccessObject {
    private static final int COL_ID_ID = 0, COL_NAME_ID = 1;

    public BodyGroupDAO(DatabaseAO databaseAO){
        super(databaseAO, "bodygroup");
    }

    public ArrayList<BodyGroup> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<BodyGroup> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((BodyGroup)data);
        return datasCasted;
    }

    public BodyGroup getObjectFromCursor(Cursor cursor){
        int id = cursor.getInt(COL_ID_ID);
        String name = cursor.getString(COL_NAME_ID);

        return new BodyGroup(id, name);
    }
}
