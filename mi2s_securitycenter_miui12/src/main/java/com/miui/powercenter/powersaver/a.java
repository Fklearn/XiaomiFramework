package com.miui.powercenter.powersaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class a extends BroadcastReceiver {
    private void a(Context context, Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("POWER_SAVE_MODE_OPEN", false);
        Bundle bundle = new Bundle();
        bundle.putBoolean("POWER_SAVE_MODE_OPEN", booleanExtra);
        context.getContentResolver().call(Uri.parse("content://com.miui.powercenter.powersaver"), "changePowerMode", (String) null, bundle);
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("PowerCommandReceiver", "receive broadcast");
        if (intent.getAction().equals("miui.intent.action.CHANGE_POWER_SAVE_MODE")) {
            a(context, intent);
        }
    }
}
