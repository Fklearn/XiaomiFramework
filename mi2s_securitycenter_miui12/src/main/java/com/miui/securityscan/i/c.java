package com.miui.securityscan.i;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;
import com.miui.maml.folme.AnimatedProperty;
import java.security.MessageDigest;
import miui.os.Build;
import miui.telephony.TelephonyManager;

public final class c {
    public static CharSequence a(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
            if (packageInfo != null) {
                return packageInfo.applicationInfo.loadLabel(packageManager);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static final String a() {
        return Build.IS_STABLE_VERSION ? "stable" : Build.IS_ALPHA_BUILD ? AnimatedProperty.PROPERTY_NAME_ALPHA : "development";
    }

    public static String a(Context context) {
        String str;
        try {
            str = Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            e.printStackTrace();
            str = null;
        }
        return TextUtils.isEmpty(str) ? "" : str;
    }

    public static String a(PackageInfo packageInfo) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA256");
            instance.update(packageInfo.signatures[0].toByteArray());
            StringBuilder sb = new StringBuilder();
            byte[] digest = instance.digest();
            int length = digest.length;
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append(Integer.toString((digest[i] & 255) + 256, 16).substring(1));
            }
            return sb.toString().toUpperCase();
        } catch (Exception unused) {
            return "";
        }
    }

    public static void a(Context context, int i) {
        Toast.makeText(context, i, 0).show();
    }

    public static String b() {
        String smallDeviceId = TelephonyManager.getDefault().getSmallDeviceId();
        return smallDeviceId == null ? "" : smallDeviceId;
    }

    public static String b(Context context) {
        String deviceId = ((android.telephony.TelephonyManager) context.getSystemService("phone")).getDeviceId();
        return deviceId == null ? "" : deviceId;
    }

    public static String c(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == 1) {
                return "WIFI";
            }
            if (activeNetworkInfo.getType() == 0) {
                switch (activeNetworkInfo.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return "2G";
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return "3G";
                    case 13:
                        return "4G";
                    default:
                        String subtypeName = activeNetworkInfo.getSubtypeName();
                        return (subtypeName.equalsIgnoreCase("TD-SCDMA") || subtypeName.equalsIgnoreCase("WCDMA") || subtypeName.equalsIgnoreCase("CDMA2000")) ? "3G" : subtypeName;
                }
            }
        }
        return "NA";
    }

    public static String d(Context context) {
        String simOperator = ((android.telephony.TelephonyManager) context.getSystemService("phone")).getSimOperator();
        return TextUtils.isEmpty(simOperator) ? "" : simOperator;
    }

    public static boolean e(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.getType() == 0;
    }

    public static boolean f(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean g(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == 1;
    }
}
