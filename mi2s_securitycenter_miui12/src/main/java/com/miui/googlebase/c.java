package com.miui.googlebase;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

class c implements Handler.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GoogleBaseAppInstallService f5449a;

    c(GoogleBaseAppInstallService googleBaseAppInstallService) {
        this.f5449a = googleBaseAppInstallService;
    }

    public boolean handleMessage(Message message) {
        GoogleBaseAppInstallService googleBaseAppInstallService;
        int i;
        int i2 = message.what;
        if (i2 == 0) {
            Log.d("GoogleBaseApp", "receive request download apk");
            GoogleBaseAppInstallService googleBaseAppInstallService2 = this.f5449a;
            googleBaseAppInstallService2.a(googleBaseAppInstallService2.g, true);
        } else if (i2 == 1) {
            this.f5449a.h();
            this.f5449a.l.a();
            if (com.miui.securityscan.i.c.f(this.f5449a.g)) {
                Log.d("GoogleBaseApp", "stop due to timeout");
                googleBaseAppInstallService = this.f5449a;
                i = 3;
            } else {
                Log.d("GoogleBaseApp", "stop due to timeout as network disconnected");
                googleBaseAppInstallService = this.f5449a;
                i = 2;
            }
            googleBaseAppInstallService.a(i);
        }
        return true;
    }
}
