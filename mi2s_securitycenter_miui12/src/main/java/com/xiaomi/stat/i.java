package com.xiaomi.stat;

import com.xiaomi.stat.a.c;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8579a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e f8580b;

    i(e eVar, boolean z) {
        this.f8580b = eVar;
        this.f8579a = z;
    }

    public void run() {
        if (b.d(this.f8580b.f8571c)) {
            int i = 2;
            if (!this.f8579a && b.e(this.f8580b.f8571c) != 2) {
                c.a().a(this.f8580b.f8571c);
            }
            String b2 = this.f8580b.f8571c;
            if (this.f8579a) {
                i = 1;
            }
            b.a(b2, i);
        }
    }
}
