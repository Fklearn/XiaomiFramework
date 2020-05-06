package com.miui.antivirus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.b.a.b;

public class UpdaterReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("UpdaterReceiver", "receive broadcast");
        if (intent != null) {
            Log.i("UpdaterReceiver", "UpdaterReceiver : onReceive !");
            if ("com.android.updater.action.UPDATE_SUCCESSED".equals(intent.getAction())) {
                b.C0023b.i();
                if (intent.getIntExtra("update_type", 2) == 1) {
                    Log.i("UpdaterReceiver", "UPDATE_SUCCESS_TYPE_FULL");
                    b.C0023b.g();
                }
            }
        }
    }
}
