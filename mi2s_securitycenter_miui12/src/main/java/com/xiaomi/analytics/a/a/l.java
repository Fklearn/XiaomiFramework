package com.xiaomi.analytics.a.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.miui.luckymoney.config.Constants;
import com.miui.permcenter.permissions.D;

public class l {

    /* renamed from: a  reason: collision with root package name */
    private static String f8290a;

    public static String a() {
        return m.a("ro.build.version.sdk", "");
    }

    public static void a(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("_m_rec", 0);
            if (!sharedPreferences.getBoolean("has_deleted_id", false)) {
                sharedPreferences.edit().remove(Constants.JSON_KEY_IMEI).apply();
                sharedPreferences.edit().putBoolean("has_deleted_id", true).apply();
            }
        } catch (Exception e) {
            a.b("SysUtils", "deleteDeviceIdInSpFile exception", e);
        }
    }

    public static String b() {
        return m.a("ro.build.product", "");
    }

    public static String b(Context context) {
        String c2 = c(context);
        return !TextUtils.isEmpty(c2) ? p.a(c2) : "";
    }

    public static String c() {
        return h.a() ? "A" : h.d() ? "S" : h.b() ? D.f6221a : "";
    }

    private static String c(Context context) {
        if (TextUtils.isEmpty(f8290a)) {
            try {
                f8290a = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
            } catch (Exception e) {
                a.c("SysUtils", "getIMEI failed!", e);
            }
        }
        return f8290a;
    }

    public static String d() {
        return Build.VERSION.INCREMENTAL;
    }

    public static String e() {
        try {
            String a2 = m.a("ro.miui.region", "");
            return TextUtils.isEmpty(a2) ? m.a("ro.product.locale.region", "") : a2;
        } catch (Exception e) {
            a.b("SysUtils", "getRegion Exception: ", e);
            return "";
        }
    }
}
