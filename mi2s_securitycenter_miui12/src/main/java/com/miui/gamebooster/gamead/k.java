package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class k implements Parcelable.Creator<Horizontal> {
    k() {
    }

    public Horizontal createFromParcel(Parcel parcel) {
        return new Horizontal(parcel);
    }

    public Horizontal[] newArray(int i) {
        return new Horizontal[i];
    }
}
