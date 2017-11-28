package com.universio.humack.synergology.data;

import android.database.Cursor;

import com.universio.humack.data.Data;
import com.universio.humack.data.DataAccessObject;
import com.universio.humack.data.DatabaseAO;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 06/05/2015.
 */
public class AttitudeTypeDAO extends DataAccessObject {

    private static final int COL_ID_ID = 0, COL_NAME_ID = 1;

    public AttitudeTypeDAO(DatabaseAO databaseAO){
        super(databaseAO, "attitude_type");
    }

    public ArrayList<AttitudeType> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<AttitudeType> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((AttitudeType)data);
        return datasCasted;
    }

    public AttitudeType getObjectFromCursor(Cursor cursor){
        int id;
        String name;
        id = cursor.getInt(COL_ID_ID);
        name = cursor.getString(COL_NAME_ID);

        return new AttitudeType(id, name);
    }
}