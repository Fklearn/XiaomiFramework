package com.miui.securitycenter.memory;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

public class MemoryModel implements Parcelable {
    public static final Parcelable.Creator<MemoryModel> CREATOR = new c();
    private String mAppName;
    private SparseBooleanArray mLockState;
    private long mMemorySize;
    private String mPackageName;

    public MemoryModel() {
    }

    public MemoryModel(Parcel parcel) {
        setAppName(parcel.readString());
        setPackageName(parcel.readString());
        setMemorySize(parcel.readLong());
        setLockState(parcel.readSparseBooleanArray());
    }

    public int describeContents() {
        return 0;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public SparseBooleanArray getLockState() {
        return this.mLockState;
    }

    public long getMemorySize() {
        return this.mMemorySize;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public void setAppName(String str) {
        this.mAppName = str;
    }

    public void setLockState(SparseBooleanArray sparseBooleanArray) {
        this.mLockState = sparseBooleanArray;
    }

    public void setMemorySize(long j) {
        this.mMemorySize = j;
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
    }

    public String toString() {
        return "ProcessModel : AppName = " + this.mAppName + " PkgName = " + this.mPackageName + " MemorySize = " + this.mMemorySize + " LockState = " + this.mLockState;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mAppName);
        parcel.writeString(this.mPackageName);
        parcel.writeLong(this.mMemorySize);
        parcel.writeSparseBooleanArray(this.mLockState);
    }
}
