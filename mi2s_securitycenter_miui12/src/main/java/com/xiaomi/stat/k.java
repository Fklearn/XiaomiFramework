package com.xiaomi.stat;

import com.xiaomi.stat.a.l;
import com.xiaomi.stat.d.r;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f8584a;

    k(e eVar) {
        this.f8584a = eVar;
    }

    public void run() {
        if (b.a() && this.f8584a.g() && b.z()) {
            long b2 = r.b();
            if (this.f8584a.a(b.r(), b2)) {
                b.a(b2);
                this.f8584a.a(l.a());
            }
        }
    }
}
