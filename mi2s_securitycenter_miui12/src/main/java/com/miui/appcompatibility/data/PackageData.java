package com.miui.appcompatibility.data;

import android.os.Parcel;
import android.os.Parcelable;

public class PackageData implements Parcelable {
    public static final Parcelable.Creator<PackageData> CREATOR = new b();
    private String pkg;
    private int status;
    private String ver;

    public PackageData() {
    }

    protected PackageData(Parcel parcel) {
        this.pkg = parcel.readString();
        this.ver = parcel.readString();
        this.status = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public String getPkg() {
        return this.pkg;
    }

    public int getStatus() {
        return this.status;
    }

    public String getVer() {
        return this.ver;
    }

    public void setPkg(String str) {
        this.pkg = str;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public void setVer(String str) {
        this.ver = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.pkg);
        parcel.writeString(this.ver);
        parcel.writeInt(this.status);
    }
}
