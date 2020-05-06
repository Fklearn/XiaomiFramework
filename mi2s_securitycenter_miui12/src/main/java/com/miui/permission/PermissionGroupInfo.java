package com.miui.permission;

import android.os.Parcel;
import android.os.Parcelable;

public class PermissionGroupInfo implements Parcelable {
    public static final Parcelable.Creator<PermissionGroupInfo> CREATOR = new Parcelable.Creator<PermissionGroupInfo>() {
        public PermissionGroupInfo createFromParcel(Parcel parcel) {
            return new PermissionGroupInfo(parcel);
        }

        public PermissionGroupInfo[] newArray(int i) {
            return new PermissionGroupInfo[i];
        }
    };
    private int flags;
    private int id;
    private String name;

    public PermissionGroupInfo() {
    }

    private PermissionGroupInfo(Parcel parcel) {
        this.id = parcel.readInt();
        this.name = parcel.readString();
        this.flags = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public int getFlags() {
        return this.flags;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setFlags(int i) {
        this.flags = i;
    }

    public void setId(int i) {
        this.id = i;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.name);
        parcel.writeInt(this.flags);
    }
}
