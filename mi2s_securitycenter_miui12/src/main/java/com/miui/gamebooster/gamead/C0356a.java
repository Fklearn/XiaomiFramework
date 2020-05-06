package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: com.miui.gamebooster.gamead.a  reason: case insensitive filesystem */
class C0356a implements Parcelable.Creator<ActivityInfo> {
    C0356a() {
    }

    public ActivityInfo createFromParcel(Parcel parcel) {
        return new ActivityInfo(parcel);
    }

    public ActivityInfo[] newArray(int i) {
        return new ActivityInfo[i];
    }
}
