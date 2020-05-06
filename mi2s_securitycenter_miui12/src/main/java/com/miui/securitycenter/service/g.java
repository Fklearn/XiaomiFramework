package com.miui.securitycenter.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import b.b.c.j.s;
import com.miui.networkassistant.config.Constants;

class g extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NotificationService f7534a;

    g(NotificationService notificationService) {
        this.f7534a = notificationService;
    }

    public void onReceive(Context context, Intent intent) {
        NotificationService notificationService;
        boolean z;
        if (intent.getAction().equals(Constants.System.ACTION_SCREEN_ON)) {
            s.a("screen_on");
            notificationService = this.f7534a;
            z = true;
        } else {
            if (intent.getAction().equals(Constants.System.ACTION_SCREEN_OFF)) {
                s.a("screen_off");
                notificationService = this.f7534a;
                z = false;
            }
            this.f7534a.a(0);
        }
        boolean unused = notificationService.e = z;
        this.f7534a.a(0);
    }
}
