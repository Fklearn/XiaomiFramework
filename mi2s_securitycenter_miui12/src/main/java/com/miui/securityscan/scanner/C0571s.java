package com.miui.securityscan.scanner;

import android.util.Log;
import com.miui.securityscan.L;
import com.miui.securityscan.i.o;
import com.miui.securityscan.ui.main.OptimizingBar;

/* renamed from: com.miui.securityscan.scanner.s  reason: case insensitive filesystem */
class C0571s implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7925a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0572t f7926b;

    C0571s(C0572t tVar, L l) {
        this.f7926b = tVar;
        this.f7925a = l;
    }

    public void run() {
        OptimizingBar optimizingBar;
        C0568o oVar = (C0568o) this.f7926b.f7928b.get();
        if (oVar != null && (optimizingBar = this.f7925a.s) != null) {
            optimizingBar.a(oVar);
            oVar.a(o.a(this.f7925a.getContext(), oVar));
            Log.d("PopOptimizeEntryListener", "PopOptimizeEntryListener  onFinishScan");
            this.f7925a.p();
        }
    }
}
