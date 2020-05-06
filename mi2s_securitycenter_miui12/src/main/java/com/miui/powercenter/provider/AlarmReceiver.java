package com.miui.powercenter.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.o.f.c.a;
import com.miui.common.persistence.b;
import com.miui.powercenter.quickoptimize.z;

public class AlarmReceiver extends BroadcastReceiver {
    private void a(Context context) {
        z.a(context, (z.a) new a(this, context.getApplicationContext()));
    }

    private void b(Context context) {
        boolean a2 = a.a(context).a();
        b.b("key_last_mobile_data_enabled", a2);
        if (a2) {
            a.a(context).a(false);
            Log.i("AlarmReceiver", "Disable mobile data");
        }
    }

    private void c(Context context) {
        if (b.a("key_last_mobile_data_enabled", false)) {
            a.a(context).a(true);
            b.b("key_last_mobile_data_enabled", false);
            Log.i("AlarmReceiver", "Enable mobile data");
        }
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "receive broadcast");
        String action = intent.getAction();
        if ("com.miui.powercenter.action.TRY_ENABLE_MOBILE_DATA".equals(action)) {
            c(context);
        } else if ("com.miui.powercenter.action.DISABLE_MOBILE_DATA".equals(action)) {
            b(context);
        } else if ("com.miui.powercenter.action.CLEAN_MEMORY".equals(action)) {
            Log.i("AlarmReceiver", "Execute clean memory on lock screen");
            a(context);
        }
    }
}
