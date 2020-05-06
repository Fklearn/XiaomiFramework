package com.miui.securityscan.cards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.securityscan.c.a;

public class DownloadInstallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("errorCode", -100);
        if (a.f7625a) {
            Log.d("DownloadInstallReceiver", "onReceive code : " + intExtra);
        }
        if (intExtra == -6 || intExtra == -3 || intExtra == -2 || intExtra == -1 || intExtra == 1 || intExtra == 2 || intExtra == 3 || intExtra == 4) {
            g.a(context).a(intent.getStringExtra("packageName"), intExtra);
        } else if (intExtra == 5) {
            String stringExtra = intent.getStringExtra("packageName");
            int intExtra2 = intent.getIntExtra("progress", -1);
            if (intExtra2 != 100) {
                g.a(context).a(stringExtra, intExtra, intExtra2);
            }
        }
    }
}
