package com.miui.networkassistant.vpn.miui;

import android.app.IProcessObserver;
import android.util.Log;

public class ActivityManagerCompat {
    private static final String TAG = "ActivityManagerCompat";

    public static void registerProcessObserver(IProcessObserver iProcessObserver) {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke((Object) null, new Object[0]);
            cls.getMethod("registerProcessObserver", new Class[]{IProcessObserver.class}).invoke(invoke, new Object[]{iProcessObserver});
        } catch (Exception unused) {
            Log.e(TAG, "registerProcessObserver: could not get IActivityManager");
        }
    }

    public static void unRegisterProcessObserver(IProcessObserver iProcessObserver) {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke((Object) null, new Object[0]);
            cls.getMethod("unRegisterProcessObserver", new Class[]{IProcessObserver.class}).invoke(invoke, new Object[]{iProcessObserver});
        } catch (Exception unused) {
            Log.e(TAG, "unRegisterProcessObserver: could not get IActivityManager");
        }
    }
}
