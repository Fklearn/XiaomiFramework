package com.miui.powercenter.batteryhistory;

import android.os.Build;
import miui.securitycenter.powercenter.HistoryItemWrapper;

public class aa implements Comparable<aa> {

    /* renamed from: a  reason: collision with root package name */
    public long f6864a;

    /* renamed from: b  reason: collision with root package name */
    public byte f6865b = -1;

    /* renamed from: c  reason: collision with root package name */
    public byte f6866c;

    /* renamed from: d  reason: collision with root package name */
    public byte f6867d;
    public byte e;
    public byte f;
    public short g;
    public char h;
    public boolean i;
    public boolean j;
    public boolean k;
    public boolean l;
    public boolean m;
    public int n;
    public boolean o;

    public aa() {
    }

    public aa(HistoryItemWrapper historyItemWrapper) {
        this.f6864a = ((Long) historyItemWrapper.getObjectValue("time")).longValue();
        this.f6865b = ((Integer) historyItemWrapper.getObjectValue("cmd")).byteValue();
        this.f6866c = ((Integer) historyItemWrapper.getObjectValue("batteryLevel")).byteValue();
        this.f6867d = ((Integer) historyItemWrapper.getObjectValue("batteryStatus")).byteValue();
        this.e = ((Integer) historyItemWrapper.getObjectValue("batteryHealth")).byteValue();
        this.f = ((Integer) historyItemWrapper.getObjectValue("batteryPlugType")).byteValue();
        this.g = ((Integer) historyItemWrapper.getObjectValue("batteryTemperature")).shortValue();
        this.h = (char) ((Integer) historyItemWrapper.getObjectValue("batteryVoltage")).intValue();
        this.i = ((Boolean) historyItemWrapper.getObjectValue("wifiOn")).booleanValue();
        this.j = ((Boolean) historyItemWrapper.getObjectValue("gpsOn")).booleanValue();
        this.k = ((Boolean) historyItemWrapper.getObjectValue("charging")).booleanValue();
        this.l = ((Boolean) historyItemWrapper.getObjectValue("screenOn")).booleanValue();
        this.m = ((Boolean) historyItemWrapper.getObjectValue("wakelockOn")).booleanValue();
        this.n = ((Integer) historyItemWrapper.getObjectValue("phoneSignalStrength")).intValue();
        Boolean bool = (Boolean) historyItemWrapper.getObjectValue("cpuRunning");
        this.o = bool == null ? this.m : bool.booleanValue();
    }

    /* renamed from: a */
    public int compareTo(aa aaVar) {
        return 0;
    }

    public long a() {
        return this.f6864a;
    }

    public boolean b() {
        return Build.VERSION.SDK_INT >= 21 ? this.f6865b == 0 : this.f6865b == 1;
    }

    public boolean c() {
        return this.f6865b == 6;
    }
}
