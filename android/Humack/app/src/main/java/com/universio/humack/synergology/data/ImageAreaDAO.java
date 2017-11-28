package com.universio.humack.synergology.data;

import android.database.Cursor;
import android.graphics.Rect;

import com.universio.humack.data.Data;
import com.universio.humack.data.DataAccessObject;
import com.universio.humack.data.DatabaseAO;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 18/09/2015.
 */
public class ImageAreaDAO extends DataAccessObject {
    private static final int COL_ID_ID = 0, COL_IMAGE_ID = 1, COL_COLOR_ID = 2, COL_RECTANGLE_ID = 3, COL_FK_BODYGROUP_ID_ID = 4;
    private ArrayList<BodyGroup> bodyGroups;

    public ImageAreaDAO(DatabaseAO databaseAO){
        super(databaseAO, "imagearea");
    }

    public void setForeignDatas(ArrayList<BodyGroup> bodyGroups){
        this.bodyGroups = bodyGroups;
    }

    public ArrayList<ImageArea> getAll(String orderbyColumn){
        ArrayList<Data> datas = super.getAllData(orderbyColumn);
        ArrayList<ImageArea> datasCasted = new ArrayList<>();
        for(Data data : datas)
            datasCasted.add((ImageArea)data);
        return datasCasted;
    }

    public ImageArea getObjectFromCursor(Cursor cursor){
        int id, image, color;
        id = cursor.getInt(COL_ID_ID);
        image = cursor.getInt(COL_IMAGE_ID);
        color = cursor.getInt(COL_COLOR_ID);

        String rectStr = cursor.getString(COL_RECTANGLE_ID);
        String[] rectVals = rectStr.split(",");
        Rect rectangle = new Rect(Integer.parseInt(rectVals[0]), Integer.parseInt(rectVals[1]), Integer.parseInt(rectVals[2]), Integer.parseInt(rectVals[3]));

        BodyGroup bodyGroup = Data.getDataById(bodyGroups, cursor.getInt(COL_FK_BODYGROUP_ID_ID));

        return new ImageArea(id, image, color, rectangle, bodyGroup);
    }
}
