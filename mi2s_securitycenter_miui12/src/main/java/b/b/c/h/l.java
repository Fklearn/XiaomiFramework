package b.b.c.h;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import b.b.o.g.e;

public class l {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f1739a = "l";

    public static void a(Context context, boolean z) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                k kVar = new k();
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
                if (z) {
                    Class[] clsArr = new Class[3];
                    clsArr[0] = Integer.TYPE;
                    clsArr[1] = Boolean.TYPE;
                    clsArr[2] = Class.forName("android.net.ConnectivityManager$OnStartTetheringCallback");
                    e.a((Object) connectivityManager, "startTethering", (Class<?>[]) clsArr, 0, true, kVar);
                    return;
                }
                e.a((Object) connectivityManager, "stopTethering", (Class<?>[]) new Class[]{Integer.TYPE}, 0);
                return;
            }
            e.a((Object) (WifiManager) context.getApplicationContext().getSystemService("wifi"), "setWifiApEnabled", (Class<?>[]) new Class[]{WifiConfiguration.class, Boolean.TYPE}, null, Boolean.valueOf(z));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
