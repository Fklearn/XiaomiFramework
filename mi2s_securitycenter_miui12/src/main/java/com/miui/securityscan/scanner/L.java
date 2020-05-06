package com.miui.securityscan.scanner;

import com.miui.securityscan.b.d;

class L implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ M f7843a;

    L(M m) {
        this.f7843a = m;
    }

    public void run() {
        d dVar = this.f7843a.f7844a;
        if (dVar != null) {
            dVar.a();
        }
        for (com.miui.securitycenter.memory.d d2 : this.f7843a.f7845b) {
            this.f7843a.f7846c.h.a(d2.d());
        }
    }
}
