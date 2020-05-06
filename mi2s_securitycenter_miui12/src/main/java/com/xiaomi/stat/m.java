package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class m implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ HttpEvent f8588a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e f8589b;

    m(e eVar, HttpEvent httpEvent) {
        this.f8589b = eVar;
        this.f8588a = httpEvent;
    }

    public void run() {
        if (b.a() && this.f8589b.g(false)) {
            e eVar = this.f8589b;
            eVar.a(l.a(this.f8588a, eVar.f8570b));
        }
    }
}
