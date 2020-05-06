package com.miui.appcompatibility.data;

import android.os.Parcel;
import android.os.Parcelable;

class a implements Parcelable.Creator<AppCompatibilityData> {
    a() {
    }

    public AppCompatibilityData createFromParcel(Parcel parcel) {
        return new AppCompatibilityData(parcel);
    }

    public AppCompatibilityData[] newArray(int i) {
        return new AppCompatibilityData[i];
    }
}
