package com.miui.powercenter.batteryhistory;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: com.miui.powercenter.batteryhistory.j  reason: case insensitive filesystem */
class C0506j implements Parcelable.Creator<BatteryHistogramItem> {
    C0506j() {
    }

    public BatteryHistogramItem createFromParcel(Parcel parcel) {
        return new BatteryHistogramItem(parcel);
    }

    public BatteryHistogramItem[] newArray(int i) {
        return new BatteryHistogramItem[i];
    }
}
