package com.miui.appcompatibility.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class AppCompatibilityData implements Parcelable {
    public static final Parcelable.Creator<AppCompatibilityData> CREATOR = new a();
    private String device;
    private String osver;
    private List<PackageData> pkgs = new ArrayList();
    private int total;

    public AppCompatibilityData() {
    }

    protected AppCompatibilityData(Parcel parcel) {
        this.device = parcel.readString();
        this.osver = parcel.readString();
        this.pkgs = parcel.createTypedArrayList(PackageData.CREATOR);
        this.total = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public String getDevice() {
        return this.device;
    }

    public String getOsver() {
        return this.osver;
    }

    public List<PackageData> getPkgs() {
        return this.pkgs;
    }

    public int getTotal() {
        return this.total;
    }

    public void setDevice(String str) {
        this.device = str;
    }

    public void setOsver(String str) {
        this.osver = str;
    }

    public void setPkgs(List<PackageData> list) {
        this.pkgs = list;
    }

    public void setTotal(int i) {
        this.total = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.device);
        parcel.writeString(this.osver);
        parcel.writeTypedList(this.pkgs);
        parcel.writeInt(this.total);
    }
}
