package com.universio.humack.synergology.data;

import com.universio.humack.data.Data;

/**
 * Created by Cyril Humbertclaude on 24/04/2015.
 */
public class Micromovement extends Data {
    private String title;

    public Micromovement(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Micromovement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}