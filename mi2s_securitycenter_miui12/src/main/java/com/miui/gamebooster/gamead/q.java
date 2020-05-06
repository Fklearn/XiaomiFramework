package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class q implements Parcelable.Creator<SimpleTopicInfo> {
    q() {
    }

    public SimpleTopicInfo createFromParcel(Parcel parcel) {
        return new SimpleTopicInfo(parcel);
    }

    public SimpleTopicInfo[] newArray(int i) {
        return new SimpleTopicInfo[i];
    }
}
