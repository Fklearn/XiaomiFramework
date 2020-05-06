package com.miui.superpower.b;

import com.miui.powercenter.utils.o;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f8076a;

    d(f fVar) {
        this.f8076a = fVar;
    }

    public void run() {
        try {
            int i = o.m(this.f8076a.f8083c) ? 1 : o.l(this.f8076a.f8083c) ? 2 : 0;
            if (!(this.f8076a.e == i || this.f8076a.e == 0 || this.f8076a.g != 2)) {
                this.f8076a.b(this.f8076a.e);
            }
            int unused = this.f8076a.e = i;
            this.f8076a.a(this.f8076a.e);
        } catch (Exception unused2) {
            this.f8076a.b();
            int unused3 = this.f8076a.g = 0;
        }
    }
}
