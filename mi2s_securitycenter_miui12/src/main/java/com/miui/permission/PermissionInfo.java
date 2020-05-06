package com.miui.permission;

import android.os.Parcel;
import android.os.Parcelable;

public class PermissionInfo implements Parcelable {
    public static final Parcelable.Creator<PermissionInfo> CREATOR = new Parcelable.Creator<PermissionInfo>() {
        public PermissionInfo createFromParcel(Parcel parcel) {
            return new PermissionInfo(parcel);
        }

        public PermissionInfo[] newArray(int i) {
            return new PermissionInfo[i];
        }
    };
    public static final int FLAG_NO_ASK = 16;
    public static final int FLAG_SUPPORT_FOREGROUND = 64;
    private int appCount;
    private String desc;
    private int flags;
    private int group;
    private long id;
    private String name;

    public PermissionInfo() {
    }

    private PermissionInfo(Parcel parcel) {
        this.id = parcel.readLong();
        this.name = parcel.readString();
        this.desc = parcel.readString();
        this.flags = parcel.readInt();
        this.appCount = parcel.readInt();
        this.group = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public int getAppCount() {
        return this.appCount;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getFlags() {
        return this.flags;
    }

    public int getGroup() {
        return this.group;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setAppCount(int i) {
        this.appCount = i;
    }

    public void setDesc(String str) {
        this.desc = str;
    }

    public void setFlags(int i) {
        this.flags = i;
    }

    public void setGroup(int i) {
        this.group = i;
    }

    public void setId(long j) {
        this.id = j;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.desc);
        parcel.writeInt(this.flags);
        parcel.writeInt(this.appCount);
        parcel.writeInt(this.group);
    }
}
