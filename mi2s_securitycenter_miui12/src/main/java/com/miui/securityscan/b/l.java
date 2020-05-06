package com.miui.securityscan.b;

import com.miui.securityscan.L;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7617a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ m f7618b;

    l(m mVar, L l) {
        this.f7618b = mVar;
        this.f7617a = l;
    }

    public void run() {
        L l = this.f7617a;
        if (!l.ta || l.sa) {
            this.f7617a.q();
        } else {
            l.f();
        }
    }
}
