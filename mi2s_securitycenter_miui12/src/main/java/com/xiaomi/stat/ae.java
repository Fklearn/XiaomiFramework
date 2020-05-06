package com.xiaomi.stat;

class ae implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f8409a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f8410b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ab f8411c;

    ae(ab abVar, String str, String str2) {
        this.f8411c = abVar;
        this.f8409a = str;
        this.f8410b = str2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r14 = this;
            r0 = 0
            com.xiaomi.stat.ab r1 = r14.f8411c     // Catch:{ Exception -> 0x006f }
            android.database.sqlite.SQLiteOpenHelper r1 = r1.g     // Catch:{ Exception -> 0x006f }
            android.database.sqlite.SQLiteDatabase r1 = r1.getWritableDatabase()     // Catch:{ Exception -> 0x006f }
            java.lang.String r2 = r14.f8409a     // Catch:{ Exception -> 0x006f }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x006f }
            java.lang.String r10 = "pref_key=?"
            java.lang.String r11 = "pref"
            r12 = 0
            r13 = 1
            if (r2 == 0) goto L_0x0023
            java.lang.String[] r2 = new java.lang.String[r13]     // Catch:{ Exception -> 0x006f }
            java.lang.String r3 = r14.f8410b     // Catch:{ Exception -> 0x006f }
            r2[r12] = r3     // Catch:{ Exception -> 0x006f }
            r1.delete(r11, r10, r2)     // Catch:{ Exception -> 0x006f }
            return
        L_0x0023:
            java.lang.String r3 = "pref"
            r4 = 0
            java.lang.String r5 = "pref_key=?"
            java.lang.String[] r6 = new java.lang.String[r13]     // Catch:{ Exception -> 0x006f }
            java.lang.String r2 = r14.f8410b     // Catch:{ Exception -> 0x006f }
            r6[r12] = r2     // Catch:{ Exception -> 0x006f }
            r7 = 0
            r8 = 0
            r9 = 0
            r2 = r1
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x006f }
            int r3 = r2.getCount()     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            if (r3 <= 0) goto L_0x003e
            r3 = r12
            goto L_0x003f
        L_0x003e:
            r3 = r13
        L_0x003f:
            android.content.ContentValues r4 = new android.content.ContentValues     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            r4.<init>()     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            java.lang.String r5 = "pref_key"
            java.lang.String r6 = r14.f8410b     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            r4.put(r5, r6)     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            java.lang.String r5 = "pref_value"
            java.lang.String r6 = r14.f8409a     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            r4.put(r5, r6)     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            if (r3 == 0) goto L_0x0058
            r1.insert(r11, r0, r4)     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            goto L_0x0061
        L_0x0058:
            java.lang.String[] r0 = new java.lang.String[r13]     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            java.lang.String r3 = r14.f8410b     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            r0[r12] = r3     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
            r1.update(r11, r4, r10, r0)     // Catch:{ Exception -> 0x006a, all -> 0x0067 }
        L_0x0061:
            if (r2 == 0) goto L_0x008b
            r2.close()
            goto L_0x008b
        L_0x0067:
            r1 = move-exception
            r0 = r2
            goto L_0x008c
        L_0x006a:
            r1 = move-exception
            r0 = r2
            goto L_0x0070
        L_0x006d:
            r1 = move-exception
            goto L_0x008c
        L_0x006f:
            r1 = move-exception
        L_0x0070:
            java.lang.String r2 = "MiStatPref"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x006d }
            r3.<init>()     // Catch:{ all -> 0x006d }
            java.lang.String r4 = "update pref db failed with "
            r3.append(r4)     // Catch:{ all -> 0x006d }
            r3.append(r1)     // Catch:{ all -> 0x006d }
            java.lang.String r1 = r3.toString()     // Catch:{ all -> 0x006d }
            com.xiaomi.stat.d.k.c(r2, r1)     // Catch:{ all -> 0x006d }
            if (r0 == 0) goto L_0x008b
            r0.close()
        L_0x008b:
            return
        L_0x008c:
            if (r0 == 0) goto L_0x0091
            r0.close()
        L_0x0091:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.ae.run():void");
    }
}
