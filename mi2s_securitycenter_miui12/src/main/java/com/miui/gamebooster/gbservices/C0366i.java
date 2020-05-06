package com.miui.gamebooster.gbservices;

import android.service.notification.StatusBarNotification;

/* renamed from: com.miui.gamebooster.gbservices.i  reason: case insensitive filesystem */
class C0366i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ StatusBarNotification f4356a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0367j f4357b;

    C0366i(C0367j jVar, StatusBarNotification statusBarNotification) {
        this.f4357b = jVar;
        this.f4356a = statusBarNotification;
    }

    public void run() {
        this.f4357b.f4358a.a(this.f4356a.getNotification());
    }
}
