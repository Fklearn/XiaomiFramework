package com.miui.powercenter.mainui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class a extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryStatusView f7121a;

    a(BatteryStatusView batteryStatusView) {
        this.f7121a = batteryStatusView;
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
            this.f7121a.a();
        }
    }
}
