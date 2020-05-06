package com.miui.appcompatibility.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.appcompatibility.n;

public class c extends BroadcastReceiver {
    private void a(Context context, String str, String str2) {
        new b(this, str, str2, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void a(Context context, String str, String str2, int i) {
        new a(this, context, str, str2, i).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void onReceive(Context context, Intent intent) {
        if ("com.miui.appcompatibility.receiver.AppCompatStateReceiver".equals(intent.getAction())) {
            String stringExtra = intent.getStringExtra("package_name");
            PackageManager packageManager = context.getPackageManager();
            String stringExtra2 = intent.getStringExtra("app_ver");
            try {
                stringExtra2 = String.valueOf(packageManager.getPackageInfo(stringExtra, 0).versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            int intExtra = intent.getIntExtra("app_status", 0);
            Log.d("AppCompatStateReceiver", "AppCompatStateReceiver package_name=" + stringExtra + ",app_status=" + intExtra);
            a(context, stringExtra, stringExtra2, intExtra);
            if (n.a(context)) {
                a(context, stringExtra, stringExtra2);
            }
        }
    }
}
