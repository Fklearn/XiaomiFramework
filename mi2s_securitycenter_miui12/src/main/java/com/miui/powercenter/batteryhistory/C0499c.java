package com.miui.powercenter.batteryhistory;

import com.miui.powercenter.batteryhistory.C0500d;
import com.miui.powercenter.legacypowerrank.BatteryData;
import java.util.List;

/* renamed from: com.miui.powercenter.batteryhistory.c  reason: case insensitive filesystem */
class C0499c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0500d.a f6872a;

    C0499c(C0500d.a aVar) {
        this.f6872a = aVar;
    }

    public void run() {
        C0500d.this.l.b();
        C0500d.this.l.a(C0500d.this.j);
        C0500d.this.l.a((List<BatteryData>) C0500d.this.i);
    }
}
