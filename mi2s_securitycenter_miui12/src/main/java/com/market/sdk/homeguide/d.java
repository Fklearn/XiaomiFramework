package com.market.sdk.homeguide;

import android.os.Parcel;
import android.os.Parcelable;

class d implements Parcelable.Creator<HomeUserGuideResult> {
    d() {
    }

    public HomeUserGuideResult createFromParcel(Parcel parcel) {
        return new HomeUserGuideResult(parcel);
    }

    public HomeUserGuideResult[] newArray(int i) {
        return new HomeUserGuideResult[i];
    }
}
