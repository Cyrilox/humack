package com.universio.humack.data;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 03/06/2015.
 */
public abstract class Data{
    protected int id;

    public int getId() {
        return id;
    }

    public static <T> T getDataById(ArrayList<T> datas, int id){
        for(T data : datas)
            if(((Data)data).getId() == id)
                return data;
        return null;
    }
}
