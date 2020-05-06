package com.miui.securitycenter;

import android.os.Process;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Runnable f7478a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ k f7479b;

    j(k kVar, Runnable runnable) {
        this.f7479b = kVar;
        this.f7478a = runnable;
    }

    public void run() {
        Process.setThreadPriority(10);
        this.f7478a.run();
    }
}
