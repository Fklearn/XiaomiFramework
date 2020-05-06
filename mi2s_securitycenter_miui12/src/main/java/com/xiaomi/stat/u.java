package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class u implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f8604a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long f8605b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ long f8606c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ e f8607d;

    u(e eVar, String str, long j, long j2) {
        this.f8607d = eVar;
        this.f8604a = str;
        this.f8605b = j;
        this.f8606c = j2;
    }

    public void run() {
        if (b.a() && this.f8607d.g() && b.z()) {
            this.f8607d.a(l.a(this.f8604a, this.f8605b, this.f8606c));
        }
    }
}
