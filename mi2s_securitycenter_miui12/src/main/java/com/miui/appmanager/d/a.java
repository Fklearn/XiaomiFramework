package com.miui.appmanager.d;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.miui.appmanager.C0322e;

public class a extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals("com.miui.packageinstaller.ACTION_INSTALL_SUCCESS")) {
            String stringExtra = intent.getStringExtra("extra_package_name");
            String stringExtra2 = intent.getStringExtra("extra_install_source");
            boolean z = true;
            if (intent.getIntExtra("extra_newinstall", 1) != 0) {
                z = false;
            }
            if (stringExtra != null && stringExtra2 != null) {
                C0322e.a(context, stringExtra, stringExtra2, z);
            }
        }
    }
}
