package com.xiaomi.stat.a;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f8377a;

    g(c cVar) {
        this.f8377a = cVar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:45:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r20 = this;
            r1 = r20
            java.lang.String r0 = "ts"
            java.lang.String r2 = "EventManager"
            com.xiaomi.stat.a.c r4 = r1.f8377a     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            com.xiaomi.stat.a.a r4 = r4.l     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            android.database.sqlite.SQLiteDatabase r4 = r4.getWritableDatabase()     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            java.util.Calendar r13 = java.util.Calendar.getInstance()     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            long r5 = com.xiaomi.stat.d.r.b()     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            r13.setTimeInMillis(r5)     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            r5 = 6
            int r6 = r13.get(r5)     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            int r6 = r6 + -7
            r13.set(r5, r6)     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            r5 = 11
            r14 = 0
            r13.set(r5, r14)     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            r5 = 12
            r13.set(r5, r14)     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            r5 = 13
            r13.set(r5, r14)     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            long r5 = r13.getTimeInMillis()     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            java.lang.String r15 = "ts < ? and e != ?"
            r12 = 2
            java.lang.String[] r11 = new java.lang.String[r12]     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            java.lang.String r5 = java.lang.Long.toString(r5)     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            r11[r14] = r5     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            java.lang.String r5 = "mistat_delete_event"
            r10 = 1
            r11[r10] = r5     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            java.lang.String r6 = "events"
            java.lang.String[] r7 = new java.lang.String[]{r0}     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            r16 = 0
            r17 = 0
            java.lang.String r18 = "ts ASC"
            r5 = r4
            r8 = r15
            r9 = r11
            r3 = r10
            r10 = r16
            r19 = r11
            r11 = r17
            r14 = r12
            r12 = r18
            android.database.Cursor r5 = r5.query(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x0120, all -> 0x011d }
            int r6 = r5.getCount()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            if (r6 == 0) goto L_0x0112
            com.xiaomi.stat.aj r7 = new com.xiaomi.stat.aj     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r7.<init>()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r8 = "ca"
            r7.putInt(r8, r6)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r8.<init>()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r9 = "delete obsolete events total number "
            r8.append(r9)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r8.append(r6)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r6 = r8.toString()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            com.xiaomi.stat.d.k.c(r2, r6)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            int r0 = r5.getColumnIndex(r0)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r6 = 0
            r8 = 0
        L_0x0090:
            boolean r9 = r5.moveToNext()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r10 = "c_"
            if (r9 == 0) goto L_0x00ee
            long r11 = r5.getLong(r0)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r13.setTimeInMillis(r11)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            int r9 = r13.get(r3)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            int r11 = r13.get(r14)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            int r11 = r11 + r3
            r12 = 5
            int r12 = r13.get(r12)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r14 = "%4d%02d%02d"
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r16 = 0
            r3[r16] = r9     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r11 = 1
            r3[r11] = r9     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r12)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r12 = 2
            r3[r12] = r9     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r3 = java.lang.String.format(r14, r3)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            boolean r9 = android.text.TextUtils.equals(r6, r3)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            if (r9 != 0) goto L_0x00e9
            if (r6 == 0) goto L_0x00e6
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r9.<init>()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r9.append(r10)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r9.append(r6)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r6 = r9.toString()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r7.putInt(r6, r8)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
        L_0x00e6:
            r6 = r3
            r8 = r11
            goto L_0x00eb
        L_0x00e9:
            int r8 = r8 + 1
        L_0x00eb:
            r3 = r11
            r14 = r12
            goto L_0x0090
        L_0x00ee:
            if (r6 == 0) goto L_0x0102
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r0.<init>()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r0.append(r10)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r0.append(r6)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r7.putInt(r0, r8)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
        L_0x0102:
            com.xiaomi.stat.a.c r0 = r1.f8377a     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            com.xiaomi.stat.a.l r3 = com.xiaomi.stat.a.l.a((com.xiaomi.stat.aj) r7)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            r0.b((com.xiaomi.stat.a.l) r3)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
            java.lang.String r0 = "events"
            r3 = r19
            r4.delete(r0, r15, r3)     // Catch:{ Exception -> 0x011a, all -> 0x0118 }
        L_0x0112:
            if (r5 == 0) goto L_0x013b
            r5.close()
            goto L_0x013b
        L_0x0118:
            r0 = move-exception
            goto L_0x013e
        L_0x011a:
            r0 = move-exception
            r3 = r5
            goto L_0x0122
        L_0x011d:
            r0 = move-exception
            r5 = 0
            goto L_0x013e
        L_0x0120:
            r0 = move-exception
            r3 = 0
        L_0x0122:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x013c }
            r4.<init>()     // Catch:{ all -> 0x013c }
            java.lang.String r5 = "remove obsolete events failed with "
            r4.append(r5)     // Catch:{ all -> 0x013c }
            r4.append(r0)     // Catch:{ all -> 0x013c }
            java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x013c }
            com.xiaomi.stat.d.k.c(r2, r0)     // Catch:{ all -> 0x013c }
            if (r3 == 0) goto L_0x013b
            r3.close()
        L_0x013b:
            return
        L_0x013c:
            r0 = move-exception
            r5 = r3
        L_0x013e:
            if (r5 == 0) goto L_0x0143
            r5.close()
        L_0x0143:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.a.g.run():void");
    }
}
