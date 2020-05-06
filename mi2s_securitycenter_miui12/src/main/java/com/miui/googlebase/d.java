package com.miui.googlebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class d extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GoogleBaseAppInstallService f5450a;

    d(GoogleBaseAppInstallService googleBaseAppInstallService) {
        this.f5450a = googleBaseAppInstallService;
    }

    public void onReceive(Context context, Intent intent) {
        long longExtra = intent.getLongExtra("extra_download_id", -1);
        if (this.f5450a.l.a(longExtra) && "android.intent.action.DOWNLOAD_COMPLETE".equals(intent.getAction())) {
            this.f5450a.l.b(longExtra);
            if (this.f5450a.o < 100) {
                GoogleBaseAppInstallService googleBaseAppInstallService = this.f5450a;
                int unused = googleBaseAppInstallService.o = googleBaseAppInstallService.o + 10;
                GoogleBaseAppInstallService googleBaseAppInstallService2 = this.f5450a;
                googleBaseAppInstallService2.a(googleBaseAppInstallService2.g, 10);
            }
        }
    }
}
