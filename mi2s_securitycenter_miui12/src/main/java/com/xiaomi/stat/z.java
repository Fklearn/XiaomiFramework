package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class z implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Throwable f8620a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f8621b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ boolean f8622c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ e f8623d;

    z(e eVar, Throwable th, String str, boolean z) {
        this.f8623d = eVar;
        this.f8620a = th;
        this.f8621b = str;
        this.f8622c = z;
    }

    public void run() {
        if (b.a() && this.f8623d.g(false)) {
            e eVar = this.f8623d;
            eVar.a(l.a(this.f8620a, this.f8621b, this.f8622c, eVar.f8570b));
        }
    }
}
