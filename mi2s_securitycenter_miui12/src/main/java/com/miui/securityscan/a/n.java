package com.miui.securityscan.a;

class n implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f7591a;

    n(long j) {
        this.f7591a = j;
    }

    public void run() {
        if (G.a(this.f7591a)) {
            G.c("newcheck_autoop_time1", this.f7591a);
        }
    }
}
