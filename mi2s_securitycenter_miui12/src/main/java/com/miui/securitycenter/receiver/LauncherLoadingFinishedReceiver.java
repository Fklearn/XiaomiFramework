package com.miui.securitycenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.j.B;
import com.miui.securitycenter.h;
import com.miui.securityscan.shortcut.e;
import miui.os.Build;

public class LauncherLoadingFinishedReceiver extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private static String f7514a = "com.miui.home.intent.action.LOADING_FINISHED";

    public void onReceive(Context context, Intent intent) {
        if (Build.IS_INTERNATIONAL_BUILD && B.f()) {
            if (h.j(context)) {
                Log.d("LauncherLoadingFinishedReceiver", " isLauncherLoadingFinished is true");
            } else if (intent != null && f7514a.equals(intent.getAction())) {
                Log.d("LauncherLoadingFinishedReceiver", " Receiver launcher's broadcast");
                if (!e.b(context, e.a.CLEANMASTER)) {
                    e.a(context, e.a.CLEANMASTER);
                    Log.d("LauncherLoadingFinishedReceiver", "Create cleanmaster shortcut");
                }
                h.d(context, true);
            }
        }
    }
}
