package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class aa implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8396a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MiStatParams f8397b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f8398c;

    aa(e eVar, boolean z, MiStatParams miStatParams) {
        this.f8398c = eVar;
        this.f8396a = z;
        this.f8397b = miStatParams;
    }

    public void run() {
        if (b.a() && this.f8398c.g(this.f8396a)) {
            e eVar = this.f8398c;
            eVar.a(l.a(this.f8397b, this.f8396a, eVar.f8570b));
        }
    }
}
