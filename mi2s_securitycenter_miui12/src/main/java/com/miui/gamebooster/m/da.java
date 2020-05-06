package com.miui.gamebooster.m;

import java.util.ArrayList;

public class da {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4479a = "com.miui.gamebooster.m.da";

    /* renamed from: b  reason: collision with root package name */
    private static ArrayList<Integer> f4480b = new ArrayList<>(3);

    /* renamed from: c  reason: collision with root package name */
    private static int f4481c = 0;

    /* renamed from: d  reason: collision with root package name */
    public static boolean f4482d = true;

    public static final String a() {
        f4481c++;
        float c2 = (float) c();
        float b2 = (float) b();
        try {
            Thread.sleep(1000);
        } catch (Exception unused) {
        }
        float b3 = (float) b();
        float c3 = ((float) c()) - c2;
        float f = 0.0f;
        if ((c3 <= 0.0f || b3 - b2 <= 0.0f) && f4481c <= 3) {
            return a();
        }
        float f2 = ((c3 - (b3 - b2)) * 100.0f) / c3;
        if (f2 >= 0.0f) {
            f = f2;
        }
        if (f >= 100.0f) {
            f = 99.0f;
        }
        f4481c = 0;
        return String.valueOf(Math.abs((int) f));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v1, types: [java.io.Reader] */
    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static long b() {
        /*
            r0 = 0
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            java.io.InputStreamReader r2 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            java.lang.String r4 = "/proc/stat"
            r3.<init>(r4)     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            r3 = 1000(0x3e8, float:1.401E-42)
            r1.<init>(r2, r3)     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            java.lang.String r2 = r1.readLine()     // Catch:{ IOException -> 0x001f }
            java.lang.String r3 = " "
            java.lang.String[] r0 = r2.split(r3)     // Catch:{ IOException -> 0x001f }
            goto L_0x002e
        L_0x001f:
            r2 = move-exception
            goto L_0x0025
        L_0x0021:
            r1 = move-exception
            goto L_0x003d
        L_0x0023:
            r2 = move-exception
            r1 = r0
        L_0x0025:
            java.lang.String r3 = f4479a     // Catch:{ all -> 0x0039 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0039 }
            android.util.Log.e(r3, r2)     // Catch:{ all -> 0x0039 }
        L_0x002e:
            miui.util.IOUtils.closeQuietly(r1)
            r1 = 5
            r0 = r0[r1]
            long r0 = java.lang.Long.parseLong(r0)
            return r0
        L_0x0039:
            r0 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
        L_0x003d:
            miui.util.IOUtils.closeQuietly(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.da.b():long");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v1, types: [java.io.Reader] */
    /* JADX WARNING: type inference failed for: r0v5 */
    /* JADX WARNING: type inference failed for: r0v7 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static long c() {
        /*
            r0 = 0
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            java.io.InputStreamReader r2 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            java.lang.String r4 = "/proc/stat"
            r3.<init>(r4)     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            r3 = 1000(0x3e8, float:1.401E-42)
            r1.<init>(r2, r3)     // Catch:{ IOException -> 0x0023, all -> 0x0021 }
            java.lang.String r2 = r1.readLine()     // Catch:{ IOException -> 0x001f }
            java.lang.String r3 = " "
            java.lang.String[] r0 = r2.split(r3)     // Catch:{ IOException -> 0x001f }
            goto L_0x002e
        L_0x001f:
            r2 = move-exception
            goto L_0x0025
        L_0x0021:
            r1 = move-exception
            goto L_0x006e
        L_0x0023:
            r2 = move-exception
            r1 = r0
        L_0x0025:
            java.lang.String r3 = f4479a     // Catch:{ all -> 0x006a }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x006a }
            android.util.Log.e(r3, r2)     // Catch:{ all -> 0x006a }
        L_0x002e:
            miui.util.IOUtils.closeQuietly(r1)
            r1 = 2
            r1 = r0[r1]
            long r1 = java.lang.Long.parseLong(r1)
            r3 = 3
            r3 = r0[r3]
            long r3 = java.lang.Long.parseLong(r3)
            long r1 = r1 + r3
            r3 = 4
            r3 = r0[r3]
            long r3 = java.lang.Long.parseLong(r3)
            long r1 = r1 + r3
            r3 = 6
            r3 = r0[r3]
            long r3 = java.lang.Long.parseLong(r3)
            long r1 = r1 + r3
            r3 = 5
            r3 = r0[r3]
            long r3 = java.lang.Long.parseLong(r3)
            long r1 = r1 + r3
            r3 = 7
            r3 = r0[r3]
            long r3 = java.lang.Long.parseLong(r3)
            long r1 = r1 + r3
            r3 = 8
            r0 = r0[r3]
            long r3 = java.lang.Long.parseLong(r0)
            long r1 = r1 + r3
            return r1
        L_0x006a:
            r0 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
        L_0x006e:
            miui.util.IOUtils.closeQuietly(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.da.c():long");
    }
}
