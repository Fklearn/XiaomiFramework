package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import miui.log.SystemLogSwitchesConfigManager;

final class SystemLogSwitchesConfigReceiver extends BroadcastReceiver {
    SystemLogSwitchesConfigReceiver() {
    }

    public void onReceive(Context context, final Intent intent) {
        new Thread(new Runnable() {
            public void run() {
                SystemLogSwitchesConfigManager.updateLogSwitches(intent);
            }
        }).start();
    }
}
