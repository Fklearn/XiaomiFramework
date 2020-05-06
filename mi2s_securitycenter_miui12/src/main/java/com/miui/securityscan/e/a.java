package com.miui.securityscan.e;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.securityscan.M;
import java.util.ArrayList;

public class a extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("AppUpdateReceiver", "receive broadcast");
        M.a(intent.getIntExtra("extra_need_update_app_count", 0));
        ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("android.intent.extra.PACKAGES");
        if (stringArrayListExtra != null) {
            M.a(stringArrayListExtra);
        }
    }
}
