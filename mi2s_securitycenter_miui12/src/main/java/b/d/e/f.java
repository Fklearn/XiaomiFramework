package b.d.e;

import android.content.Context;

public class f {
    public static boolean a(Context context, String str) {
        return b(context, str) != 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x009d A[Catch:{ SQLiteException -> 0x00ea }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00cf A[Catch:{ SQLiteException -> 0x00ea }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x009c A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int b(android.content.Context r16, java.lang.String r17) {
        /*
            r0 = r17
            java.lang.String r1 = "thread_id"
            java.lang.String r2 = "type"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            java.lang.String r4 = "+86"
            r3.<init>(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            r4 = 0
            android.content.ContentResolver r11 = r16.getContentResolver()     // Catch:{ SQLiteException -> 0x00ec }
            java.lang.String r5 = "content://sms/"
            android.net.Uri r12 = android.net.Uri.parse(r5)     // Catch:{ SQLiteException -> 0x00ec }
            java.lang.String[] r13 = new java.lang.String[]{r2, r1}     // Catch:{ SQLiteException -> 0x00ec }
            java.lang.String r8 = "address=? or address=?"
            r14 = 2
            java.lang.String[] r9 = new java.lang.String[r14]     // Catch:{ SQLiteException -> 0x00ec }
            r9[r4] = r0     // Catch:{ SQLiteException -> 0x00ec }
            r15 = 1
            r9[r15] = r3     // Catch:{ SQLiteException -> 0x00ec }
            r10 = 0
            r5 = r11
            r6 = r12
            r7 = r13
            android.database.Cursor r5 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ SQLiteException -> 0x00ec }
            boolean r6 = r5.moveToFirst()     // Catch:{ SQLiteException -> 0x00ec }
            if (r6 == 0) goto L_0x0080
            int r10 = r5.getColumnIndex(r2)     // Catch:{ SQLiteException -> 0x00ec }
            int r1 = r5.getColumnIndex(r1)     // Catch:{ SQLiteException -> 0x00ec }
            long r6 = r5.getLong(r1)     // Catch:{ SQLiteException -> 0x00ec }
            java.lang.String r8 = "thread_id=?"
            r5.close()     // Catch:{ SQLiteException -> 0x00ec }
            java.lang.String[] r9 = new java.lang.String[r15]     // Catch:{ SQLiteException -> 0x00ec }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x00ec }
            r1.<init>()     // Catch:{ SQLiteException -> 0x00ec }
            r1.append(r6)     // Catch:{ SQLiteException -> 0x00ec }
            java.lang.String r1 = r1.toString()     // Catch:{ SQLiteException -> 0x00ec }
            r9[r4] = r1     // Catch:{ SQLiteException -> 0x00ec }
            r1 = 0
            r5 = r11
            r6 = r12
            r7 = r13
            r13 = r10
            r10 = r1
            android.database.Cursor r5 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ SQLiteException -> 0x00ec }
            boolean r1 = r5.moveToFirst()     // Catch:{ SQLiteException -> 0x00ec }
            if (r1 == 0) goto L_0x0080
            r1 = r4
        L_0x006d:
            int r6 = r5.getInt(r13)     // Catch:{ SQLiteException -> 0x00ea }
            if (r6 != r15) goto L_0x0075
            r1 = r1 | 1
        L_0x0075:
            if (r6 != r14) goto L_0x0079
            r1 = r1 | 2
        L_0x0079:
            boolean r6 = r5.moveToNext()     // Catch:{ SQLiteException -> 0x00ea }
            if (r6 != 0) goto L_0x006d
            goto L_0x0081
        L_0x0080:
            r1 = r4
        L_0x0081:
            r5.close()     // Catch:{ SQLiteException -> 0x00ea }
            java.lang.String r5 = "body"
            java.lang.String[] r7 = new java.lang.String[]{r5}     // Catch:{ SQLiteException -> 0x00ea }
            r8 = 0
            r9 = 0
            r10 = 0
            r5 = r11
            r6 = r12
            android.database.Cursor r5 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ SQLiteException -> 0x00ea }
            r5.moveToFirst()     // Catch:{ SQLiteException -> 0x00ea }
        L_0x0096:
            boolean r6 = r5.isAfterLast()     // Catch:{ SQLiteException -> 0x00ea }
            if (r6 == 0) goto L_0x009d
            goto L_0x00aa
        L_0x009d:
            java.lang.String r6 = r5.getString(r4)     // Catch:{ SQLiteException -> 0x00ea }
            int r6 = r6.indexOf(r0)     // Catch:{ SQLiteException -> 0x00ea }
            r7 = -1
            if (r6 == r7) goto L_0x00e6
            r1 = r1 | 4
        L_0x00aa:
            r5.close()     // Catch:{ SQLiteException -> 0x00ea }
            android.net.Uri r6 = android.provider.CallLog.Calls.CONTENT_URI     // Catch:{ SQLiteException -> 0x00ea }
            java.lang.String[] r7 = new java.lang.String[]{r2}     // Catch:{ SQLiteException -> 0x00ea }
            java.lang.String r8 = "number=? or number=?"
            java.lang.String[] r9 = new java.lang.String[r14]     // Catch:{ SQLiteException -> 0x00ea }
            r9[r4] = r0     // Catch:{ SQLiteException -> 0x00ea }
            r9[r15] = r3     // Catch:{ SQLiteException -> 0x00ea }
            java.lang.String r10 = "date DESC"
            r5 = r11
            android.database.Cursor r0 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ SQLiteException -> 0x00ea }
            r0.moveToFirst()     // Catch:{ SQLiteException -> 0x00ea }
        L_0x00c5:
            boolean r2 = r0.isAfterLast()     // Catch:{ SQLiteException -> 0x00ea }
            if (r2 == 0) goto L_0x00cf
            r0.close()     // Catch:{ SQLiteException -> 0x00ea }
            goto L_0x00f7
        L_0x00cf:
            int r2 = r0.getInt(r4)     // Catch:{ SQLiteException -> 0x00ea }
            if (r2 != r15) goto L_0x00d8
            r1 = r1 | 32
            goto L_0x00e2
        L_0x00d8:
            if (r2 != r14) goto L_0x00dd
            r1 = r1 | 64
            goto L_0x00e2
        L_0x00dd:
            r3 = 3
            if (r2 != r3) goto L_0x00e2
            r1 = r1 | 16
        L_0x00e2:
            r0.moveToNext()     // Catch:{ SQLiteException -> 0x00ea }
            goto L_0x00c5
        L_0x00e6:
            r5.moveToNext()     // Catch:{ SQLiteException -> 0x00ea }
            goto L_0x0096
        L_0x00ea:
            r0 = move-exception
            goto L_0x00ee
        L_0x00ec:
            r0 = move-exception
            r1 = r4
        L_0x00ee:
            java.lang.String r0 = r0.getMessage()
            java.lang.String r2 = "SQLiteException in queryAddress"
            android.util.Log.e(r2, r0)
        L_0x00f7:
            long r2 = (long) r1
            java.lang.String r0 = java.lang.Long.toHexString(r2)
            java.lang.String r2 = "queryAddress result:"
            android.util.Log.e(r2, r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: b.d.e.f.b(android.content.Context, java.lang.String):int");
    }
}
