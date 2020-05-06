package com.miui.powercenter.batteryhistory;

import android.os.Parcel;
import android.os.Parcelable;

class Y implements Parcelable.Creator<BatteryShutdownItem> {
    Y() {
    }

    public BatteryShutdownItem createFromParcel(Parcel parcel) {
        return new BatteryShutdownItem(parcel);
    }

    public BatteryShutdownItem[] newArray(int i) {
        return new BatteryShutdownItem[i];
    }
}
