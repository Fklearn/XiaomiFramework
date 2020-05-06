package com.xiaomi.analytics.a;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l f8323a;

    k(l lVar) {
        this.f8323a = lVar;
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:30:0x00d0=Splitter:B:30:0x00d0, B:20:0x00b9=Splitter:B:20:0x00b9} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r6 = this;
            java.lang.String r0 = "UpdateManager"
            java.net.URL r1 = new java.net.URL     // Catch:{ Exception -> 0x00d4 }
            com.xiaomi.analytics.a.l r2 = r6.f8323a     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r2 = r2.f8327d     // Catch:{ Exception -> 0x00d4 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x00d4 }
            java.net.URLConnection r1 = r1.openConnection()     // Catch:{ Exception -> 0x00d4 }
            java.net.HttpURLConnection r1 = (java.net.HttpURLConnection) r1     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r2 = "GET"
            r1.setRequestMethod(r2)     // Catch:{ Exception -> 0x00d4 }
            int r2 = com.xiaomi.analytics.a.a.f8280c     // Catch:{ Exception -> 0x00d4 }
            r1.setConnectTimeout(r2)     // Catch:{ Exception -> 0x00d4 }
            r1.connect()     // Catch:{ Exception -> 0x00d4 }
            int r2 = r1.getResponseCode()     // Catch:{ Exception -> 0x00d4 }
            r3 = 200(0xc8, float:2.8E-43)
            if (r2 != r3) goto L_0x00de
            java.io.InputStream r1 = r1.getInputStream()     // Catch:{ Exception -> 0x00d4 }
            byte[] r1 = com.xiaomi.analytics.a.a.g.a((java.io.InputStream) r1)     // Catch:{ Exception -> 0x00d4 }
            com.xiaomi.analytics.a.l r2 = r6.f8323a     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r2 = r2.e     // Catch:{ Exception -> 0x00d4 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x00d4 }
            r3 = 0
            if (r2 != 0) goto L_0x004e
            java.lang.String r2 = com.xiaomi.analytics.a.a.p.a((byte[]) r1)     // Catch:{ Exception -> 0x00d4 }
            com.xiaomi.analytics.a.l r4 = r6.f8323a     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r4 = r4.e     // Catch:{ Exception -> 0x00d4 }
            boolean r2 = r4.equalsIgnoreCase(r2)     // Catch:{ Exception -> 0x00d4 }
            if (r2 != 0) goto L_0x004e
            r1 = r3
        L_0x004e:
            if (r1 == 0) goto L_0x00de
            java.lang.String r2 = com.xiaomi.analytics.a.a.a.a(r0)     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r4 = "download apk success."
            android.util.Log.d(r2, r4)     // Catch:{ Exception -> 0x00d4 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x00d4 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4 }
            r4.<init>()     // Catch:{ Exception -> 0x00d4 }
            com.xiaomi.analytics.a.l r5 = r6.f8323a     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r5 = r5.f     // Catch:{ Exception -> 0x00d4 }
            r4.append(r5)     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r5 = ".tmp"
            r4.append(r5)     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00d4 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x00d4 }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00c5 }
            r4.<init>(r2)     // Catch:{ Exception -> 0x00c5 }
            r4.write(r1)     // Catch:{ Exception -> 0x00c0, all -> 0x00bd }
            r4.flush()     // Catch:{ Exception -> 0x00c0, all -> 0x00bd }
            r4.close()     // Catch:{ Exception -> 0x00c0, all -> 0x00bd }
            com.xiaomi.analytics.a.l r1 = r6.f8323a     // Catch:{ Exception -> 0x00c5 }
            android.content.Context r1 = r1.f8326c     // Catch:{ Exception -> 0x00c5 }
            android.content.pm.Signature[] r1 = com.xiaomi.analytics.a.a.b.a(r1, r2)     // Catch:{ Exception -> 0x00c5 }
            boolean r1 = com.xiaomi.analytics.a.a.e.a(r1)     // Catch:{ Exception -> 0x00c5 }
            if (r1 == 0) goto L_0x00b0
            java.lang.String r1 = com.xiaomi.analytics.a.a.a.a(r0)     // Catch:{ Exception -> 0x00c5 }
            java.lang.String r4 = "verify signature success"
            android.util.Log.d(r1, r4)     // Catch:{ Exception -> 0x00c5 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x00c5 }
            com.xiaomi.analytics.a.l r4 = r6.f8323a     // Catch:{ Exception -> 0x00c5 }
            java.lang.String r4 = r4.f     // Catch:{ Exception -> 0x00c5 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x00c5 }
            r2.renameTo(r1)     // Catch:{ Exception -> 0x00c5 }
            com.xiaomi.analytics.a.l r1 = r6.f8323a     // Catch:{ Exception -> 0x00c5 }
            r1.d()     // Catch:{ Exception -> 0x00c5 }
            goto L_0x00b9
        L_0x00b0:
            java.lang.String r1 = com.xiaomi.analytics.a.a.a.a(r0)     // Catch:{ Exception -> 0x00c5 }
            java.lang.String r2 = "verify signature failed"
            android.util.Log.e(r1, r2)     // Catch:{ Exception -> 0x00c5 }
        L_0x00b9:
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r3)     // Catch:{ Exception -> 0x00d4 }
            goto L_0x00de
        L_0x00bd:
            r1 = move-exception
            r3 = r4
            goto L_0x00d0
        L_0x00c0:
            r1 = move-exception
            r3 = r4
            goto L_0x00c6
        L_0x00c3:
            r1 = move-exception
            goto L_0x00d0
        L_0x00c5:
            r1 = move-exception
        L_0x00c6:
            java.lang.String r2 = com.xiaomi.analytics.a.a.a.a(r0)     // Catch:{ all -> 0x00c3 }
            java.lang.String r4 = "mDownloader e"
            android.util.Log.e(r2, r4, r1)     // Catch:{ all -> 0x00c3 }
            goto L_0x00b9
        L_0x00d0:
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r3)     // Catch:{ Exception -> 0x00d4 }
            throw r1     // Catch:{ Exception -> 0x00d4 }
        L_0x00d4:
            r1 = move-exception
            java.lang.String r0 = com.xiaomi.analytics.a.a.a.a(r0)
            java.lang.String r2 = "mDownloader exception"
            android.util.Log.w(r0, r2, r1)
        L_0x00de:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.a.k.run():void");
    }
}
