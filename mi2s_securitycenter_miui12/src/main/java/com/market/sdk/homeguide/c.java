package com.market.sdk.homeguide;

import android.os.Parcel;
import android.os.Parcelable;

class c implements Parcelable.Creator<HomeUserGuideData> {
    c() {
    }

    public HomeUserGuideData createFromParcel(Parcel parcel) {
        return new HomeUserGuideData(parcel);
    }

    public HomeUserGuideData[] newArray(int i) {
        return new HomeUserGuideData[i];
    }
}
