package com.xiaomi.analytics.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xiaomi.analytics.a.a.a;

class b extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f8298a;

    b(c cVar) {
        this.f8298a = cVar;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String a2 = a.a("Debugger");
            Log.d(a2, "action = " + action);
            if ("com.xiaomi.analytics.intent.DEBUG_ON".equals(action)) {
                a.f8282a = true;
            } else if ("com.xiaomi.analytics.intent.DEBUG_OFF".equals(action)) {
                a.f8282a = false;
            } else if ("com.xiaomi.analytics.intent.STAGING_ON".equals(action)) {
                c.f8310b = true;
            } else if ("com.xiaomi.analytics.intent.STAGING_OFF".equals(action)) {
                c.f8310b = false;
            }
        }
    }
}
