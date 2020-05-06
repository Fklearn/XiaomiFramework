package com.miui.powercenter.legacypowerrank;

import com.miui.securitycenter.R;
import com.miui.securityscan.i.c;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f7089a;

    e(f fVar) {
        this.f7089a = fVar;
    }

    public void run() {
        this.f7089a.f7090a.i.setEnabled(false);
        this.f7089a.f7090a.h.setEnabled(false);
        this.f7089a.f7090a.j.setEnabled(false);
        c.a(this.f7089a.f7090a.getContext(), (int) R.string.uninstall_app_done);
    }
}
