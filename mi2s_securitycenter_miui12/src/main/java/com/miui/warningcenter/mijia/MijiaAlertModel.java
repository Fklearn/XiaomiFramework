package com.miui.warningcenter.mijia;

import com.miui.warningcenter.WarningAlertBaseModel;

public class MijiaAlertModel extends WarningAlertBaseModel {
    public static final String KEY_ALERTTYPE = "alertType";
    public static final String KEY_CTIME = "ctime";
    public static final String KEY_HOMENAME = "homeName";
    public static final String KEY_ROOMNAME = "roomName";
    public static final String KEY_THIRD_TYPE = "third_type";
    public static final String KEY_UID = "uid";
    public static final String KEY_URL = "url";
    public static final String KEY_WARINGSIGN = "WaringSign";
    public static final String KEY_WARNING = "warning";
    public static final String TYPE_BREAK_LOCK = "break_lock";
    public static final String TYPE_GAS_LEAK = "gas_leak";
    public static final String TYPE_SMOKE = "smoke";
    public static final String TYPE_WATER_LEAK = "water_leak";
    private String alertType;
    private long ctime;
    private String homeName;
    private String roomName;
    private String third_type;
    private String uid;
    private String url;

    public String getAlertType() {
        return this.alertType;
    }

    public long getCtime() {
        return this.ctime;
    }

    public String getHomeName() {
        return this.homeName;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public String getThird_type() {
        return this.third_type;
    }

    public String getUid() {
        return this.uid;
    }

    public String getUrl() {
        return this.url;
    }

    public void setAlertType(String str) {
        this.alertType = str;
    }

    public void setCtime(long j) {
        this.ctime = j;
    }

    public void setHomeName(String str) {
        this.homeName = str;
    }

    public void setRoomName(String str) {
        this.roomName = str;
    }

    public void setThird_type(String str) {
        this.third_type = str;
    }

    public void setUid(String str) {
        this.uid = str;
    }

    public void setUrl(String str) {
        this.url = str;
    }
}
