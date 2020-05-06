package com.market.sdk;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class ApkVerifyInfo implements Parcelable {
    public static final Parcelable.Creator<ApkVerifyInfo> CREATOR = new b();
    public static final int STATUS_CONNECTION_FAILED = 3;
    public static final int STATUS_INCONSISTENT_CERTIFICATES = 2;
    public static final int STATUS_NOT_INCLUDED = 4;
    public static final int STATUS_OK = 0;
    public static final int STATUS_OLDER = 1;
    public static final int STATUS_PROCESSING = 6;
    public static final int STATUS_RESULT_INVALID = 5;
    public String mAppId = "";
    public String mAppName = "";
    public String mInstallerName = "";
    public Intent mIntent;
    public boolean mNeedFullScan;
    public long mNonce = 0;
    public String mPackageName = "";
    public int mStatus = 4;
    public long mTimeStamp = 0;
    public String mToken = "";
    public String mUpdateLog = "";
    public long mUpdateTime = 0;
    public int mVersionCode = 0;
    public String mVersionName = "";

    public ApkVerifyInfo() {
    }

    public ApkVerifyInfo(int i) {
        this.mStatus = i;
    }

    public ApkVerifyInfo(Parcel parcel) {
        this.mStatus = parcel.readInt();
        this.mVersionName = parcel.readString();
        this.mVersionCode = parcel.readInt();
        this.mUpdateTime = parcel.readLong();
        this.mUpdateLog = parcel.readString();
        this.mNonce = parcel.readLong();
        this.mTimeStamp = parcel.readLong();
        this.mAppName = parcel.readString();
        this.mInstallerName = parcel.readString();
        this.mAppId = parcel.readString();
        this.mPackageName = parcel.readString();
        this.mIntent = (Intent) parcel.readParcelable((ClassLoader) null);
    }

    public boolean canReplaceByAppStore() {
        int i = this.mStatus;
        return i == 0 || i == 1 || i == 2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mStatus);
        parcel.writeString(this.mVersionName);
        parcel.writeInt(this.mVersionCode);
        parcel.writeLong(this.mUpdateTime);
        parcel.writeString(this.mUpdateLog);
        parcel.writeLong(this.mNonce);
        parcel.writeLong(this.mTimeStamp);
        parcel.writeString(this.mAppName);
        parcel.writeString(this.mInstallerName);
        parcel.writeString(this.mAppId);
        parcel.writeString(this.mPackageName);
        parcel.writeParcelable(this.mIntent, 0);
    }
}
