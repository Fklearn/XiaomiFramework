package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f8581a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f8582b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f8583c;

    j(e eVar, int i, int i2) {
        this.f8583c = eVar;
        this.f8581a = i;
        this.f8582b = i2;
    }

    public void run() {
        if (b.a() && this.f8583c.g()) {
            b.e(this.f8581a);
            this.f8583c.a(l.a(this.f8582b));
        }
    }
}
