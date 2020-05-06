package com.miui.antispam.policy.a;

import com.miui.antispam.db.d;

class c extends Thread {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f2363a;

    c(d dVar) {
        this.f2363a = dVar;
    }

    public void run() {
        this.f2363a.d();
        this.f2363a.c();
        this.f2363a.f2367d.set(!this.f2363a.e.get() && d.e());
    }
}
