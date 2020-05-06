package com.miui.permcenter.privacymanager;

import android.text.TextUtils;
import java.io.Serializable;

public class StatusBar implements Serializable {
    public int mUserId;
    public long permId;
    public String pkgName;

    public StatusBar() {
    }

    public StatusBar(int i, String str, long j) {
        this.mUserId = i;
        this.pkgName = str;
        this.permId = j;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof StatusBar)) {
            return false;
        }
        StatusBar statusBar = (StatusBar) obj;
        return this.pkgName.equals(statusBar.pkgName) && this.permId == statusBar.permId && this.mUserId == statusBar.mUserId;
    }

    public int hashCode() {
        return (this.mUserId + this.pkgName + this.permId).hashCode();
    }

    public boolean isSameInfo(StatusBar statusBar) {
        return statusBar != null && TextUtils.equals(statusBar.pkgName, this.pkgName) && statusBar.mUserId == this.mUserId;
    }

    public void setInfo(String str, int i) {
        this.pkgName = str;
        this.mUserId = i;
    }
}
