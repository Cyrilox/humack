package com.universio.humack.synergology.data;

import com.universio.humack.data.Data;

/**
 * Created by Cyril Humbertclaude on 03/11/2015.
 */
public class Hemisphere extends Data {
    private String name;

    public Hemisphere(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Hemisphere{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}