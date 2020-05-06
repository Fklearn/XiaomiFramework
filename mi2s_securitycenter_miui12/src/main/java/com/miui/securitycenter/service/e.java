package com.miui.securitycenter.service;

import android.database.ContentObserver;
import android.os.Handler;
import b.b.c.j.s;

class e extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NotificationService f7532a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    e(NotificationService notificationService, Handler handler) {
        super(handler);
        this.f7532a = notificationService;
    }

    public void onChange(boolean z) {
        s.a("update_antispam");
        NotificationService.b(this.f7532a.getApplicationContext());
        this.f7532a.a(0);
    }
}
