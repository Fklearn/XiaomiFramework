package com.miui.powercenter.powersaver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.y;
import com.miui.securitycenter.Application;

public class c {
    private static String a(Context context) {
        try {
            Bundle call = context.getContentResolver().call(Uri.parse("content://com.miui.powerkeeper.configure/GlobalFeatureTable"), "GlobalFeatureTablequery", (String) null, (Bundle) null);
            return (call == null || !call.containsKey("userConfigureStatus")) ? "" : call.getString("userConfigureStatus");
        } catch (Exception | IllegalArgumentException e) {
            Log.e("PowerSaverUtils", "getHideMode", e);
            return "";
        }
    }

    private static void a(Context context, String str) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("userConfigureStatus", str);
            context.getContentResolver().call(Uri.parse("content://com.miui.powerkeeper.configure/GlobalFeatureTable"), "GlobalFeatureTableupdate", (String) null, bundle);
        } catch (Exception | IllegalArgumentException e) {
            Log.e("PowerSaverUtils", "setHideMode", e);
        }
    }

    public static void a(Context context, boolean z) {
        int l = y.l();
        if (z) {
            Settings.System.putInt(context.getContentResolver(), "POWER_SAVE_PRE_CLEAN_MEMORY_TIME", l);
            if (600 != l) {
                y.d(600);
            } else {
                return;
            }
        } else if (l == 600) {
            y.d(Settings.System.getInt(context.getContentResolver(), "POWER_SAVE_PRE_CLEAN_MEMORY_TIME", 0));
        }
        Application.d().getContentResolver().notifyChange(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "key_memory_clean_time"), (ContentObserver) null, false);
    }

    private static void a(boolean z) {
        ContentResolver.setMasterSyncAutomatically(z);
    }

    private static boolean a() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    public static void b(Context context, boolean z) {
        if (Build.VERSION.SDK_INT <= 28) {
            int g = o.g(context);
            if (z) {
                Settings.System.putInt(context.getContentResolver(), "POWER_SAVE_PRE_LOCATION_MODE", g);
                if (g == 3) {
                    o.a(context, 2);
                } else if (g == 1) {
                    o.a(context, 0);
                }
            } else {
                int i = Settings.System.getInt(context.getContentResolver(), "POWER_SAVE_PRE_LOCATION_MODE", 0);
                if ((g == 2 || g == 0) && g != i) {
                    o.a(context, i);
                }
            }
        }
    }

    public static void c(Context context, boolean z) {
        String a2 = a(context);
        if (TextUtils.isEmpty(a2)) {
            Log.e("PowerSaverUtils", "hide mode status null");
        } else if (z) {
            if (!"ultimate_extra".equals(a2)) {
                a(context, "ultimate");
            }
            Settings.System.putString(context.getContentResolver(), "POWER_SAVE_PRE_HIDE_MODE", "ultimate");
        } else {
            String string = Settings.System.getString(context.getContentResolver(), "POWER_SAVE_PRE_HIDE_MODE");
            if (!"ultimate_extra".equals(Settings.System.getString(context.getContentResolver(), "EXTREME_POWER_SAVE_PRE_HIDE_MODE")) && ("ultimate".equals(string) || TextUtils.isEmpty(string))) {
                a(context, "normal");
            }
            Settings.System.putString(context.getContentResolver(), "POWER_SAVE_PRE_HIDE_MODE", "normal");
        }
    }

    public static void d(Context context, boolean z) {
        if (z) {
            boolean a2 = a();
            Settings.System.putInt(context.getContentResolver(), "POWER_SAVE_PRE_SYNCHRONIZE_ENABLE", a2 ? 1 : 0);
            if (a2) {
                a(false);
            }
        } else if (!a()) {
            boolean z2 = true;
            if (Settings.System.getInt(context.getContentResolver(), "POWER_SAVE_PRE_SYNCHRONIZE_ENABLE", 0) != 1) {
                z2 = false;
            }
            a(z2);
        }
    }
}
