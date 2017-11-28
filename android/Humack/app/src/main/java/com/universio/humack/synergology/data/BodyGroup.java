package com.universio.humack.synergology.data;

import com.universio.humack.data.Data;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 18/09/2015.
 */
public class BodyGroup extends Data{
    private String name;
    private ImageArea imageArea;
    private ArrayList<Attitude> attitudes;

    public BodyGroup(int id, String name) {
        this.id = id;
        this.name = name;
        this.imageArea = null;
        this.attitudes = null;
    }

    public String getName() {
        return name;
    }

    public ImageArea getImageArea() {
        return imageArea;
    }

    public void setImageArea(ImageArea imageArea) {
        this.imageArea = imageArea;
    }

    public boolean hasAttitudes(){
        return attitudes != null;
    }

    public ArrayList<Attitude> getAttitudes() {
        return attitudes;
    }

    public void addAttitude(Attitude attitude) {
        if(attitudes == null)
            attitudes = new ArrayList<>();
        this.attitudes.add(attitude);
    }

    @Override
    public String toString() {
        return "BodyGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
