package com.miui.powercenter.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.miui.powercenter.autotask.C0489s;
import com.miui.powercenter.bootshutdown.a;

public class DateResetReceiver extends BroadcastReceiver {
    private void a(Context context) {
        C0489s.d(context);
    }

    private void b(Context context) {
        a.a(context);
        a.b(context);
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if ("android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction()) || "android.intent.action.TIME_SET".equals(intent.getAction())) {
                b(context);
                a(context);
            }
        }
    }
}
