package com.miui.googlebase;

import android.util.Log;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GoogleBaseAppInstallService f5454a;

    h(GoogleBaseAppInstallService googleBaseAppInstallService) {
        this.f5454a = googleBaseAppInstallService;
    }

    public void run() {
        if (this.f5454a.c()) {
            Log.d("GoogleBaseApp", "thread-" + Thread.currentThread() + ": send download apk request");
            this.f5454a.h.sendEmptyMessage(0);
            GoogleBaseAppInstallService googleBaseAppInstallService = this.f5454a;
            googleBaseAppInstallService.a(googleBaseAppInstallService.g, 8);
            return;
        }
        this.f5454a.a(4);
        this.f5454a.stopSelf();
    }
}
