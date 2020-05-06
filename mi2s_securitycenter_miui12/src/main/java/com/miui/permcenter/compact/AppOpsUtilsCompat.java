package com.miui.permcenter.compact;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import com.miui.activityutil.o;
import miui.os.Build;
import miui.os.SystemProperties;

public class AppOpsUtilsCompat {
    private static final String BUILD_UTC = "ro.build.date.utc";
    public static final String TAG = "AppOpsUtilsCompat";

    public static void autoOptmize(Context context) {
        Log.i(TAG, "start auto optmize");
        try {
            if (needOpenMiuiOptimize()) {
                openMiuiOptimize(context);
            }
        } catch (Exception e) {
            Log.e(TAG, "autoOptmize exception :", e);
        }
    }

    public static boolean isXOptMode() {
        try {
            return ((Boolean) ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("android.miui.AppOpsUtils"), Boolean.TYPE, "isXOptMode", new Class[0], new Object[0])).booleanValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean needOpenMiuiOptimize() {
        try {
            String str = SystemProperties.get(BUILD_UTC);
            Log.i(TAG, "buildDate is :" + str);
            long parseLong = Long.parseLong(str);
            boolean z = true;
            boolean z2 = !Build.IS_INTERNATIONAL_BUILD && Build.IS_STABLE_VERSION && Build.VERSION.SDK_INT >= 28 && parseLong > 0 && parseLong < 1557504000;
            Log.i(TAG, "match is :" + z2);
            if (z2) {
                if (SystemProperties.getBoolean("persist.sys.miui_optimization", !o.f2310b.equals(SystemProperties.get("ro.miui.cts")))) {
                    z = false;
                }
                Log.i(TAG, "needOpenMiuiOptimize is :" + z);
                return z;
            }
        } catch (Exception e) {
            Log.e(TAG, "needOpenMiuiOptimize error ", e);
        }
        return false;
    }

    private static void openMiuiOptimize(Context context) {
        Log.i(TAG, "open miui optmize");
        SystemProperties.set("persist.sys.miui_optimization", true);
        Settings.Secure.putInt(context.getContentResolver(), "miui_optimization", 1);
    }
}
