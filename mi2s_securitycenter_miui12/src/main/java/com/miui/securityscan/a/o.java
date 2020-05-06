package com.miui.securityscan.a;

class o implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f7592a;

    o(long j) {
        this.f7592a = j;
    }

    public void run() {
        if (G.a(this.f7592a)) {
            G.c("newcheck_result_time1", this.f7592a);
        }
    }
}
