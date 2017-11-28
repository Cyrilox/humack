package com.universio.humack.synergology.data;

import android.database.Cursor;

import com.universio.humack.data.Data;
import com.universio.humack.data.DataAccessObject;
import com.universio.humack.data.DatabaseAO;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 06/05/2015.
 */
public class AttitudeDAO extends DataAccessObject {

    private static final int COL_ID_ID = 0, COL_DESCRIPTION_ID = 1, COL_MEANING_A_ID = 2, COL_MEANING_B_ID = 3, COL_BOOKA_PAGE_ID = 4, COL_SUBORDER_ID = 5, COL_FK_BODYGROUP_ID_ID = 6, COL_FK_MICROMOVEMENT_ID_ID = 7, COL_FK_HEMISPHERE_ID_ID = 8;
    public static final String COL_SUBORDER_NAME = "suborder";
    private ArrayList<BodyGroup> bodyGroups;
    private ArrayList<Micromovement> micromovements;
    private ArrayList<Hemisphere> hemispheres;

    public AttitudeDAO(DatabaseAO databaseAO){
        super(databaseAO, "attitude");
    }

    public void setForeignDatas(ArrayList<BodyGroup> bodyGroups, ArrayList<Micromovement> micromovements, ArrayList<Hemisphere> hemispheres){
        this.bodyGroups = bodyGroups;
        this.micromovements = micromovements;
        this.hemispheres = hemispheres;
    }

    public ArrayList<Attitude> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<Attitude> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((Attitude)data);
        return datasCasted;
    }
    
    public Attitude getObjectFromCursor(Cursor cursor){
        Integer id, bookAPage, suborder;
        String description, meaningA, meaningB;
        id = cursor.getInt(COL_ID_ID);
        description = cursor.isNull(COL_DESCRIPTION_ID) ? null : cursor.getString(COL_DESCRIPTION_ID);
        meaningA = cursor.isNull(COL_MEANING_A_ID) ? null : cursor.getString(COL_MEANING_A_ID);
        meaningB = cursor.isNull(COL_MEANING_B_ID) ? null : cursor.getString(COL_MEANING_B_ID);
        bookAPage = cursor.isNull(COL_BOOKA_PAGE_ID) ? null : cursor.getInt(COL_BOOKA_PAGE_ID);
        suborder = cursor.getInt(COL_SUBORDER_ID);

        BodyGroup bodyGroup = Data.getDataById(bodyGroups, cursor.getInt(COL_FK_BODYGROUP_ID_ID));

        Micromovement micromovement = cursor.isNull(COL_FK_MICROMOVEMENT_ID_ID) ? null : Data.getDataById(micromovements, cursor.getInt(COL_FK_MICROMOVEMENT_ID_ID));

        Hemisphere hemisphere = cursor.isNull(COL_FK_HEMISPHERE_ID_ID) ? null : Data.getDataById(hemispheres, cursor.getInt(COL_FK_HEMISPHERE_ID_ID));

        return new Attitude(id, description, meaningA, meaningB, bookAPage, suborder, bodyGroup, micromovement, hemisphere);
    }
}