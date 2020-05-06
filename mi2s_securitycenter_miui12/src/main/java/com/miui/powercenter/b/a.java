package com.miui.powercenter.b;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import miui.os.Build;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f6780a = false;

    public static void a(Context context) {
        Settings.System.putInt(context.getContentResolver(), "misettings_support_repost", 1);
    }

    public static void a(Context context, String str) {
        if (!Build.IS_GLOBAL_BUILD && str != null) {
            if (f6780a) {
                Log.d("SettingsInjector", "Try repost:" + str);
            }
            boolean z = false;
            if (Settings.System.getInt(context.getContentResolver(), "misettings_st_enable_sm", 0) == 1) {
                z = true;
            }
            if (z) {
                Intent intent = new Intent("miui." + str);
                intent.setPackage("com.xiaomi.misettings");
                intent.setFlags(16777216);
                context.sendBroadcast(intent);
                Log.d("SettingsInjector", "Notify to handle...");
            }
        }
    }
}
