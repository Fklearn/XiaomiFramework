package com.miui.securityscan.e;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class b extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Context context2;
        Log.d("DevModeReceiver", "receive broadcast");
        try {
            context2 = context.createPackageContext("com.android.settings", 2);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            context2 = null;
        }
        if (context2 != null) {
            context2.getSharedPreferences("development", 0).edit().putBoolean("show", intent.getBooleanExtra("show", false)).commit();
        }
    }
}
