package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class n implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NetAvailableEvent f8590a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e f8591b;

    n(e eVar, NetAvailableEvent netAvailableEvent) {
        this.f8591b = eVar;
        this.f8590a = netAvailableEvent;
    }

    public void run() {
        if (b.a() && this.f8591b.g(false) && b.y()) {
            e eVar = this.f8591b;
            eVar.a(l.a(this.f8590a, eVar.f8570b));
        }
    }
}
