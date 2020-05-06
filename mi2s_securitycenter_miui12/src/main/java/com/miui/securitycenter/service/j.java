package com.miui.securitycenter.service;

import b.b.c.j.e;
import b.b.c.j.s;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.securitycenter.service.NotificationService;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NotificationService f7537a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ NotificationService.a f7538b;

    j(NotificationService.a aVar, NotificationService notificationService) {
        this.f7538b = aVar;
        this.f7537a = notificationService;
    }

    public void run() {
        long a2 = e.a();
        long abs = Math.abs(a2 - this.f7537a.k) / 1048576;
        long currentTimeMillis = System.currentTimeMillis() - this.f7537a.g;
        s.a("memDiff : " + abs + ", timeDiff : " + currentTimeMillis);
        if (currentTimeMillis > 10000 && (this.f7537a.k == -1 || abs >= 5)) {
            s.a("cycle_memory");
            this.f7537a.a(0);
            long unused = this.f7537a.k = a2;
        }
        this.f7538b.postDelayed(this, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }
}
