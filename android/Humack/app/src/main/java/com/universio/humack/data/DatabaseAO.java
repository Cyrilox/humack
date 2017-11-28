package com.universio.humack.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.universio.humack.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Cyril Humbertclaude on 24/04/2015.
 */
public class DatabaseAO extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DB_NAME = "humack.db";
    private final String DB_PATH;
    private Context context;
    private SQLiteDatabase database = null;

    public DatabaseAO(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
        DB_PATH = context.getString(R.string.databases_dir) + DB_NAME;
    }

    public void createDatabase(){
        //Création d'une base de données vide et de son fichier
        database = this.getReadableDatabase();
        //Fermeture pour écrire dedans
        database.close();
        //Copie fichier à fichier
        try{
            this.copyDatabase();
        }catch(IOException e){
            throw new Error("Error copying database");
        }
        //On recharge en mémoire la base
        database = this.getReadableDatabase();
        database.close();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(DB_PATH);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    /**
     * Open the database
     */
    public void open(){
        if(database == null || !database.isOpen())
            database = getReadableDatabase();
    }

    /**
     * Close the database
     */
    public void close(){
        if(database != null && database.isOpen())
            database.close();
    }

    /**
     * Return the database
     * @return The database
     */
    public SQLiteDatabase getDatabase(){
        return database;
    }
}