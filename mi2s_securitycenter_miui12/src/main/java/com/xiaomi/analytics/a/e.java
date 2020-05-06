package com.xiaomi.analytics.a;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f8314a;

    e(i iVar) {
        this.f8314a = iVar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00d2 A[Catch:{ Exception -> 0x0133 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0100 A[Catch:{ Exception -> 0x0133 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r9 = this;
            r0 = 1
            java.lang.Object r1 = com.xiaomi.analytics.a.i.f8320c     // Catch:{ Exception -> 0x0133 }
            monitor-enter(r1)     // Catch:{ Exception -> 0x0133 }
            com.xiaomi.analytics.a.i r2 = r9.f8314a     // Catch:{ all -> 0x012e }
            r2.v()     // Catch:{ all -> 0x012e }
            r2 = 0
            com.xiaomi.analytics.a.i r3 = r9.f8314a     // Catch:{ all -> 0x012e }
            boolean r3 = r3.l     // Catch:{ all -> 0x012e }
            if (r3 == 0) goto L_0x001a
            boolean r3 = com.xiaomi.analytics.a.i.f8321d     // Catch:{ all -> 0x012e }
            if (r3 == 0) goto L_0x002a
        L_0x001a:
            com.xiaomi.analytics.a.i r2 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.a unused = r2.u()     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r2 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.e r2 = r2.h     // Catch:{ all -> 0x012e }
            if (r2 == 0) goto L_0x002a
            r2.init()     // Catch:{ all -> 0x012e }
        L_0x002a:
            if (r2 == 0) goto L_0x0046
            java.lang.String r3 = "SdkManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x012e }
            r4.<init>()     // Catch:{ all -> 0x012e }
            java.lang.String r5 = "sys version = "
            r4.append(r5)     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.m r5 = r2.getVersion()     // Catch:{ all -> 0x012e }
            r4.append(r5)     // Catch:{ all -> 0x012e }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.a.a.a(r3, r4)     // Catch:{ all -> 0x012e }
        L_0x0046:
            boolean r3 = com.xiaomi.analytics.a.i.f8321d     // Catch:{ all -> 0x012e }
            if (r3 == 0) goto L_0x006a
            java.lang.String r3 = "SdkManager"
            java.lang.String r4 = "use system analytics only, so don't load asset/local analytics.apk"
            com.xiaomi.analytics.a.a.a.a(r3, r4)     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r3 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.a unused = r3.f = r2     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r2 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r3 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.a r3 = r3.f     // Catch:{ all -> 0x012e }
            r2.a((com.xiaomi.analytics.a.b.a) r3)     // Catch:{ all -> 0x012e }
            monitor-exit(r1)     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r1 = r9.f8314a
            boolean unused = r1.k = r0
            return
        L_0x006a:
            com.xiaomi.analytics.a.i r3 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.a r3 = r3.r()     // Catch:{ all -> 0x012e }
            java.lang.String r4 = "SdkManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x012e }
            r5.<init>()     // Catch:{ all -> 0x012e }
            java.lang.String r6 = "assets analytics null "
            r5.append(r6)     // Catch:{ all -> 0x012e }
            r6 = 0
            if (r3 != 0) goto L_0x0081
            r7 = r0
            goto L_0x0082
        L_0x0081:
            r7 = r6
        L_0x0082:
            r5.append(r7)     // Catch:{ all -> 0x012e }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.a.a.a(r4, r5)     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r4 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.a r4 = r4.t()     // Catch:{ all -> 0x012e }
            java.lang.String r5 = "SdkManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x012e }
            r7.<init>()     // Catch:{ all -> 0x012e }
            java.lang.String r8 = "local analytics null "
            r7.append(r8)     // Catch:{ all -> 0x012e }
            if (r4 != 0) goto L_0x00a1
            r6 = r0
        L_0x00a1:
            r7.append(r6)     // Catch:{ all -> 0x012e }
            java.lang.String r6 = r7.toString()     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.a.a.a(r5, r6)     // Catch:{ all -> 0x012e }
            if (r3 == 0) goto L_0x00c8
            if (r4 == 0) goto L_0x00be
            com.xiaomi.analytics.a.m r5 = r4.getVersion()     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.m r6 = r3.getVersion()     // Catch:{ all -> 0x012e }
            int r5 = r5.compareTo(r6)     // Catch:{ all -> 0x012e }
            if (r5 <= 0) goto L_0x00be
            goto L_0x00c8
        L_0x00be:
            if (r3 == 0) goto L_0x00d0
            java.lang.String r4 = "SdkManager"
            java.lang.String r5 = "use assets analytics."
            com.xiaomi.analytics.a.a.a.a(r4, r5)     // Catch:{ all -> 0x012e }
            goto L_0x00d0
        L_0x00c8:
            java.lang.String r3 = "SdkManager"
            java.lang.String r5 = "use local analytics."
            com.xiaomi.analytics.a.a.a.a(r3, r5)     // Catch:{ all -> 0x012e }
            r3 = r4
        L_0x00d0:
            if (r2 == 0) goto L_0x00f7
            if (r3 == 0) goto L_0x00e3
            com.xiaomi.analytics.a.m r4 = r3.getVersion()     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.m r5 = r2.getVersion()     // Catch:{ all -> 0x012e }
            int r4 = r4.compareTo(r5)     // Catch:{ all -> 0x012e }
            if (r4 <= 0) goto L_0x00e3
            goto L_0x00f7
        L_0x00e3:
            if (r2 == 0) goto L_0x0109
            java.lang.String r4 = "SdkManager"
            java.lang.String r5 = "use sys analytics."
            com.xiaomi.analytics.a.a.a.a(r4, r5)     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r4 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.a unused = r4.q = r3     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r3 = r9.f8314a     // Catch:{ all -> 0x012e }
            r3.s()     // Catch:{ all -> 0x012e }
            goto L_0x0109
        L_0x00f7:
            java.lang.String r2 = "SdkManager"
            java.lang.String r4 = "use dex analytics."
            com.xiaomi.analytics.a.a.a.a(r2, r4)     // Catch:{ all -> 0x012e }
            if (r3 == 0) goto L_0x0103
            r3.init()     // Catch:{ all -> 0x012e }
        L_0x0103:
            com.xiaomi.analytics.a.i r2 = r9.f8314a     // Catch:{ all -> 0x012e }
            r2.a((boolean) r0)     // Catch:{ all -> 0x012e }
            r2 = r3
        L_0x0109:
            if (r2 == 0) goto L_0x011c
            com.xiaomi.analytics.a.m r3 = r2.getVersion()     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.m r4 = com.xiaomi.analytics.a.a.f8279b     // Catch:{ all -> 0x012e }
            int r3 = r3.compareTo(r4)     // Catch:{ all -> 0x012e }
            if (r3 < 0) goto L_0x011c
            com.xiaomi.analytics.a.i r3 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.a unused = r3.f = r2     // Catch:{ all -> 0x012e }
        L_0x011c:
            com.xiaomi.analytics.a.i r2 = r9.f8314a     // Catch:{ all -> 0x012e }
            r2.h()     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r2 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.i r3 = r9.f8314a     // Catch:{ all -> 0x012e }
            com.xiaomi.analytics.a.b.a r3 = r3.f     // Catch:{ all -> 0x012e }
            r2.a((com.xiaomi.analytics.a.b.a) r3)     // Catch:{ all -> 0x012e }
            monitor-exit(r1)     // Catch:{ all -> 0x012e }
            goto L_0x013f
        L_0x012e:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x012e }
            throw r2     // Catch:{ Exception -> 0x0133 }
        L_0x0131:
            r1 = move-exception
            goto L_0x0145
        L_0x0133:
            r1 = move-exception
            java.lang.String r2 = "SdkManager"
            java.lang.String r2 = com.xiaomi.analytics.a.a.a.a(r2)     // Catch:{ all -> 0x0131 }
            java.lang.String r3 = "heavy work exception"
            android.util.Log.w(r2, r3, r1)     // Catch:{ all -> 0x0131 }
        L_0x013f:
            com.xiaomi.analytics.a.i r1 = r9.f8314a
            boolean unused = r1.k = r0
            return
        L_0x0145:
            com.xiaomi.analytics.a.i r2 = r9.f8314a
            boolean unused = r2.k = r0
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.a.e.run():void");
    }
}
