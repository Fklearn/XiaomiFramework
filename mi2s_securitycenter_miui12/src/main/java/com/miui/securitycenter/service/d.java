package com.miui.securitycenter.service;

import com.miui.permcenter.privacymanager.b.s;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConnectivityChangeJobService2 f7531a;

    d(ConnectivityChangeJobService2 connectivityChangeJobService2) {
        this.f7531a = connectivityChangeJobService2;
    }

    public void run() {
        s.a(this.f7531a.getApplicationContext());
    }
}
