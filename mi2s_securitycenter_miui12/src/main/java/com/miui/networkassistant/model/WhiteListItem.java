package com.miui.networkassistant.model;

import com.miui.networkassistant.ui.base.recyclerview.BaseEntity;

public class WhiteListItem extends BaseEntity {
    protected String mAppLabel;
    protected boolean mEnabled;
    protected String mPkgName;
    protected int mUid;

    public String getAppLabel() {
        return this.mAppLabel;
    }

    public String getPkgName() {
        return this.mPkgName;
    }

    public int getUid() {
        return this.mUid;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void setAppLabel(String str) {
        this.mAppLabel = str;
    }

    public void setEnabled(boolean z) {
        this.mEnabled = z;
    }

    public void setPkgName(String str) {
        this.mPkgName = str;
    }

    public void setUid(int i) {
        this.mUid = i;
    }

    public String toString() {
        return "WhiteListItem : " + "mPkgName : " + this.mPkgName + ", mAppLabel : " + this.mAppLabel + ", mEnabled : " + this.mEnabled;
    }
}
