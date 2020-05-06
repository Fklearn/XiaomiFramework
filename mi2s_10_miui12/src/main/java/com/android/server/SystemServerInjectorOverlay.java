package com.android.server;

import android.util.Slog;

public class SystemServerInjectorOverlay {
    private static final String TAG = "SystemServer";

    private static void reportWtf(String msg, Throwable e) {
        Slog.w(TAG, "***********************************************");
        Slog.wtf(TAG, "BOOT FAILURE " + msg, e);
    }
}
