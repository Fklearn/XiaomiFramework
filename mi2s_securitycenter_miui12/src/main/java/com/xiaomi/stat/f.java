package com.xiaomi.stat;

import com.xiaomi.stat.a.c;
import com.xiaomi.stat.b.g;
import com.xiaomi.stat.d.e;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f8573a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f8574b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f8575c;

    f(e eVar, String str, boolean z) {
        this.f8575c = eVar;
        this.f8573a = str;
        this.f8574b = z;
    }

    public void run() {
        e.a();
        if (this.f8575c.f8569a) {
            b.h(this.f8573a);
        }
        b.d();
        g.a().a(b.f());
        b.a(this.f8575c.f8571c, this.f8574b);
        b.n();
        if (!this.f8575c.f8569a) {
            b.f(this.f8575c.f8570b);
        }
        this.f8575c.f();
        c.a().b();
        com.xiaomi.stat.b.e.a().execute(new g(this));
    }
}
