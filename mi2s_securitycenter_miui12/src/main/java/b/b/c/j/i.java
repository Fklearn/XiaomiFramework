package b.b.c.j;

import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.c;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.permcenter.compact.MiuiSettingsCompat;
import com.miui.securityscan.M;
import com.miui.securityscan.i.m;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.os.SystemProperties;

public class i {
    public static int a(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static Address a(Context context, Location location) {
        try {
            List<Address> fromLocation = new Geocoder(context, Locale.getDefault()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (fromLocation.size() > 0) {
                return fromLocation.get(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String a() {
        return y.a("ro.miui.product.home", "com.miui.home");
    }

    public static String a(Context context) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getSubscriberIdForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(c()));
        return a2.f();
    }

    private static String a(Context context, String str) {
        return str.startsWith("46001") ? TelephonyUtil.UNICOM : (str.startsWith("46003") || str.startsWith("460003")) ? TelephonyUtil.TELECOM : (str.startsWith("46000") || str.startsWith("46002") || str.startsWith("46007")) ? TelephonyUtil.CMCC : "OTHER";
    }

    private static boolean a(Context context, int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getSimOperatorForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        String f = a2.f();
        return TextUtils.isEmpty(f) || f.startsWith("460");
    }

    public static final String b() {
        return Build.IS_STABLE_VERSION ? "stable" : Build.IS_ALPHA_BUILD ? AnimatedProperty.PROPERTY_NAME_ALPHA : "development";
    }

    public static String b(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "android_id");
        return string == null ? "" : string;
    }

    public static int c() {
        c.a a2 = c.a.a("miui.telephony.SubscriptionManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getDefaultDataSlotId", (Class<?>[]) null, new Object[0]);
        int c2 = a2.c();
        if (c2 < 0 || c2 > 1) {
            return 0;
        }
        return c2;
    }

    public static String c(Context context) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getDeviceId", (Class<?>[]) null, new Object[0]);
        String f = a2.f();
        return f == null ? "" : f;
    }

    public static String d(Context context) {
        String a2 = a(context);
        return TextUtils.isEmpty(a2) ? "OTHER" : a(context, a2);
    }

    public static boolean d() {
        return y.a("ro.hardware.fp.fod", false);
    }

    public static String e(Context context) {
        String a2 = a(context);
        if (TextUtils.isEmpty(a2)) {
            return "OTHER";
        }
        String a3 = a(context, a2);
        return "OTHER".equals(a3) ? a2.substring(0, 8) : a3;
    }

    public static boolean e() {
        return SystemProperties.getInt("ro.miui.notch", 0) == 1;
    }

    public static int f(Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("status_bar_height", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
        if (identifier > 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static boolean f() {
        return y.a("ro.miui.google.csp", false);
    }

    public static boolean g(Context context) {
        return a(context, c());
    }

    public static boolean h(Context context) {
        return MiuiSettingsCompat.isNavigationBarFullScreen(context, "force_fsg_nav_bar");
    }

    public static boolean i(Context context) {
        return ((PowerManager) context.getSystemService("power")).isScreenOn();
    }

    public static boolean j(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), "miui_new_version");
        Log.d("SecurityCenterDebug", "curVersion : " + Build.VERSION.INCREMENTAL + ", newVersion : " + string);
        if (TextUtils.isEmpty(string) || Build.VERSION.INCREMENTAL.equals(string)) {
            return false;
        }
        String f = M.f();
        M.b(string);
        if (TextUtils.isEmpty(f) || f.equals(string)) {
            return true;
        }
        m.b("MIUI_UPDATE");
        return true;
    }

    public static boolean k(Context context) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("isVoiceCapable", (Class<?>[]) null, new Object[0]);
        return a2.a();
    }

    public static void l(Context context) {
        ((PowerManager) context.getSystemService("power")).reboot((String) null);
    }
}
