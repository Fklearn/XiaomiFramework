package com.miui.securityscan.scanner;

import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class y implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f7936a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ List f7937b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ A f7938c;

    y(A a2, int i, List list) {
        this.f7938c = a2;
        this.f7936a = i;
        this.f7937b = list;
    }

    public void run() {
        if (this.f7936a == 11) {
            try {
                if (this.f7937b != null) {
                    this.f7938c.f7815c.h.c(this.f7937b);
                }
                if (this.f7938c.f7813a) {
                    this.f7938c.f7815c.m.a(v.PREDICT_AUTO_ITEM, new C0558e(O.f.FINISH));
                } else {
                    this.f7938c.f7815c.n.a(C0568o.SYSTEM_CONFIG, new C0558e(O.f.FINISH));
                }
            } catch (InterruptedException e) {
                O.e eVar = this.f7938c.f7814b;
                if (eVar != null) {
                    eVar.a();
                }
                Log.e("SecurityManager", "startScanAutoItem onFinishScan()  InterruptedException", e);
            }
        }
    }
}
