package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class n implements Parcelable.Creator<LikeInfo> {
    n() {
    }

    public LikeInfo createFromParcel(Parcel parcel) {
        return new LikeInfo(parcel);
    }

    public LikeInfo[] newArray(int i) {
        return new LikeInfo[i];
    }
}
