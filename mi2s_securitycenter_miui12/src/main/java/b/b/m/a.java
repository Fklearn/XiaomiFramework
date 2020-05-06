package b.b.m;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.y;
import com.miui.analytics.AnalyticsUtil;
import com.miui.securityadd.input.b;
import com.miui.securityadd.input.g;
import java.util.Map;

public class a {
    public static void a(String str, long j) {
        AnalyticsUtil.recordNumericEvent("securityadd", str, j);
    }

    public static void a(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("securityadd", str, str2);
    }

    public static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("securityadd", str, map);
    }

    private static boolean a() {
        return 1 == y.a("ro.miui.support_miui_ime_bottom", 0);
    }

    public static boolean a(Context context) {
        String str;
        if (Build.VERSION.SDK_INT < 26) {
            return false;
        }
        if (!B.f()) {
            str = "not owner space";
        } else if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            str = "not support in internation version";
        } else {
            boolean g = g(context);
            if (!g) {
                str = "isSecurityCoreSupport: " + g;
            } else {
                boolean f = f(context);
                if (f) {
                    return true;
                }
                str = "isMiVideoSupport: " + f;
            }
        }
        Log.i("Analytic_SecurityCenter", str);
        return false;
    }

    public static void b(Context context) {
        if (a()) {
            b.b(context);
            if (g.i(context)) {
                b.c(context);
                b.d(context);
            }
        }
    }

    public static void c(Context context) {
        b.a();
        b.e(context);
        b.a(context);
    }

    public static void d(Context context) {
        if (a(context)) {
            try {
                context.getContentResolver().call(Uri.parse("content://com.miui.child.home.analytics.analyticsprovider"), "analytics_method_kid_space", (String) null, (Bundle) null);
            } catch (Exception e) {
                Log.e("Analytic_SecurityCenter", "kidSpaceAnalytic", e);
            }
        }
    }

    public static void e(Context context) {
        d(context);
        b(context);
        c(context);
    }

    private static boolean f(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.video", "com.miui.childmode.video.CMVideoPlayerActivity"));
            return context.getPackageManager().resolveActivity(intent, 0) != null;
        } catch (Exception e) {
            Log.e("Analytic_SecurityCenter", "isMiVideoSupport: ", e);
            return false;
        }
    }

    private static boolean g(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycore", "com.miui.securityspace.service.KidModeSpaceService"));
            return context.getPackageManager().resolveService(intent, 0) != null;
        } catch (Exception e) {
            Log.e("Analytic_SecurityCenter", "isSecurityCoreSupport: ", e);
            return false;
        }
    }
}
