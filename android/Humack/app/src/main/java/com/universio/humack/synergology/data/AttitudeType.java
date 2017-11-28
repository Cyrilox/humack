package com.universio.humack.synergology.data;

import com.universio.humack.data.Data;

/**
 * Created by Cyril Humbertclaude on 24/04/2015.
 */
public class AttitudeType extends Data {
    private String name;

    public AttitudeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AttitudeType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
