package com.miui.gamebooster.ui;

class F implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4875a;

    F(N n) {
        this.f4875a = n;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        com.miui.gamebooster.m.C0391w.a(r12.f4875a.mAppContext, (java.lang.String) null, -1, true, 0);
        com.miui.gamebooster.m.V.a("already_added_game", (java.lang.String) null, new java.util.ArrayList());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00b2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0091 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r12 = this;
            java.lang.String r0 = "already_added_game"
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r3 = 0
            r4 = 1
            r5 = 0
            r6 = -1
            com.miui.gamebooster.ui.N r7 = r12.f4875a     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            android.content.Context r7 = r7.mAppContext     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            android.database.Cursor r7 = com.miui.gamebooster.m.C0391w.a((android.content.Context) r7, (int) r5)     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            r7.moveToFirst()     // Catch:{ Exception -> 0x0091 }
        L_0x001d:
            boolean r8 = r7.isAfterLast()     // Catch:{ Exception -> 0x0091 }
            if (r8 != 0) goto L_0x0089
            java.lang.String r8 = "package_name"
            int r8 = r7.getColumnIndex(r8)     // Catch:{ Exception -> 0x0091 }
            java.lang.String r3 = r7.getString(r8)     // Catch:{ Exception -> 0x0091 }
            java.lang.String r8 = "package_uid"
            int r8 = r7.getColumnIndex(r8)     // Catch:{ Exception -> 0x0091 }
            int r6 = r7.getInt(r8)     // Catch:{ Exception -> 0x0091 }
            com.miui.gamebooster.ui.N r8 = r12.f4875a     // Catch:{ Exception -> 0x0091 }
            android.content.pm.ApplicationInfo r8 = r8.a((java.lang.String) r3, (int) r6)     // Catch:{ Exception -> 0x0091 }
            if (r8 == 0) goto L_0x0074
            com.miui.gamebooster.ui.N r9 = r12.f4875a     // Catch:{ Exception -> 0x0091 }
            android.content.pm.PackageManager r9 = r9.f4940b     // Catch:{ Exception -> 0x0091 }
            java.lang.String r10 = r8.packageName     // Catch:{ Exception -> 0x0091 }
            android.content.Intent r9 = r9.getLaunchIntentForPackage(r10)     // Catch:{ Exception -> 0x0091 }
            if (r9 == 0) goto L_0x0074
            int r9 = r8.flags     // Catch:{ Exception -> 0x0091 }
            r10 = 8388608(0x800000, float:1.17549435E-38)
            r9 = r9 & r10
            if (r9 == 0) goto L_0x0074
            r1.add(r8)     // Catch:{ Exception -> 0x0091 }
            com.miui.gamebooster.ui.N r9 = r12.f4875a     // Catch:{ Exception -> 0x0091 }
            android.content.Context r9 = r9.mAppContext     // Catch:{ Exception -> 0x0091 }
            java.lang.String r9 = b.b.c.j.x.a((android.content.Context) r9, (android.content.pm.ApplicationInfo) r8)     // Catch:{ Exception -> 0x0091 }
            com.miui.gamebooster.model.d r10 = new com.miui.gamebooster.model.d     // Catch:{ Exception -> 0x0091 }
            com.miui.gamebooster.ui.N r11 = r12.f4875a     // Catch:{ Exception -> 0x0091 }
            android.content.pm.PackageManager r11 = r11.f4940b     // Catch:{ Exception -> 0x0091 }
            android.graphics.drawable.Drawable r11 = r8.loadIcon(r11)     // Catch:{ Exception -> 0x0091 }
            r10.<init>(r8, r4, r9, r11)     // Catch:{ Exception -> 0x0091 }
            r2.add(r10)     // Catch:{ Exception -> 0x0091 }
            goto L_0x0085
        L_0x0074:
            com.miui.gamebooster.ui.N r8 = r12.f4875a     // Catch:{ Exception -> 0x0091 }
            android.content.Context r8 = r8.mAppContext     // Catch:{ Exception -> 0x0091 }
            com.miui.gamebooster.m.C0391w.a((android.content.Context) r8, (java.lang.String) r3, (int) r6, (boolean) r4, (int) r5)     // Catch:{ Exception -> 0x0091 }
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x0091 }
            r8.<init>()     // Catch:{ Exception -> 0x0091 }
            com.miui.gamebooster.m.V.a(r0, r3, r8)     // Catch:{ Exception -> 0x0091 }
        L_0x0085:
            r7.moveToNext()     // Catch:{ Exception -> 0x0091 }
            goto L_0x001d
        L_0x0089:
            miui.util.IOUtils.closeQuietly(r7)
            goto L_0x00a3
        L_0x008d:
            r0 = move-exception
            r7 = r3
            goto L_0x00b3
        L_0x0090:
            r7 = r3
        L_0x0091:
            com.miui.gamebooster.ui.N r1 = r12.f4875a     // Catch:{ all -> 0x00b2 }
            android.content.Context r1 = r1.mAppContext     // Catch:{ all -> 0x00b2 }
            com.miui.gamebooster.m.C0391w.a((android.content.Context) r1, (java.lang.String) r3, (int) r6, (boolean) r4, (int) r5)     // Catch:{ all -> 0x00b2 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x00b2 }
            r1.<init>()     // Catch:{ all -> 0x00b2 }
            com.miui.gamebooster.m.V.a(r0, r3, r1)     // Catch:{ all -> 0x00b2 }
            goto L_0x0089
        L_0x00a3:
            com.miui.gamebooster.ui.N r0 = r12.f4875a
            com.miui.gamebooster.ui.E r1 = new com.miui.gamebooster.ui.E
            android.app.Activity r3 = r0.mActivity
            r1.<init>(r12, r3, r2)
            r0.postOnUiThread(r1)
            return
        L_0x00b2:
            r0 = move-exception
        L_0x00b3:
            miui.util.IOUtils.closeQuietly(r7)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.ui.F.run():void");
    }
}
