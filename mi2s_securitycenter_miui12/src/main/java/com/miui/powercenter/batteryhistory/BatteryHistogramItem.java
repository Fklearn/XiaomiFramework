package com.miui.powercenter.batteryhistory;

import android.os.Parcel;
import android.os.Parcelable;
import com.miui.powercenter.legacypowerrank.BatteryData;
import java.util.ArrayList;
import java.util.List;

public class BatteryHistogramItem implements Parcelable {
    public static final Parcelable.Creator<BatteryHistogramItem> CREATOR = new C0506j();
    public List<BatteryData> batteryDataList;
    public String batteryDataStr;
    public long endTime;
    public String histogramDataStr;
    public int id;
    public long idleUsageTime;
    public long minLastItemHold;
    public long screenUsageTime;
    public long shutdownDuration;
    public long startTime;
    public long startUTCTime;
    public double totalConsume;
    public int type;

    public BatteryHistogramItem() {
    }

    protected BatteryHistogramItem(Parcel parcel) {
        this.id = parcel.readInt();
        this.type = parcel.readInt();
        this.startTime = parcel.readLong();
        this.endTime = parcel.readLong();
        this.shutdownDuration = parcel.readLong();
        this.totalConsume = parcel.readDouble();
        this.screenUsageTime = parcel.readLong();
        this.idleUsageTime = parcel.readLong();
        this.batteryDataList = parcel.createTypedArrayList(BatteryData.CREATOR);
    }

    public BatteryHistogramItem cloneItem() {
        BatteryHistogramItem batteryHistogramItem = new BatteryHistogramItem();
        batteryHistogramItem.id = this.id;
        batteryHistogramItem.type = this.type;
        batteryHistogramItem.startTime = this.startTime;
        batteryHistogramItem.endTime = this.endTime;
        batteryHistogramItem.shutdownDuration = this.shutdownDuration;
        batteryHistogramItem.totalConsume = this.totalConsume;
        batteryHistogramItem.screenUsageTime = this.screenUsageTime;
        batteryHistogramItem.idleUsageTime = this.idleUsageTime;
        batteryHistogramItem.startUTCTime = this.startUTCTime;
        List<BatteryData> list = this.batteryDataList;
        if (list != null) {
            batteryHistogramItem.batteryDataList = new ArrayList(list);
        }
        return batteryHistogramItem;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeInt(this.type);
        parcel.writeLong(this.startTime);
        parcel.writeLong(this.endTime);
        parcel.writeLong(this.shutdownDuration);
        parcel.writeDouble(this.totalConsume);
        parcel.writeLong(this.screenUsageTime);
        parcel.writeLong(this.idleUsageTime);
        parcel.writeTypedList(this.batteryDataList);
    }
}
