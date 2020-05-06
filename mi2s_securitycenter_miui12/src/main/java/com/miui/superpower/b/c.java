package com.miui.superpower.b;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8074a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ f f8075b;

    c(f fVar, boolean z) {
        this.f8075b = fVar;
        this.f8074a = z;
    }

    public void run() {
        try {
            boolean unused = this.f8075b.f = this.f8074a;
            if (!this.f8075b.f && this.f8075b.g == 2 && this.f8075b.e != 0) {
                this.f8075b.b(this.f8075b.e, true);
            }
        } catch (Exception unused2) {
            this.f8075b.b();
            int unused3 = this.f8075b.g = 0;
        }
    }
}
