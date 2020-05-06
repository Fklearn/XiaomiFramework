package com.market.sdk;

import android.os.Parcel;
import android.os.Parcelable;

class h implements Parcelable.Creator<DesktopRecommendInfo> {
    h() {
    }

    public DesktopRecommendInfo createFromParcel(Parcel parcel) {
        return new DesktopRecommendInfo(parcel);
    }

    public DesktopRecommendInfo[] newArray(int i) {
        return new DesktopRecommendInfo[i];
    }
}
