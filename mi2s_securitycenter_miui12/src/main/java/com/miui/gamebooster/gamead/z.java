package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class z implements Parcelable.Creator<ViewPointVideoInfo> {
    z() {
    }

    public ViewPointVideoInfo createFromParcel(Parcel parcel) {
        return new ViewPointVideoInfo(parcel);
    }

    public ViewPointVideoInfo[] newArray(int i) {
        return new ViewPointVideoInfo[i];
    }
}
