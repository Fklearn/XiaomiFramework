package com.miui.securityscan.b;

import com.miui.securityscan.L;
import com.miui.securityscan.h.a.a;

class o implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7620a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ p f7621b;

    o(p pVar, L l) {
        this.f7621b = pVar;
        this.f7620a = l;
    }

    public void run() {
        int u = this.f7620a.u();
        if (u > 0 && this.f7621b.f7623b) {
            a.a(u);
        }
    }
}
