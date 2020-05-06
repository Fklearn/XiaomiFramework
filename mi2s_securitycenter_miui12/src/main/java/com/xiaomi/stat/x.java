package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class x implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f8612a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long f8613b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ long f8614c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ MiStatParams f8615d;
    final /* synthetic */ e e;

    x(e eVar, String str, long j, long j2, MiStatParams miStatParams) {
        this.e = eVar;
        this.f8612a = str;
        this.f8613b = j;
        this.f8614c = j2;
        this.f8615d = miStatParams;
    }

    public void run() {
        if (b.a() && this.e.g(false) && b.z()) {
            e eVar = this.e;
            String str = this.f8612a;
            long j = this.f8613b;
            eVar.a(l.a(str, j - this.f8614c, j, this.f8615d, eVar.f8570b));
        }
    }
}
