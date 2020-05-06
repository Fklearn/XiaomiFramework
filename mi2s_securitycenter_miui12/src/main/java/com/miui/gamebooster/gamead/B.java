package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class B implements Parcelable.Creator<ViewpointInfo> {
    B() {
    }

    public ViewpointInfo createFromParcel(Parcel parcel) {
        return new ViewpointInfo(parcel);
    }

    public ViewpointInfo[] newArray(int i) {
        return new ViewpointInfo[i];
    }
}
