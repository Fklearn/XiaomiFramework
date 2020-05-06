package com.miui.securityscan.scanner;

import com.miui.securitycenter.memory.d;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class D implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f7826a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ E f7827b;

    D(E e, List list) {
        this.f7827b = e;
        this.f7826a = list;
    }

    public void run() {
        List<d> list = this.f7826a;
        if (list != null) {
            for (d dVar : list) {
                if (dVar.a().get(1)) {
                    dVar.a(false);
                } else {
                    dVar.a(true);
                }
                this.f7827b.f7831d.h.a(dVar);
            }
        }
        try {
            this.f7827b.f7831d.h.w();
            if (!this.f7827b.f7828a) {
                this.f7827b.f7831d.a(this.f7827b.f7830c, this.f7827b.f7831d.h.h());
            } else {
                this.f7827b.f7831d.m.a(v.PREDICT_MEMORY, new C0558e(O.f.FINISH));
            }
        } catch (InterruptedException e) {
            O.e eVar = this.f7827b.f7829b;
            if (eVar != null) {
                eVar.a();
            }
            Log.e("SecurityManager", "startScanMemoryItem onFinishScan() callback InterruptedException", e);
        }
    }
}
