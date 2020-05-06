package com.miui.powercenter.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import b.b.c.j.y;
import b.b.o.g.c;
import com.miui.powercenter.a.a;
import miui.os.Build;

public class g {
    public static void a(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "power_center_5g_save_mode", i);
        Log.d("FiveGUtils", "start 5g save enable " + i);
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        a.a(z);
    }

    public static void a(boolean z) {
        try {
            c.a a2 = c.a.a("miui.telephony.TelephonyManager");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("setUserFiveGEnabled", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("FiveGUtils", "set5GModeEnabled failed: " + e);
        }
    }

    public static boolean a() {
        try {
            c.a a2 = c.a.a("miui.telephony.TelephonyManager");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            c.a(a2.b(), "isUserFiveGEnabled", (Class<?>[]) new Class[0], new Object[0]);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean a(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        return telephonyManager.getSimState() == 1 || telephonyManager.getSimState() == 0;
    }

    public static int b(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "power_center_5g_save_mode", 1);
    }

    public static boolean b() {
        return "andromeda".equals(Build.DEVICE) || y.a("ro.vendor.radio.5g", 0) > 0;
    }

    public static boolean c(Context context) {
        try {
            return 1 == Settings.Global.getInt(context.getContentResolver(), "fiveg_user_enable", 1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
