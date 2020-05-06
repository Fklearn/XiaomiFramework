package com.miui.securityscan.scanner;

import com.miui.securityscan.L;
import com.miui.securityscan.ui.main.OptimizingBar;

class r implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7922a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0558e f7923b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C0572t f7924c;

    r(C0572t tVar, L l, C0558e eVar) {
        this.f7924c = tVar;
        this.f7922a = l;
        this.f7923b = eVar;
    }

    public void run() {
        OptimizingBar optimizingBar;
        C0568o oVar = (C0568o) this.f7924c.f7928b.get();
        if (oVar != null && (optimizingBar = this.f7922a.s) != null) {
            optimizingBar.a(oVar, this.f7923b.f7891c);
            OptimizingBar optimizingBar2 = this.f7922a.s;
            C0558e eVar = this.f7923b;
            optimizingBar2.a(oVar, (eVar.f7889a * 100) / eVar.f7890b);
        }
    }
}
