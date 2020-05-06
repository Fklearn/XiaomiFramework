package com.miui.activityutil;

final class af implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ai f2265a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ aa f2266b;

    af(aa aaVar, ai aiVar) {
        this.f2266b = aaVar;
        this.f2265a = aiVar;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x002c, code lost:
        if (r1 != 1) goto L_0x0051;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        /*
            r9 = this;
            com.miui.activityutil.aa r0 = r9.f2266b
            android.content.Context r0 = r0.r
            java.lang.String r1 = com.miui.activityutil.aa.e
            java.io.File r0 = com.miui.activityutil.aj.a(r0, r1)
            boolean r1 = r0.exists()
            r2 = 0
            r3 = 3
            r4 = 1
            r5 = 2
            r6 = 4
            r7 = 6
            if (r1 != 0) goto L_0x001b
            goto L_0x0051
        L_0x001b:
            byte[] r1 = com.miui.activityutil.h.a((java.io.File) r0)
            if (r1 == 0) goto L_0x0051
            com.miui.activityutil.aa r8 = r9.f2266b
            int r1 = r8.a((byte[]) r1)
            r8 = -1
            if (r1 == r8) goto L_0x004c
            if (r1 == 0) goto L_0x0031
            if (r1 == r4) goto L_0x002f
            goto L_0x0051
        L_0x002f:
            r3 = r6
            goto L_0x0052
        L_0x0031:
            com.miui.activityutil.aa r0 = r9.f2266b
            boolean r0 = com.miui.activityutil.aa.d(r0)
            if (r0 == 0) goto L_0x0040
            java.lang.String r0 = "interval"
            com.miui.activityutil.h.a((java.lang.String) r0)
            r7 = r2
            goto L_0x0052
        L_0x0040:
            com.miui.activityutil.aa r0 = r9.f2266b
            boolean r0 = r0.b(com.miui.activityutil.h.a(com.miui.activityutil.aj.a(r0.r, com.miui.activityutil.aa.f)))
            if (r0 == 0) goto L_0x004a
            r4 = r2
            goto L_0x002f
        L_0x004a:
            r7 = r6
            goto L_0x0052
        L_0x004c:
            r3 = 5
            r0.delete()
            goto L_0x0052
        L_0x0051:
            r3 = r5
        L_0x0052:
            com.miui.activityutil.ai r0 = r9.f2265a
            r0.f2269b = r3
            r0.f2270c = r7
            r0.f2271d = r4
            com.miui.activityutil.aa r0 = r9.f2266b
            android.os.Handler r0 = r0.v
            com.miui.activityutil.ag r1 = new com.miui.activityutil.ag
            r1.<init>(r9)
            r0.post(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.af.run():void");
    }
}
