package com.miui.gamebooster.m;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* renamed from: com.miui.gamebooster.m.k  reason: case insensitive filesystem */
public class C0380k {

    /* renamed from: a  reason: collision with root package name */
    private static final Pattern f4497a = Pattern.compile(".*\\(([a-f[A-F][0-9]].*?)\\s.*\\)");

    /* renamed from: b  reason: collision with root package name */
    private static long f4498b = System.currentTimeMillis();

    /* renamed from: c  reason: collision with root package name */
    private static int f4499c = b();

    public static float a() {
        float f;
        long currentTimeMillis = System.currentTimeMillis();
        int b2 = b();
        float f2 = (float) (((double) (currentTimeMillis - f4498b)) / 1000.0d);
        if (f2 <= 0.0f || b2 <= 0) {
            f = -1.0f;
        } else {
            f = ((float) (b2 - f4499c)) / f2;
            f4499c = b2;
            f4498b = currentTimeMillis;
        }
        Log.d("jin", "fps: " + Math.round(f));
        return f;
    }

    private static String a(String str) {
        Matcher matcher = f4497a.matcher(str);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0096 A[SYNTHETIC, Splitter:B:31:0x0096] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x009e A[Catch:{ IOException -> 0x009a }] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00ac A[SYNTHETIC, Splitter:B:41:0x00ac] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00b4 A[Catch:{ IOException -> 0x00b0 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int b() {
        /*
            java.lang.String r0 = "service call SurfaceFlinger 1013"
            java.lang.String r1 = "IOException : "
            java.lang.String r2 = "SurfaceMonitorHelper"
            r3 = 0
            r4 = -1
            java.lang.Runtime r5 = java.lang.Runtime.getRuntime()     // Catch:{ Exception -> 0x007a, all -> 0x0077 }
            java.lang.Process r5 = r5.exec(r0)     // Catch:{ Exception -> 0x007a, all -> 0x0077 }
            int r6 = r5.waitFor()     // Catch:{ Exception -> 0x0075 }
            if (r6 == 0) goto L_0x002c
            java.lang.String r7 = "// Shell command %s status was %s"
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0075 }
            r9 = 0
            r8[r9] = r0     // Catch:{ Exception -> 0x0075 }
            r0 = 1
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x0075 }
            r8[r0] = r6     // Catch:{ Exception -> 0x0075 }
            java.lang.String r0 = java.lang.String.format(r7, r8)     // Catch:{ Exception -> 0x0075 }
            android.util.Log.e(r2, r0)     // Catch:{ Exception -> 0x0075 }
        L_0x002c:
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0075 }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0075 }
            java.io.InputStream r7 = r5.getInputStream()     // Catch:{ Exception -> 0x0075 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0075 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x0075 }
            java.lang.String r3 = r0.readLine()     // Catch:{ Exception -> 0x0070, all -> 0x006b }
            if (r3 == 0) goto L_0x004a
            java.lang.String r3 = a(r3)     // Catch:{ Exception -> 0x0070, all -> 0x006b }
            r6 = 16
            int r4 = java.lang.Integer.parseInt(r3, r6)     // Catch:{ Exception -> 0x0070, all -> 0x006b }
        L_0x004a:
            r0.close()     // Catch:{ IOException -> 0x0053 }
            if (r5 == 0) goto L_0x00a8
            r5.destroy()     // Catch:{ IOException -> 0x0053 }
            goto L_0x00a8
        L_0x0053:
            r0 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
        L_0x0059:
            r3.append(r1)
            java.lang.String r0 = r0.toString()
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            android.util.Log.e(r2, r0)
            goto L_0x00a8
        L_0x006b:
            r3 = move-exception
            r10 = r3
            r3 = r0
            r0 = r10
            goto L_0x00aa
        L_0x0070:
            r3 = move-exception
            r10 = r3
            r3 = r0
            r0 = r10
            goto L_0x007c
        L_0x0075:
            r0 = move-exception
            goto L_0x007c
        L_0x0077:
            r0 = move-exception
            r5 = r3
            goto L_0x00aa
        L_0x007a:
            r0 = move-exception
            r5 = r3
        L_0x007c:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a9 }
            r6.<init>()     // Catch:{ all -> 0x00a9 }
            java.lang.String r7 = "// Exception from service call SurfaceFlinger 1013 : "
            r6.append(r7)     // Catch:{ all -> 0x00a9 }
            java.lang.String r0 = r0.getMessage()     // Catch:{ all -> 0x00a9 }
            r6.append(r0)     // Catch:{ all -> 0x00a9 }
            java.lang.String r0 = r6.toString()     // Catch:{ all -> 0x00a9 }
            android.util.Log.e(r2, r0)     // Catch:{ all -> 0x00a9 }
            if (r3 == 0) goto L_0x009c
            r3.close()     // Catch:{ IOException -> 0x009a }
            goto L_0x009c
        L_0x009a:
            r0 = move-exception
            goto L_0x00a2
        L_0x009c:
            if (r5 == 0) goto L_0x00a8
            r5.destroy()     // Catch:{ IOException -> 0x009a }
            goto L_0x00a8
        L_0x00a2:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            goto L_0x0059
        L_0x00a8:
            return r4
        L_0x00a9:
            r0 = move-exception
        L_0x00aa:
            if (r3 == 0) goto L_0x00b2
            r3.close()     // Catch:{ IOException -> 0x00b0 }
            goto L_0x00b2
        L_0x00b0:
            r3 = move-exception
            goto L_0x00b8
        L_0x00b2:
            if (r5 == 0) goto L_0x00ce
            r5.destroy()     // Catch:{ IOException -> 0x00b0 }
            goto L_0x00ce
        L_0x00b8:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            java.lang.String r1 = r3.toString()
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            android.util.Log.e(r2, r1)
        L_0x00ce:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.C0380k.b():int");
    }
}
