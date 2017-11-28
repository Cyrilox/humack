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

    private static final int COL_ID_ID = 0, COL_DESCRIPTION_ID = 1, COL_MEANING_ID = 2, COL_BOOKA_PAGE_ID = 3, COL_SUBORDER_ID = 4, COL_FK_MICROMOVEMENT_ID_ID = 5, COL_FK_BODYPART_ID_ID = 6, COL_FK_ATTITUDE_TYPE_ID_ID = 7;
    public static final String COL_SUBORDER_NAME = "suborder";
    private ArrayList<AttitudeType> attitudeTypes;
    private ArrayList<Bodypart> bodyparts;
    private ArrayList<Micromovement> micromovements;

    public AttitudeDAO(DatabaseAO databaseAO){
        super(databaseAO, "attitude");
    }

    public void setForeignDatas(ArrayList<AttitudeType> attitudeTypes, ArrayList<Bodypart> bodyparts, ArrayList<Micromovement> micromovements){
        this.attitudeTypes = attitudeTypes;
        this.bodyparts = bodyparts;
        this.micromovements = micromovements;
    }

    public ArrayList<Attitude> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<Attitude> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((Attitude)data);
        return datasCasted;
    }
    
    public Attitude getObjectFromCursor(Cursor cursor){
        int id, bookAPage, suborder, fkMicromovementId, fkBodypartId, fkAttitudeTypeId;
        String description = null, meaning;
        id = cursor.getInt(COL_ID_ID);
        if(!cursor.isNull(COL_DESCRIPTION_ID))
            description = cursor.getString(COL_DESCRIPTION_ID);
        meaning = cursor.getString(COL_MEANING_ID);
        bookAPage = cursor.getInt(COL_BOOKA_PAGE_ID);
        suborder = cursor.getInt(COL_SUBORDER_ID);
        fkMicromovementId = cursor.getInt(COL_FK_MICROMOVEMENT_ID_ID);
        fkBodypartId = cursor.getInt(COL_FK_BODYPART_ID_ID);
        fkAttitudeTypeId = cursor.getInt(COL_FK_ATTITUDE_TYPE_ID_ID);

        Micromovement micromovement = null;
        Bodypart bodypart = null;
        AttitudeType attitudeType = null;
        if(fkMicromovementId != 0)
            micromovement = Data.getDataById(micromovements, fkMicromovementId);
        if(fkBodypartId != 0)
            bodypart = Data.getDataById(bodyparts, fkBodypartId);
        if(fkAttitudeTypeId != 0)
            attitudeType = Data.getDataById(attitudeTypes, fkAttitudeTypeId);

        return new Attitude(id, description, meaning, bookAPage, suborder, micromovement, bodypart, attitudeType);
    }
}