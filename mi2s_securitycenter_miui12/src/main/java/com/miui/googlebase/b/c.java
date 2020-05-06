package com.miui.googlebase.b;

public class c {
    /* JADX WARNING: type inference failed for: r3v0 */
    /* JADX WARNING: type inference failed for: r3v1, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r3v2 */
    /* JADX WARNING: type inference failed for: r3v3, types: [java.net.HttpURLConnection] */
    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: type inference failed for: r3v5, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r3v6 */
    /* JADX WARNING: type inference failed for: r3v9 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String a(java.lang.String r10, b.b.c.h.j r11) {
        /*
            java.lang.String r0 = "CloudUpdate"
            boolean r1 = com.miui.securitycenter.h.i()
            java.lang.String r2 = ""
            if (r1 != 0) goto L_0x000b
            return r2
        L_0x000b:
            java.net.URL r1 = new java.net.URL     // Catch:{ MalformedURLException -> 0x00cc }
            r1.<init>(r10)     // Catch:{ MalformedURLException -> 0x00cc }
            r10 = -1
            r3 = 0
            r4 = 0
            java.lang.String r5 = r1.getProtocol()     // Catch:{ Exception -> 0x00a6, all -> 0x00a2 }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ Exception -> 0x00a6, all -> 0x00a2 }
            java.lang.String r6 = "https"
            boolean r5 = r5.equals(r6)     // Catch:{ Exception -> 0x00a6, all -> 0x00a2 }
            if (r5 == 0) goto L_0x002f
            java.net.URLConnection r1 = r1.openConnection()     // Catch:{ Exception -> 0x00a6, all -> 0x00a2 }
            javax.net.ssl.HttpsURLConnection r1 = (javax.net.ssl.HttpsURLConnection) r1     // Catch:{ Exception -> 0x00a6, all -> 0x00a2 }
            java.lang.String r5 = "use HTTPS protocol"
            android.util.Log.d(r0, r5)     // Catch:{ Exception -> 0x009e, all -> 0x009b }
            goto L_0x0035
        L_0x002f:
            java.net.URLConnection r1 = r1.openConnection()     // Catch:{ Exception -> 0x00a6, all -> 0x00a2 }
            java.net.HttpURLConnection r1 = (java.net.HttpURLConnection) r1     // Catch:{ Exception -> 0x00a6, all -> 0x00a2 }
        L_0x0035:
            r5 = 10000(0x2710, float:1.4013E-41)
            r1.setConnectTimeout(r5)     // Catch:{ Exception -> 0x009e, all -> 0x009b }
            r1.setReadTimeout(r5)     // Catch:{ Exception -> 0x009e, all -> 0x009b }
            java.lang.String r5 = "GET"
            r1.setRequestMethod(r5)     // Catch:{ Exception -> 0x009e, all -> 0x009b }
            r1.setDoOutput(r4)     // Catch:{ Exception -> 0x009e, all -> 0x009b }
            r1.connect()     // Catch:{ Exception -> 0x009e, all -> 0x009b }
            int r5 = r1.getResponseCode()     // Catch:{ Exception -> 0x009e, all -> 0x009b }
            r6 = 200(0xc8, float:2.8E-43)
            if (r5 != r6) goto L_0x008b
            r6 = 2048(0x800, float:2.87E-42)
            byte[] r7 = new byte[r6]     // Catch:{ Exception -> 0x0087, all -> 0x0083 }
            java.io.InputStream r8 = r1.getInputStream()     // Catch:{ Exception -> 0x0087, all -> 0x0083 }
            java.lang.StringBuffer r9 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0081, all -> 0x007d }
            r9.<init>()     // Catch:{ Exception -> 0x0081, all -> 0x007d }
            java.io.ByteArrayOutputStream r9 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0081, all -> 0x007d }
            r9.<init>()     // Catch:{ Exception -> 0x0081, all -> 0x007d }
        L_0x0062:
            int r3 = r8.read(r7, r4, r6)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            if (r3 == r10) goto L_0x006c
            r9.write(r7, r4, r3)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            goto L_0x0062
        L_0x006c:
            java.lang.String r10 = new java.lang.String     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            byte[] r3 = r9.toByteArray()     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            r10.<init>(r3)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            r2 = r10
            r3 = r8
            goto L_0x008c
        L_0x0078:
            r0 = move-exception
            goto L_0x007f
        L_0x007a:
            r3 = r1
            r10 = r5
            goto L_0x00a8
        L_0x007d:
            r0 = move-exception
            r9 = r3
        L_0x007f:
            r10 = r5
            goto L_0x00bc
        L_0x0081:
            r9 = r3
            goto L_0x0089
        L_0x0083:
            r0 = move-exception
            r9 = r3
            r10 = r5
            goto L_0x00bd
        L_0x0087:
            r8 = r3
            r9 = r8
        L_0x0089:
            r10 = r5
            goto L_0x00a0
        L_0x008b:
            r9 = r3
        L_0x008c:
            b.b.c.h.i.a(r11, r5, r4)
            if (r1 == 0) goto L_0x00b9
            r1.disconnect()
            miui.util.IOUtils.closeQuietly(r3)
        L_0x0097:
            miui.util.IOUtils.closeQuietly(r9)
            goto L_0x00b9
        L_0x009b:
            r0 = move-exception
            r9 = r3
            goto L_0x00bd
        L_0x009e:
            r8 = r3
            r9 = r8
        L_0x00a0:
            r3 = r1
            goto L_0x00a8
        L_0x00a2:
            r0 = move-exception
            r1 = r3
            r9 = r1
            goto L_0x00bd
        L_0x00a6:
            r8 = r3
            r9 = r8
        L_0x00a8:
            java.lang.String r1 = "httpGet Failed!!!"
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x00ba }
            b.b.c.h.i.a(r11, r10, r4)
            if (r3 == 0) goto L_0x00b9
            r3.disconnect()
            miui.util.IOUtils.closeQuietly(r8)
            goto L_0x0097
        L_0x00b9:
            return r2
        L_0x00ba:
            r0 = move-exception
            r1 = r3
        L_0x00bc:
            r3 = r8
        L_0x00bd:
            b.b.c.h.i.a(r11, r10, r4)
            if (r1 == 0) goto L_0x00cb
            r1.disconnect()
            miui.util.IOUtils.closeQuietly(r3)
            miui.util.IOUtils.closeQuietly(r9)
        L_0x00cb:
            throw r0
        L_0x00cc:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.googlebase.b.c.a(java.lang.String, b.b.c.h.j):java.lang.String");
    }
}
