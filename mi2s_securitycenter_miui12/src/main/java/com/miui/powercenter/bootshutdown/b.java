package com.miui.powercenter.bootshutdown;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.permcenter.compact.AppOpsUtilsCompat;
import com.miui.permission.PermissionContract;
import com.miui.powercenter.utils.u;

public class b extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("BootShutdownReceiver", "receive broadcast");
        if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
            Log.i("BootShutdownReceiver", "ACTION_SHUTDOWN received");
            if (u.e()) {
                a.c(context);
            }
            AppOpsUtilsCompat.autoOptmize(context);
        }
        try {
            if (context.getPackageManager().getPackageInfo("com.lbe.security.miui", 0).versionCode > 113) {
                context.getContentResolver().update(PermissionContract.RECORD_URI, (ContentValues) null, (String) null, (String[]) null);
            }
        } catch (Exception e) {
            Log.i("BootShutdownReceiver", "flush record exception!", e);
        }
    }
}
