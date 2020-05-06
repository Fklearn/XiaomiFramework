package com.miui.securitycenter.utils;

import android.text.TextUtils;
import b.b.c.j.y;
import miui.util.FeatureParser;

public class d {
    public static String a() {
        return y.a("ro.secureboot.lockstate", "");
    }

    public static boolean b() {
        return y.a("ro.debuggable", 0) == 1;
    }

    public static String c() {
        String i = i();
        if (!TextUtils.isEmpty(i)) {
            return i;
        }
        String string = FeatureParser.getString("vendor");
        return "mediatek".equals(string) ? f() : "qcom".equals(string) ? h() : "pinecone".equals(string) ? g() : i;
    }

    public static boolean d() {
        return y.a("ro.secureboot.devicelock", 0) == 1;
    }

    public static boolean e() {
        if (FeatureParser.hasFeature("is_patchrom", 1)) {
            return FeatureParser.getBoolean("is_patchrom", false);
        }
        return false;
    }

    private static String f() {
        StringBuilder sb = new StringBuilder();
        byte[] readOTP = LoadSeriNum.readOTP();
        for (byte b2 : readOTP) {
            sb.append((char) b2);
        }
        return sb.toString();
    }

    private static String g() {
        return i();
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0032  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String h() {
        /*
            r0 = 0
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0023 }
            java.io.FileReader r2 = new java.io.FileReader     // Catch:{ Exception -> 0x0023 }
            java.lang.String r3 = "/proc/serial_num"
            r2.<init>(r3)     // Catch:{ Exception -> 0x0023 }
            r3 = 256(0x100, float:3.59E-43)
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0023 }
            java.lang.String r0 = r1.readLine()     // Catch:{ Exception -> 0x001c, all -> 0x0017 }
            miui.util.IOUtils.closeQuietly(r1)
            goto L_0x002c
        L_0x0017:
            r0 = move-exception
            r4 = r1
            r1 = r0
            r0 = r4
            goto L_0x0037
        L_0x001c:
            r0 = move-exception
            r4 = r1
            r1 = r0
            r0 = r4
            goto L_0x0024
        L_0x0021:
            r1 = move-exception
            goto L_0x0037
        L_0x0023:
            r1 = move-exception
        L_0x0024:
            r1.printStackTrace()     // Catch:{ all -> 0x0021 }
            miui.util.IOUtils.closeQuietly(r0)
            java.lang.String r0 = ""
        L_0x002c:
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x0036
            java.lang.String r0 = i()
        L_0x0036:
            return r0
        L_0x0037:
            miui.util.IOUtils.closeQuietly(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.utils.d.h():java.lang.String");
    }

    private static String i() {
        return y.a("ro.boot.cpuid", "");
    }
}
