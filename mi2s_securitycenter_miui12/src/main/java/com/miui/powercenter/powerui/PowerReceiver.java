package com.miui.powercenter.powerui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if ("com.android.systemui.OPEN_SAVE_MODE".equals(intent.getAction())) {
            k.c(context);
            k.b(context);
        }
    }
}
