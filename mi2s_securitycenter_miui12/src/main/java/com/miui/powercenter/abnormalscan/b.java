package com.miui.powercenter.abnormalscan;

import android.os.Parcel;
import android.os.Parcelable;

class b implements Parcelable.Creator<AbScanModel> {
    b() {
    }

    public AbScanModel createFromParcel(Parcel parcel) {
        return new AbScanModel(parcel.readString(), parcel.readArrayList(AbScanModel.class.getClassLoader()));
    }

    public AbScanModel[] newArray(int i) {
        return new AbScanModel[i];
    }
}
