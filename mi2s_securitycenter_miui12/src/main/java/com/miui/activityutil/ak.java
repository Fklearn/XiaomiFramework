package com.miui.activityutil;

import android.os.Process;

final class ak extends Thread {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Runnable f2272a;

    ak(Runnable runnable) {
        this.f2272a = runnable;
    }

    public final void run() {
        Process.setThreadPriority(10);
        this.f2272a.run();
    }
}
