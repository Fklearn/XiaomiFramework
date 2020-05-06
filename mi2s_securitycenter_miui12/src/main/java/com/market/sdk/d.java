package com.market.sdk;

import android.os.Parcel;
import android.os.Parcelable;

class d implements Parcelable.Creator<AppstoreAppInfo> {
    d() {
    }

    public AppstoreAppInfo createFromParcel(Parcel parcel) {
        return new AppstoreAppInfo(parcel);
    }

    public AppstoreAppInfo[] newArray(int i) {
        return new AppstoreAppInfo[i];
    }
}
