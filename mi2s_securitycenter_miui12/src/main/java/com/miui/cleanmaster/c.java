package com.miui.cleanmaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.networkassistant.config.Constants;

public class c extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private static final String f3742a = "c";

    public void onReceive(Context context, Intent intent) {
        boolean a2 = e.a(context);
        if (a2) {
            String str = f3742a;
            Log.i(str, " cm need send notification = " + a2);
            if (Constants.System.ACTION_USER_PRESENT.equals(intent.getAction())) {
                m.b(context, true);
                m.a(context, true);
                m.c(context, true);
            }
        }
    }
}
