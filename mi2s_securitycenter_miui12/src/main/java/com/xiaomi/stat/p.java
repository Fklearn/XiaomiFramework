package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class p implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f8593a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f8594b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ long f8595c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ long f8596d;
    final /* synthetic */ e e;

    p(e eVar, int i, int i2, long j, long j2) {
        this.e = eVar;
        this.f8593a = i;
        this.f8594b = i2;
        this.f8595c = j;
        this.f8596d = j2;
    }

    public void run() {
        if (b.a() && this.e.g()) {
            this.e.a(l.a(this.f8593a, this.f8594b, this.f8595c, this.f8596d));
        }
    }
}
