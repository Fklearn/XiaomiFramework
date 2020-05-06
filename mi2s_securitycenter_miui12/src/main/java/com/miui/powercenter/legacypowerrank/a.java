package com.miui.powercenter.legacypowerrank;

import android.os.Parcel;
import android.os.Parcelable;

class a implements Parcelable.Creator<BatteryData> {
    a() {
    }

    public BatteryData createFromParcel(Parcel parcel) {
        return new BatteryData(parcel);
    }

    public BatteryData[] newArray(int i) {
        return new BatteryData[i];
    }
}
