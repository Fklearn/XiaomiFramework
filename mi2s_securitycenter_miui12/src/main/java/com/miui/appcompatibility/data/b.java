package com.miui.appcompatibility.data;

import android.os.Parcel;
import android.os.Parcelable;

class b implements Parcelable.Creator<PackageData> {
    b() {
    }

    public PackageData createFromParcel(Parcel parcel) {
        return new PackageData(parcel);
    }

    public PackageData[] newArray(int i) {
        return new PackageData[i];
    }
}
