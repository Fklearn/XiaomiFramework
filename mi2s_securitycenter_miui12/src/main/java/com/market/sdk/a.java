package com.market.sdk;

import android.os.Parcel;
import android.os.Parcelable;

class a implements Parcelable.Creator<AdsBannerInfo> {
    a() {
    }

    public AdsBannerInfo createFromParcel(Parcel parcel) {
        return new AdsBannerInfo(parcel);
    }

    public AdsBannerInfo[] newArray(int i) {
        return new AdsBannerInfo[i];
    }
}
