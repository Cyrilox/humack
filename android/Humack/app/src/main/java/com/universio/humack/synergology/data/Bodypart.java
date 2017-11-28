package com.universio.humack.synergology.data;

import com.universio.humack.data.Data;

/**
 * Created by Cyril Humbertclaude on 24/04/2015.
 */
public class Bodypart extends Data {
    private String name;

    public Bodypart(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Bodypart{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
