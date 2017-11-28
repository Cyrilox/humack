package com.universio.humack.data;

import com.universio.humack.Tools;

/**
 * Created by Cyril Humbertclaude on 28/12/2015.
 */
public class Appversion extends Data{
    private int code;
    private String name, changes;

    public Appversion(int id, int code, String name, String changes) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.changes = changes;
        //Line breaks
        if(changes != null)
            this.changes = this.changes.replace("\\r", "").replace("\\n", Tools.getLineSeparator());
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getChanges() {
        return changes;
    }

    public boolean hasChanges() {
        return changes != null;
    }

    @Override
    public String toString() {
        return "Appversion{" +
                "id=" + id +
                ", code=" + code +
                ", name='" + name + '\'' +
                ", changes='" + changes + '\'' +
                '}';
    }
}
