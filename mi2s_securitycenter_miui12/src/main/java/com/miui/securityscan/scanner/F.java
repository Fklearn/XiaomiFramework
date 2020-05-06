package com.miui.securityscan.scanner;

import com.miui.securityscan.scanner.ScoreManager;

class F implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ G f7832a;

    F(G g) {
        this.f7832a = g;
    }

    public void run() {
        this.f7832a.f7834b.j.b("com.miui.cleanmaster.action.CHECK_GARBAGE_CHECK");
        for (ScoreManager.ResultModel a2 : this.f7832a.f7833a.values()) {
            this.f7832a.f7834b.h.a(a2);
        }
        this.f7832a.f7834b.h.v();
    }
}
