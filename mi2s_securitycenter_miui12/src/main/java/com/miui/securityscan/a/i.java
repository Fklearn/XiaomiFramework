package com.miui.securityscan.a;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f7587a;

    i(long j) {
        this.f7587a = j;
    }

    public void run() {
        if (G.a(this.f7587a)) {
            G.c("new_homepage_stay_time1", this.f7587a);
        }
    }
}
