package com.miui.powercenter.legacypowerrank;

import android.os.Parcel;
import android.os.Parcelable;
import com.xiaomi.stat.MiStat;
import miui.cloud.CloudPushConstants;
import miui.securitycenter.powercenter.BatterySipper;

public class BatteryData implements Comparable<BatteryData>, Parcelable {
    public static final int AMBIENT_DISPLAY = 11;
    public static final int APP = 6;
    public static final int BLUETOOTH = 4;
    public static final int CAMERA = 9;
    public static final int CELL = 1;
    public static final Parcelable.Creator<BatteryData> CREATOR = new a();
    public static final int FLASHLIGHT = 7;
    public static final int IDLE = 0;
    public static final int OTHER = 10;
    public static final int PHONE = 2;
    public static final int SCREEN = 5;
    public static final int USER = 8;
    public static final int WIFI = 3;
    public long cpuFgTime;
    public long cpuTime;
    public String defaultPackageName;
    public int drainType;
    public long gpsTime;
    public long mobileRxBytes;
    public long mobileTxBytes;
    public String name;
    public double noCoveragePercent;
    public int uid = -1;
    public long usageTime;
    public double value;
    public long wakeLockTime;
    public long wifiRunningTime;

    public BatteryData() {
    }

    protected BatteryData(Parcel parcel) {
        this.name = parcel.readString();
        this.uid = parcel.readInt();
        this.value = parcel.readDouble();
        this.drainType = parcel.readInt();
        this.usageTime = parcel.readLong();
        this.cpuTime = parcel.readLong();
        this.gpsTime = parcel.readLong();
        this.wifiRunningTime = parcel.readLong();
        this.cpuFgTime = parcel.readLong();
        this.wakeLockTime = parcel.readLong();
        this.mobileRxBytes = parcel.readLong();
        this.mobileTxBytes = parcel.readLong();
        this.noCoveragePercent = parcel.readDouble();
        this.defaultPackageName = parcel.readString();
    }

    public BatteryData(BatteryData batteryData) {
        this.name = batteryData.name;
        this.uid = batteryData.uid;
        this.drainType = batteryData.drainType;
        this.defaultPackageName = batteryData.defaultPackageName;
        add(batteryData);
    }

    public BatteryData(BatterySipper batterySipper) {
        this.name = (String) batterySipper.getObjectValue(CloudPushConstants.XML_NAME);
        this.uid = batterySipper.getUid();
        this.drainType = batterySipper.getDrainType();
        this.defaultPackageName = batterySipper.getPackageName();
        add(batterySipper);
    }

    public void add(BatteryData batteryData) {
        this.value += batteryData.value;
        this.usageTime += batteryData.usageTime;
        this.cpuTime += batteryData.cpuTime;
        this.gpsTime += batteryData.gpsTime;
        this.wifiRunningTime += batteryData.wifiRunningTime;
        this.cpuFgTime += batteryData.cpuFgTime;
        this.wakeLockTime += batteryData.wakeLockTime;
        this.mobileRxBytes += batteryData.mobileRxBytes;
        this.mobileTxBytes += batteryData.mobileTxBytes;
        this.noCoveragePercent += batteryData.noCoveragePercent;
    }

    public void add(BatterySipper batterySipper) {
        this.value += ((Double) batterySipper.getObjectValue(MiStat.Param.VALUE)).doubleValue();
        this.usageTime += ((Long) batterySipper.getObjectValue("usageTime")).longValue();
        this.cpuTime += ((Long) batterySipper.getObjectValue("cpuTime")).longValue();
        this.gpsTime += ((Long) batterySipper.getObjectValue("gpsTime")).longValue();
        this.wifiRunningTime += ((Long) batterySipper.getObjectValue("wifiRunningTime")).longValue();
        this.cpuFgTime += ((Long) batterySipper.getObjectValue("cpuFgTime")).longValue();
        this.wakeLockTime += ((Long) batterySipper.getObjectValue("wakeLockTime")).longValue();
        this.mobileRxBytes += ((Long) batterySipper.getObjectValue("mobileRxBytes")).longValue();
        this.mobileTxBytes += ((Long) batterySipper.getObjectValue("mobileTxBytes")).longValue();
        this.noCoveragePercent += ((Double) batterySipper.getObjectValue("noCoveragePercent")).doubleValue();
    }

    public int compareTo(BatteryData batteryData) {
        return Double.compare(batteryData.getValue(), getValue());
    }

    public int describeContents() {
        return 0;
    }

    public String getPackageName() {
        return this.defaultPackageName;
    }

    public int getUid() {
        return this.uid;
    }

    public double getValue() {
        return this.value;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(this.uid);
        parcel.writeDouble(this.value);
        parcel.writeInt(this.drainType);
        parcel.writeLong(this.usageTime);
        parcel.writeLong(this.cpuTime);
        parcel.writeLong(this.gpsTime);
        parcel.writeLong(this.wifiRunningTime);
        parcel.writeLong(this.cpuFgTime);
        parcel.writeLong(this.wakeLockTime);
        parcel.writeLong(this.mobileRxBytes);
        parcel.writeLong(this.mobileTxBytes);
        parcel.writeDouble(this.noCoveragePercent);
        parcel.writeString(this.defaultPackageName);
    }
}
