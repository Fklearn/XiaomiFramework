package com.android.server;

import android.content.pm.ApplicationInfo;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.StrictMode;

public class ServiceThread extends HandlerThread {
    private static final String TAG = "ServiceThread";
    private final boolean mAllowIo;

    public ServiceThread(String name, int priority, boolean allowIo) {
        super(name, priority);
        this.mAllowIo = allowIo;
    }

    public void run() {
        Process.setCanSelfBackground(false);
        if (!this.mAllowIo) {
            StrictMode.initThreadDefaults((ApplicationInfo) null);
        }
        super.run();
    }

    public synchronized void start() {
        super.start();
        Looper looper = getLooper();
        if (looper != null) {
            looper.getMessageMonitor().enableMonitorMessage(true);
        }
    }
}
