package com.xiaomi.analytics.a.a;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

public class k {

    /* renamed from: a  reason: collision with root package name */
    private static String f8289a = "NetworkUtils";

    private static i a(int i) {
        switch (i) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
            case 16:
                return i.MN2G;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
            case 17:
                return i.MN3G;
            case 13:
            case 18:
            case 19:
                return i.MN4G;
            default:
                return i.NONE;
        }
    }

    public static i a(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.isConnectedOrConnecting()) {
                    if (Build.VERSION.SDK_INT >= 16) {
                        if (!connectivityManager.isActiveNetworkMetered()) {
                            return i.WIFI;
                        }
                    } else if (activeNetworkInfo.getType() == 1) {
                        return i.WIFI;
                    }
                    return a(((TelephonyManager) context.getSystemService("phone")).getNetworkType());
                }
            }
            return i.NONE;
        } catch (Exception e) {
            a.b(f8289a, "getNetState", e);
            return i.NONE;
        }
    }

    public static int b(Context context) {
        int i = j.f8288a[a(context).ordinal()];
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    return i != 4 ? 0 : 10;
                }
            }
        }
        return i2;
    }
}
