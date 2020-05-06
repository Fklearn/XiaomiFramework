package com.miui.networkassistant.model;

public class VirtualNotiInfo {
    private String mAcitionDesc;
    private String mAction;
    private String mIconUri;
    private String mMonthUsedTraffic;
    private String mTodayUsedTraffic;

    public String getAcitionDesc() {
        return this.mAcitionDesc;
    }

    public String getAction() {
        return this.mAction;
    }

    public String getIconUri() {
        return this.mIconUri;
    }

    public String getMonthUsedTraffic() {
        return this.mMonthUsedTraffic;
    }

    public String getTodayUsedTraffic() {
        return this.mTodayUsedTraffic;
    }

    public void setAcitionDesc(String str) {
        this.mAcitionDesc = str;
    }

    public void setAction(String str) {
        this.mAction = str;
    }

    public void setIconUri(String str) {
        this.mIconUri = str;
    }

    public void setMonthUsedTraffic(String str) {
        this.mMonthUsedTraffic = str;
    }

    public void setTodayUsedTraffic(String str) {
        this.mTodayUsedTraffic = str;
    }
}
