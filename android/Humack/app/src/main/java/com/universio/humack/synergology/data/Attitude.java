package com.universio.humack.synergology.data;

import com.universio.humack.data.Data;

/**
 * Created by Cyril Humbertclaude on 24/04/2015.
 */
public class Attitude extends Data {
    private int booka_page, suborder;
    private String description, meaning;
    private Micromovement micromovement;
    private Bodypart bodypart;
    private AttitudeType attitudeType;

    public Attitude(int id, String description, String meaning, int booka_page, int suborder, Micromovement micromovement, Bodypart bodypart, AttitudeType attitudeType) {
        this.id = id;
        this.booka_page = booka_page;
        this.suborder = suborder;
        this.description = description;
        this.meaning = meaning;
        this.micromovement = micromovement;
        this.bodypart = bodypart;
        this.attitudeType = attitudeType;
        //Line breaks
        String lineSeparator = System.getProperty("line.separator");
        if(description != null)
            this.description = this.description.replace("\\r", "").replace("\\n", lineSeparator);
        this.meaning = this.meaning.replace("\\r", "").replace("\\n", lineSeparator);
    }

    public int getBooka_page() {
        return booka_page;
    }

    public int getSuborder() {
        return suborder;
    }

    public String getDescription() {
        return description;
    }

    public String getMeaning() {
        return meaning;
    }

    public Micromovement getMicromovement() {
        return micromovement;
    }

    public Bodypart getBodypart() {
        return bodypart;
    }

    public AttitudeType getAttitudeType() {
        return attitudeType;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasBooka_page() {
        return booka_page != 0;
    }

    public boolean hasMicromovement() {
        return micromovement != null;
    }

    @Override
    public String toString() {
        return "Attitude{" +
                "id=" + id +
                ", booka_page=" + booka_page +
                ", suborder=" + suborder +
                ", description='" + description + '\'' +
                ", meaning='" + meaning + '\'' +
                ", micromovement=" + micromovement +
                ", bodypart=" + bodypart +
                ", attitudeType=" + attitudeType +
                '}';
    }
}
