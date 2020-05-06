package com.miui.securityscan.a;

class q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f7594a;

    q(long j) {
        this.f7594a = j;
    }

    public void run() {
        G.c("newcheck_autoop_score", this.f7594a);
    }
}
