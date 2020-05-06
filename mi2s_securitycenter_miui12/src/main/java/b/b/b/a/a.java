package b.b.b.a;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import b.b.o.g.e;
import com.google.android.exoplayer2.C;
import com.miui.securitycenter.Application;
import java.math.BigInteger;
import java.security.MessageDigest;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static String f1467a = a((Context) Application.d());

    public static String a(Context context) {
        String str;
        String str2;
        String deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        try {
            str = Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            Log.e("AdScoreAnalytics", "getDeviceId error ", e);
            str = null;
        }
        if (Build.VERSION.SDK_INT < 28) {
            str2 = Build.SERIAL;
        } else {
            try {
                str2 = (String) e.a(Class.forName("android.os.Build"), String.class, "getSerial", (Class<?>[]) new Class[0], new Object[0]);
            } catch (Exception e2) {
                Log.e("AdScoreAnalytics", "get serial number error", e2);
                str2 = "";
            }
        }
        return a(deviceId + str + str2);
    }

    private static String a(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA1");
            instance.update(str.getBytes(C.UTF8_NAME));
            return String.format("%1$032X", new Object[]{new BigInteger(1, instance.digest())});
        } catch (Exception unused) {
            return str;
        }
    }
}
