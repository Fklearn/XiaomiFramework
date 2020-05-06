package com.miui.powercenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

class w extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f7365a;

    w(x xVar) {
        this.f7365a = xVar;
    }

    public void onReceive(Context context, Intent intent) {
        if ("miui.intent.action.ACTION_WIRELESS_CHARGING".equals(intent.getAction())) {
            int intExtra = intent.getIntExtra("miui.intent.extra.WIRELESS_CHARGING", 1);
            Log.d("PowerSettings", "receive broadcast " + intExtra);
            if (this.f7365a.s != null) {
                this.f7365a.s.setChecked(this.f7365a.u.b());
            }
        } else if ("miui.intent.action.EXTREME_POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
            this.f7365a.c();
        }
    }
}
