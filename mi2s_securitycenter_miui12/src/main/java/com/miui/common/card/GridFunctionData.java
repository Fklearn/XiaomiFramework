package com.miui.common.card;

import android.graphics.drawable.Drawable;

public class GridFunctionData {
    private String ABtest;
    private String action;
    private Drawable adsCacheDrawable;
    private String dataId;
    private String functionId;
    private String icon;
    private String iconId;
    private int iconResourceId;
    private boolean isMarquee;
    private int localPicResoourceId;
    private String statKey;
    private String summary;
    private int template;
    private long time;
    private String title;
    private boolean useLocalPic;

    public GridFunctionData() {
    }

    public GridFunctionData(String str, String str2, int i, String str3) {
        this(str, str2, i, str3, true);
    }

    public GridFunctionData(String str, String str2, int i, String str3, boolean z) {
        this.title = str;
        this.summary = str2;
        this.localPicResoourceId = i;
        this.action = str3;
        this.useLocalPic = z;
    }

    public String getABtest() {
        return this.ABtest;
    }

    public String getAction() {
        return this.action;
    }

    public Drawable getAdsCacheDrawable() {
        return this.adsCacheDrawable;
    }

    public String getDataId() {
        return this.dataId;
    }

    public String getFunctionId() {
        return this.functionId;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getIconId() {
        return this.iconId;
    }

    public int getIconResourceId() {
        return this.iconResourceId;
    }

    public int getLocalPicResoourceId() {
        return this.localPicResoourceId;
    }

    public String getStatKey() {
        return this.statKey;
    }

    public String getSummary() {
        return this.summary;
    }

    public int getTemplate() {
        return this.template;
    }

    public long getTime() {
        return this.time;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isMarquee() {
        return this.isMarquee;
    }

    public boolean isUseLocalPic() {
        return this.useLocalPic;
    }

    public void setABtest(String str) {
        this.ABtest = str;
    }

    public void setAction(String str) {
        this.action = str;
    }

    public void setAdsCacheDrawable(Drawable drawable) {
        this.adsCacheDrawable = drawable;
    }

    public void setDataId(String str) {
        this.dataId = str;
    }

    public void setFunctionId(String str) {
        this.functionId = str;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public void setIconId(String str) {
        this.iconId = str;
    }

    public void setIconResourceId(int i) {
        this.iconResourceId = i;
    }

    public void setMarquee(boolean z) {
        this.isMarquee = z;
    }

    public void setStatKey(String str) {
        this.statKey = str;
    }

    public void setSummary(String str) {
        this.summary = str;
    }

    public void setTemplate(int i) {
        this.template = i;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
