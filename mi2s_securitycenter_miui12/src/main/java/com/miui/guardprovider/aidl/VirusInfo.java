package com.miui.guardprovider.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class VirusInfo implements Parcelable {
    public static final Parcelable.Creator<VirusInfo> CREATOR = new b();
    public String adDescription;
    public int adLevel;
    public String adName;
    public String engineName;
    public boolean isAuthoritative = false;
    public boolean isInCache = false;
    public String packageName;
    public String path;
    public int versionCode;
    public String versionName;
    public String virusDescription;
    public int virusLevel;
    public String virusName;
    public int virusType;

    public VirusInfo() {
    }

    public VirusInfo(Parcel parcel) {
        this.path = parcel.readString();
        this.packageName = parcel.readString();
        this.virusName = parcel.readString();
        this.virusType = parcel.readInt();
        this.virusLevel = parcel.readInt();
        this.virusDescription = parcel.readString();
        this.engineName = parcel.readString();
        this.versionName = parcel.readString();
        this.adName = parcel.readString();
        this.adDescription = parcel.readString();
        this.adLevel = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
        this.path = parcel.readString();
        this.packageName = parcel.readString();
        this.virusName = parcel.readString();
        this.virusType = parcel.readInt();
        this.virusLevel = parcel.readInt();
        this.virusDescription = parcel.readString();
        this.engineName = parcel.readString();
        this.versionName = parcel.readString();
        this.adName = parcel.readString();
        this.adDescription = parcel.readString();
        this.adLevel = parcel.readInt();
    }

    public String toString() {
        return "VirusInfo {\n" + " path: " + this.path + "\n" + " packageName :" + this.packageName + "\n" + " virusName :" + this.virusName + "\n" + " virusType :" + this.virusType + "\n" + " virusLevel :" + this.virusLevel + "\n" + " isAuthoritative :" + this.isAuthoritative + "\n" + "virusDescription :" + this.virusDescription + "\n" + "}";
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.path);
        parcel.writeString(this.packageName);
        parcel.writeString(this.virusName);
        parcel.writeInt(this.virusType);
        parcel.writeInt(this.virusLevel);
        parcel.writeString(this.virusDescription);
        parcel.writeString(this.engineName);
        parcel.writeString(this.versionName);
        parcel.writeString(this.adName);
        parcel.writeString(this.adDescription);
        parcel.writeInt(this.adLevel);
    }
}
