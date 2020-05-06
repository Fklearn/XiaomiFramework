package com.miui.monthreport;

import com.miui.monthreport.l;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l f5648a;

    k(l lVar) {
        this.f5648a = lVar;
    }

    public void run() {
        if (!this.f5648a.h) {
            boolean unused = this.f5648a.h = true;
            new l.b(this.f5648a, (i) null).executeOnExecutor(l.f5651c, new Void[0]);
        }
    }
}
