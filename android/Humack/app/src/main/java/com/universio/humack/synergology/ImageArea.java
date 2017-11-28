package com.universio.humack.synergology;

import android.graphics.Rect;

/**
 * Created by Cyril Humbertclaude on 11/05/2015.
 */
public class ImageArea {
    private int id, color;
    private Rect imageFrame;

    public ImageArea(int id, int color, Rect imageFrame) {
        this.id = id;
        this.color = color;
        this.imageFrame = imageFrame;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public Rect getImageFrame() {
        return imageFrame;
    }

    @Override
    public String toString() {
        return "ImageArea{" +
                "id=" + id +
                ", color=" + color +
                ", imageFrame=" + imageFrame +
                '}';
    }
}
