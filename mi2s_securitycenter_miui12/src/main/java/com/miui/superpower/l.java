package com.miui.superpower;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8111a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f8112b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f8113c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ o f8114d;

    l(o oVar, boolean z, int i, int i2) {
        this.f8114d = oVar;
        this.f8111a = z;
        this.f8112b = i;
        this.f8113c = i2;
    }

    public void run() {
        if (this.f8114d.c(this.f8111a, this.f8112b, this.f8113c)) {
            this.f8114d.a(false, false);
        }
    }
}
