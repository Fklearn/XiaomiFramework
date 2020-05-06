package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class u implements Parcelable.Creator<VerticalInRow> {
    u() {
    }

    public VerticalInRow createFromParcel(Parcel parcel) {
        return new VerticalInRow(parcel);
    }

    public VerticalInRow[] newArray(int i) {
        return new VerticalInRow[i];
    }
}
