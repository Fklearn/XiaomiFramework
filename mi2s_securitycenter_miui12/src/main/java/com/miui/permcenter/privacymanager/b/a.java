package com.miui.permcenter.privacymanager.b;

import com.miui.common.persistence.b;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f6345a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f6346b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ b f6347c;

    a(b bVar, long j, String str) {
        this.f6347c = bVar;
        this.f6345a = j;
        this.f6346b = str;
    }

    public void run() {
        try {
            b.b(this.f6347c.g(), this.f6345a);
            this.f6347c.c(this.f6346b);
            this.f6347c.i();
        } catch (Exception e) {
            b bVar = this.f6347c;
            bVar.a("recordAsync error on package: " + this.f6346b, (Throwable) e);
        }
    }
}
