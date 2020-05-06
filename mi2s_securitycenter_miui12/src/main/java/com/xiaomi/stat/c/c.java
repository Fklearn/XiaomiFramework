package com.xiaomi.stat.c;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.miui.networkassistant.config.Constants;
import com.xiaomi.stat.ak;
import com.xiaomi.stat.b;
import com.xiaomi.stat.d;
import com.xiaomi.stat.d.i;
import java.util.Map;

public class c {

    /* renamed from: a  reason: collision with root package name */
    public static final String f8472a = "com.xiaomi.xmsf";

    /* renamed from: b  reason: collision with root package name */
    public static final String f8473b = "com.xiaomi.xmsf.push.service.HttpService";

    /* renamed from: c  reason: collision with root package name */
    private static final String f8474c = "UploadMode";

    private static ServiceConnection a(String str, Map<String, String> map, String[] strArr) {
        return new d(strArr, str, map);
    }

    public static String a(String str, Map<String, String> map, boolean z) {
        return (!b.u() || !a()) ? i.b(str, map, z) : b(str, map, z);
    }

    public static boolean a() {
        boolean z;
        boolean z2;
        Context a2 = ak.a();
        if (a2 == null) {
            return false;
        }
        try {
            z = (a2.getApplicationInfo().flags & 1) == 1;
            try {
                PackageManager packageManager = a2.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(a2.getPackageName(), 64);
                PackageInfo packageInfo2 = packageManager.getPackageInfo(Constants.System.ANDROID_PACKAGE_NAME, 64);
                if (!(packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length <= 0 || packageInfo2 == null || packageInfo2.signatures == null || packageInfo2.signatures.length <= 0)) {
                    z2 = packageInfo2.signatures[0].equals(packageInfo.signatures[0]);
                    return !z || z2;
                }
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            z = false;
        }
        z2 = false;
        if (!z) {
        }
    }

    public static String b(String str, Map<String, String> map, boolean z) {
        if (z) {
            map.put(d.f, i.a(map));
        }
        Intent intent = new Intent();
        intent.setClassName("com.xiaomi.xmsf", f8473b);
        Context a2 = ak.a();
        String[] strArr = new String[1];
        if (a2 == null) {
            return strArr[0];
        }
        ServiceConnection a3 = a(str, map, strArr);
        boolean bindService = a2.bindService(intent, a3, 1);
        synchronized (i.class) {
            try {
                i.class.wait(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!bindService) {
            strArr[0] = null;
        }
        if (bindService) {
            a2.unbindService(a3);
        }
        return strArr[0];
    }
}
