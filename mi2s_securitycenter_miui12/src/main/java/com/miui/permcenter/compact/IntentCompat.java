package com.miui.permcenter.compact;

import android.content.Intent;
import android.os.IBinder;

public class IntentCompat {
    public static final String EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT";
    public static final String EXTRA_ORIGINATING_UID = "android.intent.extra.ORIGINATING_UID";
    public static final String EXTRA_ORIGINATING_URI = "android.intent.extra.ORIGINATING_URI";
    public static final String EXTRA_REFERRER = "android.intent.extra.REFERRER";
    public static final String EXTRA_REMOTE_CALLBACK = "android.intent.extra.REMOTE_CALLBACK";
    public static final String EXTRA_RESULT_NEEDED = "android.intent.extra.RESULT_NEEDED";
    public static final String EXTRA_UNINSTALL_ALL_USERS = "android.intent.extra.UNINSTALL_ALL_USERS";
    public static final String EXTRA_USER_ID = "android.intent.extra.USER_ID";
    public static final String TAG = "IntentCompat";

    public static IBinder getIBinderExtra(Intent intent, String str) {
        return (IBinder) ReflectUtilHelper.callObjectMethod(TAG, (Object) intent, IBinder.class, "getIBinderExtra", (Class<?>[]) new Class[]{String.class}, str);
    }

    public static void putExtra(Intent intent, String str, IBinder iBinder) {
        ReflectUtilHelper.callObjectMethod(TAG, intent, "putExtra", new Class[]{String.class, IBinder.class}, str, iBinder);
    }
}
