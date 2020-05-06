package com.miui.powercenter.batteryhistory;

import android.os.Parcel;
import android.os.Parcelable;

public class BatteryShutdownItem implements Parcelable {
    public static final Parcelable.Creator<BatteryShutdownItem> CREATOR = new Y();
    public long shutDownDuration;
    public int shutDownIndex = -1;
    public long shutDownPlusTime;
    public long shutDownTime;

    public BatteryShutdownItem() {
    }

    protected BatteryShutdownItem(Parcel parcel) {
        this.shutDownTime = parcel.readLong();
        this.shutDownDuration = parcel.readLong();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.shutDownTime);
        parcel.writeLong(this.shutDownDuration);
    }
}
