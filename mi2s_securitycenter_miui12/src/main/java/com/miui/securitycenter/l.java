package com.miui.securitycenter;

import android.os.Process;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Runnable f7482a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ m f7483b;

    l(m mVar, Runnable runnable) {
        this.f7483b = mVar;
        this.f7482a = runnable;
    }

    public void run() {
        Process.setThreadPriority(10);
        this.f7482a.run();
    }
}
