package com.market.sdk;

import android.os.Parcel;
import android.os.Parcelable;

class b implements Parcelable.Creator<ApkVerifyInfo> {
    b() {
    }

    public ApkVerifyInfo createFromParcel(Parcel parcel) {
        return new ApkVerifyInfo(parcel);
    }

    public ApkVerifyInfo[] newArray(int i) {
        return new ApkVerifyInfo[i];
    }
}
