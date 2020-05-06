package com.android.server.am;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import android.widget.Toast;
import com.android.internal.os.BackgroundThread;

class FindDeviceAliveChecker {
    private static final long CHECK_FINDDEVICE_DEFAULT_DELAY = 300000;
    private static final long SHOW_WARNING_TOAST_INTERVAL = 60000;
    private static Handler sCheckFindDeviceHandler = new CheckFindDeviceHandler(BackgroundThread.get().getLooper());

    FindDeviceAliveChecker() {
    }

    private static Context getAMSContext() {
        return ((ActivityManagerService) ServiceManager.getService("activity")).mContext;
    }

    static void postCheckFindDeviceAliveDelayed(String processName) {
        if ("com.xiaomi.finddevice".equals(processName)) {
            postCheckFindDeviceAliveDelayedImmediately(getAMSContext(), 300000);
        }
    }

    private static void postCheckFindDeviceAliveDelayedImmediately(Context context, long delay) {
        Message msg = new Message();
        msg.obj = context;
        msg.what = 0;
        sCheckFindDeviceHandler.sendMessageDelayed(msg, delay);
    }

    /* access modifiers changed from: private */
    public static void checkFindDeviceAlive(Context context) {
        sCheckFindDeviceHandler.removeMessages(0);
        if (!pingFindDevice(context)) {
            showWarningToast(context);
            postCheckFindDeviceAliveDelayedImmediately(context, 60000);
        }
    }

    private static void showWarningToast(Context context) {
        Toast.makeText(context, "FindDevice has been destroyed.", 1).show();
    }

    private static boolean pingFindDevice(Context context) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.xiaomi.finddevice.ping.provider/ping"), (String[]) null, (String) null, (String[]) null, (String) null, (CancellationSignal) null);
        if (cursor == null || !cursor.moveToFirst()) {
            return false;
        }
        String pingResult = cursor.getString(0);
        cursor.close();
        return "Ping! I'm alive!".equals(pingResult);
    }

    private static final class CheckFindDeviceHandler extends Handler {
        private static final int WHAT_CHECK_FINDDEVICE_ALIVE = 0;

        private CheckFindDeviceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                FindDeviceAliveChecker.checkFindDeviceAlive((Context) msg.obj);
            }
        }
    }
}
