package com.android.server.location;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

public final class GnssLocationProviderInjector {
    public static String CALLER_PACKAGE_NAME_ACTION = "com.xiaomi.bsp.gps.nps.callerName";
    public static int FIRST_FIX = 3;
    public static int LOSE = 4;
    public static int RECOVER = 5;
    public static int START = 1;
    public static int STOP = 2;
    private static String TAG = "GnssLocationProviderInjector";

    private GnssLocationProviderInjector() {
    }

    public static void notifyState(Context context, int event) {
        String str = TAG;
        Log.d(str, "gps now on " + event);
        deliverIntent(context, event);
    }

    public static void notifyCallerName(Context context, String pkgName) {
        String str = TAG;
        Log.d(str, "caller name: " + pkgName);
        deliverCallerNameIntent(context, pkgName);
    }

    private static void deliverIntent(Context context, int event) {
        Intent toNpsReceiver = new Intent();
        toNpsReceiver.setAction("com.xiaomi.bsp.gps.nps.GetEvent");
        toNpsReceiver.putExtra("com.xiaomi.bsp.gps.nps.NewEvent", event);
        toNpsReceiver.setClassName("com.xiaomi.bsp.gps.nps", "com.xiaomi.bsp.gps.nps.GnssEventReceiver");
        context.sendBroadcastAsUser(toNpsReceiver, UserHandle.CURRENT_OR_SELF);
    }

    private static void deliverCallerNameIntent(Context context, String packageName) {
        Intent toNpsReceiver = new Intent();
        toNpsReceiver.setAction(CALLER_PACKAGE_NAME_ACTION);
        toNpsReceiver.putExtra("com.xiaomi.bsp.gps.nps.pkgNname", packageName);
        toNpsReceiver.setClassName("com.xiaomi.bsp.gps.nps", "com.xiaomi.bsp.gps.nps.GnssCallerNameEventReceiver");
        context.sendBroadcastAsUser(toNpsReceiver, UserHandle.CURRENT_OR_SELF);
    }
}
