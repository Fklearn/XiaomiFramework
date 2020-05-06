package com.miui.appcompatibility.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class d extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("NetConnectivityReceiver", "action=" + action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            com.miui.appcompatibility.d.b(context).b();
        }
    }
}
