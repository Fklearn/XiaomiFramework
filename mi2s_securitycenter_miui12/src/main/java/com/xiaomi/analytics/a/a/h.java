package com.xiaomi.analytics.a.a;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import com.miui.networkassistant.config.Constants;

public class h {
    public static boolean a() {
        try {
            return ((Boolean) Class.forName("miui.os.Build").getField("IS_ALPHA_BUILD").get((Object) null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    @TargetApi(17)
    public static boolean a(Context context) {
        try {
            if (Build.VERSION.SDK_INT < 17) {
                return true;
            }
            boolean z = false;
            if (Settings.Global.getInt(context.getContentResolver(), Constants.System.DEVICE_PROVISIONED, 0) != 0) {
                z = true;
            }
            if (!z) {
                a.c("MIUI", "Provisioned: " + z);
            }
            return z;
        } catch (Exception e) {
            a.b("MIUI", "isDeviceProvisioned exception", e);
            return true;
        }
    }

    public static boolean a(Context context, String str) {
        if (a(context)) {
            return false;
        }
        a.c(str, "should not access network or location, not provisioned");
        return true;
    }

    public static boolean b() {
        try {
            return ((Boolean) Class.forName("miui.os.Build").getField("IS_DEVELOPMENT_VERSION").get((Object) null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean c() {
        try {
            return ((Boolean) Class.forName("miui.os.Build").getField("IS_INTERNATIONAL_BUILD").get((Object) null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean d() {
        try {
            return ((Boolean) Class.forName("miui.os.Build").getField("IS_STABLE_VERSION").get((Object) null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }
}
