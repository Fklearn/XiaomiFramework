package com.miui.powercenter.batteryhistory;

import com.miui.powercenter.legacypowerrank.BatteryData;
import java.util.Comparator;

/* renamed from: com.miui.powercenter.batteryhistory.b  reason: case insensitive filesystem */
class C0498b implements Comparator<BatteryData> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0500d f6868a;

    C0498b(C0500d dVar) {
        this.f6868a = dVar;
    }

    /* renamed from: a */
    public int compare(BatteryData batteryData, BatteryData batteryData2) {
        if (batteryData.getValue() - batteryData2.getValue() > 0.0d) {
            return -1;
        }
        return batteryData.getValue() - batteryData2.getValue() < 0.0d ? 1 : 0;
    }
}
