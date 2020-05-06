package com.miui.warningcenter;

import java.io.Serializable;

public class WarningAlertBaseModel implements Serializable {
    public static final String KEY_ID = "id";
    private String id;
    private int type;
    private int version;

    public String getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    public int getVersion() {
        return this.version;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setVersion(int i) {
        this.version = i;
    }
}
