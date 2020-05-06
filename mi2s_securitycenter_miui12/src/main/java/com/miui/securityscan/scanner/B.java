package com.miui.securityscan.scanner;

import com.miui.securityscan.scanner.O;
import miui.util.Log;

class B implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f7816a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C f7817b;

    B(C c2, int i) {
        this.f7817b = c2;
        this.f7816a = i;
    }

    public void run() {
        if (this.f7816a == 10) {
            try {
                this.f7817b.e.h.y();
                this.f7817b.e.h.x();
                if (this.f7817b.f7818a) {
                    this.f7817b.e.m.a(v.PREDICT_SYSTEM_APP, new C0558e(O.f.FINISH));
                    return;
                }
                this.f7817b.e.a(this.f7817b.f7819b, this.f7817b.e.h.k(), this.f7817b.f7820c);
            } catch (InterruptedException e) {
                O.e eVar = this.f7817b.f7821d;
                if (eVar != null) {
                    eVar.a();
                }
                Log.e("SecurityManager", "startScanSystemApps onFinishScan()  InterruptedException", e);
            }
        }
    }
}
