package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f8597a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f8598b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f8599c;

    q(e eVar, String str, String str2) {
        this.f8599c = eVar;
        this.f8597a = str;
        this.f8598b = str2;
    }

    public void run() {
        if (b.a() && this.f8599c.g(false)) {
            e eVar = this.f8599c;
            eVar.a(l.a(this.f8597a, this.f8598b, eVar.f8570b));
        }
    }
}
