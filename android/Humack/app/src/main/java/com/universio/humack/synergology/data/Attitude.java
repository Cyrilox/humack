package com.universio.humack.synergology.data;

import com.universio.humack.data.Data;

/**
 * Created by Cyril Humbertclaude on 24/04/2015.
 */
public class Attitude extends Data {
    private Integer booka_page, suborder;
    private String description, meaningA, meaningB;
    private BodyGroup bodyGroup;
    private Micromovement micromovement;
    private Hemisphere hemisphere;

    public Attitude(int id, String description, String meaningA, String meaningB, Integer booka_page, Integer suborder, BodyGroup bodyGroup, Micromovement micromovement, Hemisphere hemisphere) {
        this.id = id;
        this.booka_page = booka_page;
        this.suborder = suborder;
        this.description = description;
        this.meaningA = meaningA;
        this.meaningB = meaningB;
        this.bodyGroup = bodyGroup;
        this.micromovement = micromovement;
        this.hemisphere = hemisphere;
        //Line breaks
        String lineSeparator = System.getProperty("line.separator");
        if(description != null)
            this.description = this.description.replace("\\r", "").replace("\\n", lineSeparator);
        if(meaningA != null)
            this.meaningA = this.meaningA.replace("\\r", "").replace("\\n", lineSeparator);
        if(meaningB != null)
            this.meaningB = this.meaningB.replace("\\r", "").replace("\\n", lineSeparator);
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

    public String getMeaningA() {
        return meaningA;
    }

    public String getMeaningB() {
        return meaningB;
    }

    public BodyGroup getBodyGroup() {
        return bodyGroup;
    }

    public Micromovement getMicromovement() {
        return micromovement;
    }

    public Hemisphere getHemisphere() {
        return hemisphere;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasBooka_page() {
        return booka_page != null;
    }

    public boolean hasMicromovement() {
        return micromovement != null;
    }

    public boolean hasHemisphere() {
        return hemisphere != null;
    }

    @Override
    public String toString() {
        return "Attitude{" +
                "id=" + id +
                ", booka_page=" + booka_page +
                ", suborder=" + suborder +
                ", description='" + description + '\'' +
                ", meaningA='" + meaningA + '\'' +
                ", meaningB='" + meaningB + '\'' +
                ", bodyGroup=" + bodyGroup +
                ", micromovement=" + micromovement +
                ", hemisphere=" + hemisphere +
                '}';
    }
}
