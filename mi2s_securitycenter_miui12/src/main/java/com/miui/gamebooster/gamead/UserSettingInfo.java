package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

public class UserSettingInfo implements Parcelable {
    public static final Parcelable.Creator<UserSettingInfo> CREATOR = new t();
    private boolean isNoTalking;
    private boolean isShield;
    private long mUUId;

    public UserSettingInfo(long j) {
        this.mUUId = j;
    }

    public UserSettingInfo(long j, boolean z, boolean z2) {
        this.mUUId = j;
        this.isNoTalking = z;
        this.isShield = z2;
    }

    protected UserSettingInfo(Parcel parcel) {
        this.mUUId = parcel.readLong();
        boolean z = true;
        this.isNoTalking = parcel.readByte() != 0;
        this.isShield = parcel.readByte() == 0 ? false : z;
    }

    public int describeContents() {
        return 0;
    }

    public long getUUId() {
        return this.mUUId;
    }

    public boolean isNoTalking() {
        return this.isNoTalking;
    }

    public boolean isShield() {
        return this.isShield;
    }

    public void setNoTalking(boolean z) {
        this.isNoTalking = z;
    }

    public void setShield(boolean z) {
        this.isShield = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.mUUId);
        parcel.writeByte(this.isNoTalking ? (byte) 1 : 0);
        parcel.writeByte(this.isShield ? (byte) 1 : 0);
    }
}
