package com.universio.humack.synergology.data;

import android.graphics.Rect;

import com.universio.humack.data.Data;

/**
 * Created by Cyril Humbertclaude on 18/09/2015.
 */
public class ImageArea extends Data{
    private int image, color;
    private Rect rectangle;
    private BodyGroup bodyGroup;

    public ImageArea(int id, int image, int color, Rect rectangle, BodyGroup bodyGroup) {
        this.id = id;
        this.image = image;
        this.color = color;
        this.rectangle = rectangle;
        this.bodyGroup = bodyGroup;
    }

    public int getImage() {
        return image;
    }

    public int getColor() {
        return color;
    }

    public Rect getRectangle() {
        return rectangle;
    }

    public BodyGroup getBodyGroup() {
        return bodyGroup;
    }

    @Override
    public String toString() {
        return "ImageArea{" +
                "id=" + id +
                ", image=" + image +
                ", color=" + color +
                ", rectangle=" + rectangle +
                ", bodyGroup=" + bodyGroup +
                '}';
    }
}
