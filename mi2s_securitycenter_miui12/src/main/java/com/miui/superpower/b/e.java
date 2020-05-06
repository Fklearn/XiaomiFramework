package com.miui.superpower.b;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8077a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f8078b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f8079c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ f f8080d;

    e(f fVar, boolean z, int i, int i2) {
        this.f8080d = fVar;
        this.f8077a = z;
        this.f8078b = i;
        this.f8079c = i2;
    }

    public void run() {
        if (this.f8080d.e != 0) {
            try {
                if (!this.f8077a || this.f8080d.g != 2) {
                    this.f8080d.a(this.f8078b, this.f8079c);
                } else {
                    this.f8080d.b(this.f8080d.e);
                }
            } catch (Exception unused) {
                this.f8080d.b();
                int unused2 = this.f8080d.g = 0;
            }
        }
    }
}
