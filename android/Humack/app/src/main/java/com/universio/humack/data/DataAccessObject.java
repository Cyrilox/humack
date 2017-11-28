package com.universio.humack.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 06/05/2015.
 */
public abstract class DataAccessObject<T>{

    private DatabaseAO databaseAO;
    private final String TABLE_NAME;

    public DataAccessObject(DatabaseAO databaseAO, String tableName){
        this.databaseAO = databaseAO;
        this.TABLE_NAME = tableName;
    }

    /**
     * Query the database for a single row then convert it into an object
     * @param id The id of the sql row
     * @return The object found or null
     */
    protected Data getData(int id){
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE _id = " + id + " LIMIT 1";
        SQLiteDatabase database = databaseAO.getDatabase();
        Cursor cursor = database.rawQuery(query, new String[]{});
        Data data = null;
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            data = getObjectFromCursor(cursor);
        }
        return data;
    }

    /**
     * Query the database for all rows then convert them into objects
     * @param orderbyColumn Name(s) for the order by clause or null
     * @return The objects found
     */
    protected ArrayList<Data> getAllData(String orderbyColumn){
        String query = "SELECT * FROM " + TABLE_NAME;
        if(orderbyColumn != null)
            query += " ORDER BY " + orderbyColumn + " ASC";
        SQLiteDatabase database = databaseAO.getDatabase();
        Cursor cursor = database.rawQuery(query, new String[]{});
        ArrayList<Data> datas = new ArrayList<>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Data data = getObjectFromCursor(cursor);
                datas.add(data);
                cursor.moveToNext();
            }
        }
        return datas;
    }

    public abstract ArrayList<T> getAll(String orderbyColumn);

    protected abstract Data getObjectFromCursor(Cursor cursor);
}
