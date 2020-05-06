package com.miui.activityutil;

import java.io.File;

final class z extends Thread {

    /* renamed from: b  reason: collision with root package name */
    private static final String f2331b = "1";

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ w f2332a;

    /* renamed from: c  reason: collision with root package name */
    private String[] f2333c;

    /* renamed from: d  reason: collision with root package name */
    private byte[] f2334d;
    private File e;
    private x f;
    private long g;
    /* access modifiers changed from: private */
    public String h = "";
    /* access modifiers changed from: private */
    public String i = "";

    public z(w wVar, String[] strArr, File file, x xVar) {
        this.f2332a = wVar;
        this.f2333c = strArr;
        this.e = file;
        this.f = xVar;
        this.g = file.length();
    }

    public z(w wVar, String[] strArr, byte[] bArr, x xVar) {
        this.f2332a = wVar;
        this.f2333c = strArr;
        this.f2334d = bArr;
        this.f = xVar;
        this.g = (long) bArr.length;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0086 A[Catch:{ Exception -> 0x00c8, all -> 0x00c3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00b6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.miui.activityutil.y a(java.lang.String[] r13) {
        /*
            r12 = this;
            com.miui.activityutil.y r0 = new com.miui.activityutil.y
            com.miui.activityutil.w r1 = r12.f2332a
            r0.<init>(r1)
            int r1 = r13.length
            r2 = 0
            r3 = r2
        L_0x000a:
            if (r3 >= r1) goto L_0x00fb
            r4 = r13[r3]
            r5 = 0
            java.net.URL r6 = new java.net.URL     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            r6.<init>(r4)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.net.URLConnection r4 = r6.openConnection()     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            r6 = 50000(0xc350, float:7.0065E-41)
            r4.setConnectTimeout(r6)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            r4.setReadTimeout(r6)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            r4.setUseCaches(r2)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.lang.String r6 = "POST"
            r4.setRequestMethod(r6)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.lang.String r6 = "Content-Type"
            java.lang.String r7 = "application/x-www-form-urlencoded"
            r4.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.lang.String r6 = "Content-Length"
            long r7 = r12.g     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.lang.String r7 = java.lang.String.valueOf(r7)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            r4.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.lang.String r6 = "pkg"
            java.lang.String r7 = r12.h     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            r4.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.lang.String r6 = "ver"
            java.lang.String r7 = r12.i     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            r4.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.lang.String r6 = "upload_ver"
            java.lang.String r7 = "1"
            r4.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            r6 = 1
            r4.setDoOutput(r6)     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.io.OutputStream r6 = r4.getOutputStream()     // Catch:{ Exception -> 0x00d8, all -> 0x00d3 }
            java.io.File r7 = r12.e     // Catch:{ Exception -> 0x00d0, all -> 0x00cc }
            r8 = -1
            r9 = 4096(0x1000, float:5.74E-42)
            if (r7 == 0) goto L_0x0074
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00d0, all -> 0x00cc }
            java.io.File r10 = r12.e     // Catch:{ Exception -> 0x00d0, all -> 0x00cc }
            r7.<init>(r10)     // Catch:{ Exception -> 0x00d0, all -> 0x00cc }
            byte[] r10 = new byte[r9]     // Catch:{ Exception -> 0x00c8, all -> 0x00c3 }
        L_0x006a:
            int r11 = r7.read(r10)     // Catch:{ Exception -> 0x00c8, all -> 0x00c3 }
            if (r11 == r8) goto L_0x007a
            r6.write(r10, r2, r11)     // Catch:{ Exception -> 0x00c8, all -> 0x00c3 }
            goto L_0x006a
        L_0x0074:
            byte[] r7 = r12.f2334d     // Catch:{ Exception -> 0x00d0, all -> 0x00cc }
            r6.write(r7)     // Catch:{ Exception -> 0x00d0, all -> 0x00cc }
            r7 = r5
        L_0x007a:
            int r10 = r4.getResponseCode()     // Catch:{ Exception -> 0x00c8, all -> 0x00c3 }
            r0.f2328a = r10     // Catch:{ Exception -> 0x00c8, all -> 0x00c3 }
            int r10 = r0.f2328a     // Catch:{ Exception -> 0x00c8, all -> 0x00c3 }
            r11 = 200(0xc8, float:2.8E-43)
            if (r10 != r11) goto L_0x00b6
            java.io.InputStream r4 = r4.getInputStream()     // Catch:{ Exception -> 0x00c8, all -> 0x00c3 }
            java.io.ByteArrayOutputStream r10 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x00b4, all -> 0x00b1 }
            r10.<init>()     // Catch:{ Exception -> 0x00b4, all -> 0x00b1 }
            byte[] r5 = new byte[r9]     // Catch:{ Exception -> 0x00ca, all -> 0x00af }
        L_0x0091:
            int r9 = r4.read(r5)     // Catch:{ Exception -> 0x00ca, all -> 0x00af }
            if (r9 == r8) goto L_0x009b
            r10.write(r5, r2, r9)     // Catch:{ Exception -> 0x00ca, all -> 0x00af }
            goto L_0x0091
        L_0x009b:
            java.lang.String r5 = r10.toString()     // Catch:{ Exception -> 0x00ca, all -> 0x00af }
            r0.f2329b = r5     // Catch:{ Exception -> 0x00ca, all -> 0x00af }
            com.miui.activityutil.aj.a((java.io.Closeable) r7)
            com.miui.activityutil.aj.a((java.io.Closeable) r4)
            com.miui.activityutil.aj.a((java.io.Closeable) r6)
            com.miui.activityutil.aj.a((java.io.Closeable) r10)
            goto L_0x00fb
        L_0x00af:
            r13 = move-exception
            goto L_0x00c6
        L_0x00b1:
            r13 = move-exception
            r10 = r5
            goto L_0x00c6
        L_0x00b4:
            r10 = r5
            goto L_0x00ca
        L_0x00b6:
            com.miui.activityutil.aj.a((java.io.Closeable) r7)
            com.miui.activityutil.aj.a((java.io.Closeable) r5)
            com.miui.activityutil.aj.a((java.io.Closeable) r6)
            com.miui.activityutil.aj.a((java.io.Closeable) r5)
            goto L_0x00e9
        L_0x00c3:
            r13 = move-exception
            r4 = r5
            r10 = r4
        L_0x00c6:
            r5 = r7
            goto L_0x00ee
        L_0x00c8:
            r4 = r5
            r10 = r4
        L_0x00ca:
            r5 = r7
            goto L_0x00db
        L_0x00cc:
            r13 = move-exception
            r4 = r5
            r10 = r4
            goto L_0x00ee
        L_0x00d0:
            r4 = r5
            r10 = r4
            goto L_0x00db
        L_0x00d3:
            r13 = move-exception
            r4 = r5
            r6 = r4
            r10 = r6
            goto L_0x00ee
        L_0x00d8:
            r4 = r5
            r6 = r4
            r10 = r6
        L_0x00db:
            r0.f2328a = r2     // Catch:{ all -> 0x00ed }
            com.miui.activityutil.aj.a((java.io.Closeable) r5)
            com.miui.activityutil.aj.a((java.io.Closeable) r4)
            com.miui.activityutil.aj.a((java.io.Closeable) r6)
            com.miui.activityutil.aj.a((java.io.Closeable) r10)
        L_0x00e9:
            int r3 = r3 + 1
            goto L_0x000a
        L_0x00ed:
            r13 = move-exception
        L_0x00ee:
            com.miui.activityutil.aj.a((java.io.Closeable) r5)
            com.miui.activityutil.aj.a((java.io.Closeable) r4)
            com.miui.activityutil.aj.a((java.io.Closeable) r6)
            com.miui.activityutil.aj.a((java.io.Closeable) r10)
            throw r13
        L_0x00fb:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.z.a(java.lang.String[]):com.miui.activityutil.y");
    }

    private void a() {
        start();
    }

    private void b() {
        run();
    }

    public final void run() {
        y a2 = a(this.f2333c);
        int i2 = a2.f2328a;
        if (i2 == 200) {
            x xVar = this.f;
            if (xVar != null) {
                xVar.a(a2.f2329b);
                return;
            }
            return;
        }
        x xVar2 = this.f;
        if (xVar2 != null) {
            xVar2.a(i2);
        }
    }
}
