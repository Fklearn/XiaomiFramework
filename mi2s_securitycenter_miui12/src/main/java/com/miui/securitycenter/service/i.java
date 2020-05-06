package com.miui.securitycenter.service;

import android.os.HandlerThread;
import com.miui.securitycenter.service.NotificationService;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NotificationService f7536a;

    i(NotificationService notificationService) {
        this.f7536a = notificationService;
    }

    public void run() {
        this.f7536a.c();
        if (this.f7536a.h) {
            HandlerThread unused = this.f7536a.i = new HandlerThread("cycleMemory");
            this.f7536a.i.start();
            NotificationService notificationService = this.f7536a;
            NotificationService.a unused2 = notificationService.j = new NotificationService.a(notificationService, notificationService.i.getLooper());
            this.f7536a.j.a();
        }
        boolean unused3 = this.f7536a.h = false;
    }
}
