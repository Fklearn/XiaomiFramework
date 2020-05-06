package com.miui.securitycenter.service;

import com.miui.cleanmaster.a;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConnectivityChangeJobService2 f7530a;

    c(ConnectivityChangeJobService2 connectivityChangeJobService2) {
        this.f7530a = connectivityChangeJobService2;
    }

    public void run() {
        a.a(this.f7530a.getApplicationContext());
    }
}
