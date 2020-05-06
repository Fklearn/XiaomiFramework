package com.miui.superpower;

import com.miui.powercenter.utils.o;
import com.miui.superpower.b.h;

class m implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8115a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f8116b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f8117c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ o f8118d;

    m(o oVar, boolean z, int i, int i2) {
        this.f8118d = oVar;
        this.f8115a = z;
        this.f8116b = i;
        this.f8117c = i2;
    }

    public void run() {
        if (this.f8118d.b(this.f8115a, this.f8116b, this.f8117c)) {
            h.a("auto");
            o.a(this.f8118d.f8135c, true, false);
        }
    }
}
