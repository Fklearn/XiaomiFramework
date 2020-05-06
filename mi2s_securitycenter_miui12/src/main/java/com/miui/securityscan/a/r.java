package com.miui.securityscan.a;

class r implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f7595a;

    r(long j) {
        this.f7595a = j;
    }

    public void run() {
        G.c("newcheck_finish_score", this.f7595a);
    }
}
