package com.miui.securityscan.a;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f7590a;

    l(long j) {
        this.f7590a = j;
    }

    public void run() {
        if (G.a(this.f7590a)) {
            G.c("newcheck_scantime1", this.f7590a);
        }
    }
}
