package com.miui.powercenter.batteryhistory;

import com.miui.powercenter.batteryhistory.ga;
import com.miui.powercenter.utils.i;

class fa implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ba f6885a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ga.a f6886b;

    fa(ga.a aVar, ba baVar) {
        this.f6886b = aVar;
        this.f6885a = baVar;
    }

    public void run() {
        ba unused = ga.this.e = this.f6885a;
        ga.this.d();
        i.a().a(10002, "");
    }
}
