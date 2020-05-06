package com.android.server.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Slog;

final class DisplayProjectionReceiver extends BroadcastReceiver {
    public static final String ACTION_START_PROJECTION = "miui.action.START_PROJECTION";
    public static final String ACTION_STOP_PROJECTION = "miui.action.STOP_PROJECTION";
    private static final String TAG = "DisplayProjectionReceiver";
    /* access modifiers changed from: private */
    public MiuiProjectionManager mProjectionManager;

    public DisplayProjectionReceiver(Context context, Handler handler, WifiDisplayController controller) {
        this.mProjectionManager = new MiuiProjectionManager(context, handler, controller);
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_START_PROJECTION)) {
            final String iface = intent.getStringExtra("iface");
            Slog.d(TAG, "start projection: " + iface);
            if (iface != null) {
                new Thread() {
                    public void run() {
                        DisplayProjectionReceiver.this.mProjectionManager.startProjection(iface);
                    }
                }.start();
            }
        } else if (action.equals(ACTION_STOP_PROJECTION)) {
            Slog.d(TAG, "stop active projection ");
            new Thread() {
                public void run() {
                    DisplayProjectionReceiver.this.mProjectionManager.stopProjection();
                }
            }.start();
        }
    }
}
