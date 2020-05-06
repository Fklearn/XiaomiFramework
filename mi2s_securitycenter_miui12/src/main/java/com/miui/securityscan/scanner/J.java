package com.miui.securityscan.scanner;

import com.miui.securityscan.b.d;
import com.miui.securityscan.scanner.O;

class J implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ K f7840a;

    J(K k) {
        this.f7840a = k;
    }

    public void run() {
        d dVar = this.f7840a.f7841a;
        if (dVar != null) {
            dVar.b();
        }
        try {
            this.f7840a.f7842b.n.a(C0568o.SYSTEM_APP, new C0558e(O.f.FINISH));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
