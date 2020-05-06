package com.xiaomi.stat.b;

import com.xiaomi.stat.d.l;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b f8437a;

    c(b bVar) {
        this.f8437a = bVar;
    }

    public void run() {
        if (l.a()) {
            this.f8437a.f8436a.a(false, false);
            i.a().a(false);
            d.a().b();
        }
    }
}
