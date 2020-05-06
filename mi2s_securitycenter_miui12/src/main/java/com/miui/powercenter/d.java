package com.miui.powercenter;

import com.miui.powercenter.utils.o;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f6985a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PowerMainActivity f6986b;

    d(PowerMainActivity powerMainActivity, boolean z) {
        this.f6986b = powerMainActivity;
        this.f6985a = z;
    }

    public void run() {
        o.a(this.f6986b.s, this.f6985a);
    }
}
