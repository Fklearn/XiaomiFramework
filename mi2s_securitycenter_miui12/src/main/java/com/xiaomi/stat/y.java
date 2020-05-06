package com.xiaomi.stat;

import com.xiaomi.stat.a.l;

class y implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8616a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f8617b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f8618c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ MiStatParams f8619d;
    final /* synthetic */ e e;

    y(e eVar, boolean z, String str, String str2, MiStatParams miStatParams) {
        this.e = eVar;
        this.f8616a = z;
        this.f8617b = str;
        this.f8618c = str2;
        this.f8619d = miStatParams;
    }

    public void run() {
        if (b.a() && this.e.g(this.f8616a) && b.A()) {
            e eVar = this.e;
            eVar.a(l.a(this.f8617b, this.f8618c, this.f8619d, eVar.f8570b, this.f8616a));
        }
    }
}
