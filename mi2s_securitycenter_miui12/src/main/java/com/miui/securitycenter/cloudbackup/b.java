package com.miui.securitycenter.cloudbackup;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final String f7471a = "b";

    /* JADX WARNING: Removed duplicated region for block: B:33:0x010a A[LOOP:1: B:33:0x010a->B:72:0x010a, LOOP_START, PHI: r2 r8 
      PHI: (r2v28 org.json.JSONArray) = (r2v1 org.json.JSONArray), (r2v30 org.json.JSONArray) binds: [B:32:0x0108, B:72:0x010a] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r8v3 java.lang.String) = (r8v1 java.lang.String), (r8v4 java.lang.String) binds: [B:32:0x0108, B:72:0x010a] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC, Splitter:B:33:0x010a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.json.JSONObject a(android.content.Context r37) {
        /*
            r1 = r37
            java.lang.String r2 = "contact_sms_mode"
            java.lang.String r3 = "CKContactSmsModes"
            java.lang.String r4 = "stranger_call_mode"
            java.lang.String r5 = "state"
            java.lang.String r6 = "CKStrangerCallModes"
            java.lang.String r7 = "notes"
            java.lang.String r8 = "stranger_sms_mode"
            java.lang.String r9 = "CKStrangerSmsModes"
            java.lang.String r10 = "number"
            java.lang.String r11 = "CKNotificationShowType"
            java.lang.String r12 = "CKAntispamEnable"
            org.json.JSONObject r13 = new org.json.JSONObject
            r13.<init>()
            org.json.JSONObject r14 = new org.json.JSONObject
            r14.<init>()
            org.json.JSONObject r15 = new org.json.JSONObject
            r15.<init>()
            r16 = r13
            org.json.JSONArray r13 = new org.json.JSONArray
            r13.<init>()
            r17 = r15
            org.json.JSONArray r15 = new org.json.JSONArray
            r15.<init>()
            r18 = r3
            org.json.JSONArray r3 = new org.json.JSONArray
            r3.<init>()
            r19 = r2
            org.json.JSONArray r2 = new org.json.JSONArray
            r2.<init>()
            r20 = r6
            org.json.JSONArray r6 = new org.json.JSONArray
            r6.<init>()
            r21 = r4
            org.json.JSONArray r4 = new org.json.JSONArray
            r4.<init>()
            android.content.ContentResolver r22 = r37.getContentResolver()
            android.net.Uri r23 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI
            r28 = r9
            r9 = 2
            java.lang.String[] r0 = new java.lang.String[r9]
            r9 = 1
            java.lang.String r24 = java.lang.String.valueOf(r9)
            r25 = 0
            r0[r25] = r24
            r24 = 4
            java.lang.String r24 = java.lang.String.valueOf(r24)
            r0[r9] = r24
            r24 = 0
            java.lang.String r25 = "type = ? OR type = ? "
            r27 = 0
            r26 = r0
            android.database.Cursor r9 = r22.query(r23, r24, r25, r26, r27)
            r22 = r8
            java.lang.String r8 = "sim_id"
            r23 = r11
            java.lang.String r11 = "type"
            if (r9 == 0) goto L_0x00db
        L_0x0083:
            boolean r0 = r9.moveToNext()     // Catch:{ Exception -> 0x00ca }
            if (r0 == 0) goto L_0x00db
            java.lang.String r0 = "data"
            int r0 = r9.getColumnIndex(r0)     // Catch:{ Exception -> 0x00ca }
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x00ca }
            r24 = r12
            int r12 = r9.getColumnIndex(r8)     // Catch:{ Exception -> 0x00c6 }
            int r12 = r9.getInt(r12)     // Catch:{ Exception -> 0x00c6 }
            r25 = r14
            int r14 = r9.getColumnIndex(r11)     // Catch:{ Exception -> 0x00c4 }
            int r14 = r9.getInt(r14)     // Catch:{ Exception -> 0x00c4 }
            r1 = 1
            if (r1 != r14) goto L_0x00b4
            if (r12 != r1) goto L_0x00b0
            r13.put(r0)     // Catch:{ Exception -> 0x00c4 }
            goto L_0x00bd
        L_0x00b0:
            r15.put(r0)     // Catch:{ Exception -> 0x00c4 }
            goto L_0x00bd
        L_0x00b4:
            if (r12 != r1) goto L_0x00ba
            r3.put(r0)     // Catch:{ Exception -> 0x00c4 }
            goto L_0x00bd
        L_0x00ba:
            r2.put(r0)     // Catch:{ Exception -> 0x00c4 }
        L_0x00bd:
            r1 = r37
            r12 = r24
            r14 = r25
            goto L_0x0083
        L_0x00c4:
            r0 = move-exception
            goto L_0x00cf
        L_0x00c6:
            r0 = move-exception
            goto L_0x00cd
        L_0x00c8:
            r0 = move-exception
            goto L_0x00d7
        L_0x00ca:
            r0 = move-exception
            r24 = r12
        L_0x00cd:
            r25 = r14
        L_0x00cf:
            java.lang.String r1 = f7471a     // Catch:{ all -> 0x00c8 }
            java.lang.String r12 = "Get keyword list JSON failed. "
            android.util.Log.e(r1, r12, r0)     // Catch:{ all -> 0x00c8 }
            goto L_0x00df
        L_0x00d7:
            miui.util.IOUtils.closeQuietly(r9)
            throw r0
        L_0x00db:
            r24 = r12
            r25 = r14
        L_0x00df:
            miui.util.IOUtils.closeQuietly(r9)
            android.content.ContentResolver r29 = r37.getContentResolver()
            android.net.Uri r30 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI
            java.lang.String r31 = "number"
            java.lang.String r32 = "notes"
            java.lang.String r33 = "state"
            java.lang.String r34 = "sim_id"
            java.lang.String r35 = "type"
            java.lang.String r36 = "sync_dirty"
            java.lang.String[] r31 = new java.lang.String[]{r31, r32, r33, r34, r35, r36}
            java.lang.String r0 = "1"
            java.lang.String r1 = "2"
            java.lang.String[] r33 = new java.lang.String[]{r0, r1}
            r34 = 0
            java.lang.String r32 = "type = ? OR type = ?"
            android.database.Cursor r1 = r29.query(r30, r31, r32, r33, r34)
            if (r1 == 0) goto L_0x017d
        L_0x010a:
            boolean r0 = r1.moveToNext()     // Catch:{ Exception -> 0x016e }
            if (r0 == 0) goto L_0x017d
            java.lang.String r0 = "sync_dirty"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ Exception -> 0x016e }
            int r0 = r1.getInt(r0)     // Catch:{ Exception -> 0x016e }
            r9 = 1
            if (r0 != r9) goto L_0x011e
            goto L_0x010a
        L_0x011e:
            int r0 = r1.getColumnIndex(r10)     // Catch:{ Exception -> 0x016e }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ Exception -> 0x016e }
            int r9 = r1.getColumnIndex(r7)     // Catch:{ Exception -> 0x016e }
            java.lang.String r9 = r1.getString(r9)     // Catch:{ Exception -> 0x016e }
            int r12 = r1.getColumnIndex(r5)     // Catch:{ Exception -> 0x016e }
            int r12 = r1.getInt(r12)     // Catch:{ Exception -> 0x016e }
            int r14 = r1.getColumnIndex(r8)     // Catch:{ Exception -> 0x016e }
            int r14 = r1.getInt(r14)     // Catch:{ Exception -> 0x016e }
            r26 = r8
            int r8 = r1.getColumnIndex(r11)     // Catch:{ Exception -> 0x016e }
            java.lang.String r8 = r1.getString(r8)     // Catch:{ Exception -> 0x016e }
            r27 = r2
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x016a }
            r2.<init>()     // Catch:{ Exception -> 0x016a }
            r2.put(r10, r0)     // Catch:{ Exception -> 0x016a }
            r2.put(r7, r9)     // Catch:{ Exception -> 0x016a }
            r2.put(r5, r12)     // Catch:{ Exception -> 0x016a }
            r2.put(r11, r8)     // Catch:{ Exception -> 0x016a }
            r8 = 1
            if (r14 != r8) goto L_0x0162
            r6.put(r2)     // Catch:{ Exception -> 0x016a }
            goto L_0x0165
        L_0x0162:
            r4.put(r2)     // Catch:{ Exception -> 0x016a }
        L_0x0165:
            r8 = r26
            r2 = r27
            goto L_0x010a
        L_0x016a:
            r0 = move-exception
            goto L_0x0171
        L_0x016c:
            r0 = move-exception
            goto L_0x0179
        L_0x016e:
            r0 = move-exception
            r27 = r2
        L_0x0171:
            java.lang.String r2 = f7471a     // Catch:{ all -> 0x016c }
            java.lang.String r5 = "Get phone list JSON failed. "
            android.util.Log.e(r2, r5, r0)     // Catch:{ all -> 0x016c }
            goto L_0x017f
        L_0x0179:
            miui.util.IOUtils.closeQuietly(r1)
            throw r0
        L_0x017d:
            r27 = r2
        L_0x017f:
            miui.util.IOUtils.closeQuietly(r1)
            r2 = 1
            r1 = r37
            boolean r0 = b.b.a.e.c.b((android.content.Context) r1, (int) r2)     // Catch:{ JSONException -> 0x0328 }
            r7 = r24
            r5 = r25
            r5.put(r7, r0)     // Catch:{ JSONException -> 0x0328 }
            int r0 = b.b.a.e.c.a((android.content.Context) r1, (int) r2)     // Catch:{ JSONException -> 0x0328 }
            r8 = r23
            r5.put(r8, r0)     // Catch:{ JSONException -> 0x0328 }
            r9 = r22
            int r0 = com.miui.antispam.db.d.a(r1, r9, r2, r2)     // Catch:{ JSONException -> 0x0328 }
            r10 = r28
            r5.put(r10, r0)     // Catch:{ JSONException -> 0x0328 }
            r11 = r21
            int r0 = com.miui.antispam.db.d.a(r1, r11, r2, r2)     // Catch:{ JSONException -> 0x0328 }
            r12 = r20
            r5.put(r12, r0)     // Catch:{ JSONException -> 0x0328 }
            r14 = r19
            int r0 = com.miui.antispam.db.d.a(r1, r14, r2, r2)     // Catch:{ JSONException -> 0x0328 }
            r2 = r18
            r5.put(r2, r0)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKContactCallModes"
            r18 = r4
            java.lang.String r4 = "contact_call_mode"
            r19 = r15
            r15 = 1
            int r4 = com.miui.antispam.db.d.a(r1, r4, r15, r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKServiceSmsModes"
            java.lang.String r4 = "service_sms_mode"
            int r4 = com.miui.antispam.db.d.a(r1, r4, r15, r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKEmptyCallModes"
            java.lang.String r4 = "empty_call_mode"
            int r4 = com.miui.antispam.db.d.a(r1, r4, r15, r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKOverseaCallModes"
            java.lang.String r4 = "oversea_call_mode"
            int r4 = com.miui.antispam.db.d.a(r1, r4, r15, r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKFraud"
            boolean r4 = com.miui.antispam.db.d.b((android.content.Context) r1, (int) r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKAgent"
            boolean r4 = com.miui.antispam.db.d.a((android.content.Context) r1, (int) r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKSell"
            boolean r4 = com.miui.antispam.db.d.d(r1, r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKHarass"
            boolean r4 = com.miui.antispam.db.d.c(r1, r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKRepeatedMarkNum"
            boolean r4 = com.miui.antispam.db.d.c((int) r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKCallTransfer"
            boolean r4 = com.miui.antispam.db.d.b((int) r15)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKKeywordsBlack"
            r5.put(r0, r13)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKKeywordsWhite"
            r5.put(r0, r3)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKPhoneList"
            r5.put(r0, r6)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKMmsModes"
            java.lang.String r3 = "mms_mode"
            r4 = 2
            r6 = 1
            int r3 = com.miui.antispam.db.d.a(r1, r3, r6, r4)     // Catch:{ JSONException -> 0x0328 }
            r5.put(r0, r3)     // Catch:{ JSONException -> 0x0328 }
            boolean r0 = b.b.a.e.c.b((android.content.Context) r1, (int) r4)     // Catch:{ JSONException -> 0x0328 }
            r3 = r17
            r3.put(r7, r0)     // Catch:{ JSONException -> 0x0328 }
            int r0 = b.b.a.e.c.a((android.content.Context) r1, (int) r4)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r8, r0)     // Catch:{ JSONException -> 0x0328 }
            r6 = 1
            int r0 = com.miui.antispam.db.d.a(r1, r9, r4, r6)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r10, r0)     // Catch:{ JSONException -> 0x0328 }
            int r0 = com.miui.antispam.db.d.a(r1, r11, r4, r6)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r12, r0)     // Catch:{ JSONException -> 0x0328 }
            int r0 = com.miui.antispam.db.d.a(r1, r14, r4, r6)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r2, r0)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKContactCallModes"
            java.lang.String r2 = "contact_call_mode"
            int r2 = com.miui.antispam.db.d.a(r1, r2, r4, r6)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKServiceSmsModes"
            java.lang.String r2 = "service_sms_mode"
            int r2 = com.miui.antispam.db.d.a(r1, r2, r4, r6)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKEmptyCallModes"
            java.lang.String r2 = "empty_call_mode"
            int r2 = com.miui.antispam.db.d.a(r1, r2, r4, r6)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKOverseaCallModes"
            java.lang.String r2 = "oversea_call_mode"
            int r2 = com.miui.antispam.db.d.a(r1, r2, r4, r6)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKFraud"
            boolean r2 = com.miui.antispam.db.d.b((android.content.Context) r1, (int) r4)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKAgent"
            boolean r2 = com.miui.antispam.db.d.a((android.content.Context) r1, (int) r4)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKSell"
            boolean r2 = com.miui.antispam.db.d.d(r1, r4)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKHarass"
            boolean r2 = com.miui.antispam.db.d.c(r1, r4)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKRepeatedMarkNum"
            boolean r2 = com.miui.antispam.db.d.c((int) r4)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKCallTransfer"
            boolean r2 = com.miui.antispam.db.d.b((int) r4)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKKeywordsBlack"
            r2 = r19
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKKeywordsWhite"
            r2 = r27
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKPhoneList"
            r2 = r18
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "CKMmsModes"
            java.lang.String r2 = "mms_mode"
            r4 = 2
            int r2 = com.miui.antispam.db.d.a(r1, r2, r4, r4)     // Catch:{ JSONException -> 0x0328 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0328 }
            java.lang.String r0 = "sim_id_1"
            r2 = r16
            r2.put(r0, r5)     // Catch:{ JSONException -> 0x0326 }
            java.lang.String r0 = "sim_id_2"
            r2.put(r0, r3)     // Catch:{ JSONException -> 0x0326 }
            java.lang.String r0 = "CKAutoUpdateLibrary"
            boolean r3 = b.b.a.e.c.f(r37)     // Catch:{ JSONException -> 0x0326 }
            r2.put(r0, r3)     // Catch:{ JSONException -> 0x0326 }
            java.lang.String r0 = "CKSimsShareSettings"
            boolean r3 = b.b.a.e.c.e(r37)     // Catch:{ JSONException -> 0x0326 }
            r2.put(r0, r3)     // Catch:{ JSONException -> 0x0326 }
            java.lang.String r0 = "CKReportedNumberGuideFraud"
            java.lang.String r3 = "mark_guide_fraud"
            boolean r3 = b.b.a.e.c.a((android.content.Context) r1, (java.lang.String) r3)     // Catch:{ JSONException -> 0x0326 }
            r2.put(r0, r3)     // Catch:{ JSONException -> 0x0326 }
            java.lang.String r0 = "CKReportedNumberGuideAgent"
            java.lang.String r3 = "mark_guide_agent"
            boolean r3 = b.b.a.e.c.a((android.content.Context) r1, (java.lang.String) r3)     // Catch:{ JSONException -> 0x0326 }
            r2.put(r0, r3)     // Catch:{ JSONException -> 0x0326 }
            java.lang.String r0 = "CKReportedNumberGuideSell"
            java.lang.String r3 = "mark_guide_sell"
            boolean r1 = b.b.a.e.c.a((android.content.Context) r1, (java.lang.String) r3)     // Catch:{ JSONException -> 0x0326 }
            r2.put(r0, r1)     // Catch:{ JSONException -> 0x0326 }
            goto L_0x0332
        L_0x0326:
            r0 = move-exception
            goto L_0x032b
        L_0x0328:
            r0 = move-exception
            r2 = r16
        L_0x032b:
            java.lang.String r1 = f7471a
            java.lang.String r3 = "Get mode JSON failed. "
            android.util.Log.e(r1, r3, r0)
        L_0x0332:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.cloudbackup.b.a(android.content.Context):org.json.JSONObject");
    }

    /* JADX WARNING: Removed duplicated region for block: B:140:0x02dd A[Catch:{ OperationApplicationException | RemoteException -> 0x05af }] */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x030a A[Catch:{ OperationApplicationException | RemoteException -> 0x05af }] */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x0552 A[Catch:{ OperationApplicationException | RemoteException -> 0x05af }] */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x0563 A[Catch:{ OperationApplicationException | RemoteException -> 0x05af }] */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0574 A[Catch:{ OperationApplicationException | RemoteException -> 0x05af }] */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0587 A[Catch:{ OperationApplicationException | RemoteException -> 0x05af }] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x059a A[Catch:{ OperationApplicationException | RemoteException -> 0x05af }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(android.content.Context r26, org.json.JSONObject r27) {
        /*
            r1 = r26
            r2 = r27
            if (r2 != 0) goto L_0x0007
            return
        L_0x0007:
            boolean r3 = miui.yellowpage.YellowPageUtils.isYellowPageEnable(r26)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r4.<init>()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.String r5 = "sim_id_1"
            boolean r5 = r2.has(r5)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.String r6 = "CKOverseaCallModes"
            java.lang.String r7 = "CKEmptyCallModes"
            java.lang.String r8 = "CKServiceSmsModes"
            java.lang.String r9 = "CKContactCallModes"
            java.lang.String r10 = "CKContactSmsModes"
            java.lang.String r11 = "CKStrangerCallModes"
            java.lang.String r12 = "CKStrangerSmsModes"
            java.lang.String r13 = "CKNotificationShowType"
            java.lang.String r14 = "CKAntispamEnable"
            java.lang.String r15 = "notes"
            r16 = r15
            java.lang.String r15 = "sim_id"
            r17 = r4
            java.lang.String r4 = "type"
            r18 = 0
            r19 = r4
            r4 = 1
            if (r5 == 0) goto L_0x02ea
            java.lang.String r5 = "sim_id_1"
            org.json.JSONObject r5 = r2.optJSONObject(r5)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            boolean r20 = r5.has(r14)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r20 == 0) goto L_0x004c
            boolean r2 = r5.optBoolean(r14)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.a((android.content.Context) r1, (int) r4, (boolean) r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x004c:
            boolean r2 = r5.has(r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x0059
            int r2 = r5.optInt(r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.a((android.content.Context) r1, (int) r2, (int) r4)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0059:
            boolean r2 = r5.has(r12)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x006b
            java.lang.String r2 = "stranger_sms_mode"
            r20 = r13
            int r13 = r5.optInt(r12)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r2, r4, r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            goto L_0x006d
        L_0x006b:
            r20 = r13
        L_0x006d:
            boolean r2 = r5.has(r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x007c
            java.lang.String r2 = "stranger_call_mode"
            int r13 = r5.optInt(r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r2, r4, r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x007c:
            boolean r2 = r5.has(r10)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x008b
            java.lang.String r2 = "contact_sms_mode"
            int r13 = r5.optInt(r10)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r2, r4, r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x008b:
            boolean r2 = r5.has(r9)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x009a
            java.lang.String r2 = "contact_call_mode"
            int r13 = r5.optInt(r9)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r2, r4, r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x009a:
            boolean r2 = r5.has(r8)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x00a9
            java.lang.String r2 = "service_sms_mode"
            int r13 = r5.optInt(r8)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r2, r4, r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x00a9:
            boolean r2 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x00b8
            java.lang.String r2 = "empty_call_mode"
            int r13 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r2, r4, r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x00b8:
            boolean r2 = r5.has(r6)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x00c7
            java.lang.String r2 = "oversea_call_mode"
            int r13 = r5.optInt(r6)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r2, r4, r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x00c7:
            java.lang.String r2 = "CKFraud"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x00e0
            java.lang.String r2 = "CKFraud"
            boolean r2 = r5.optBoolean(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x00db
            if (r3 == 0) goto L_0x00db
            r2 = r4
            goto L_0x00dd
        L_0x00db:
            r2 = r18
        L_0x00dd:
            com.miui.antispam.db.d.b(r1, r4, r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x00e0:
            java.lang.String r2 = "CKAgent"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x00f9
            java.lang.String r2 = "CKAgent"
            boolean r2 = r5.optBoolean(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x00f4
            if (r3 == 0) goto L_0x00f4
            r2 = r4
            goto L_0x00f6
        L_0x00f4:
            r2 = r18
        L_0x00f6:
            com.miui.antispam.db.d.a(r1, r4, r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x00f9:
            java.lang.String r2 = "CKSell"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x0112
            java.lang.String r2 = "CKSell"
            boolean r2 = r5.optBoolean(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x010d
            if (r3 == 0) goto L_0x010d
            r2 = r4
            goto L_0x010f
        L_0x010d:
            r2 = r18
        L_0x010f:
            com.miui.antispam.db.d.d(r1, r4, r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0112:
            java.lang.String r2 = "CKHarass"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x012b
            java.lang.String r2 = "CKHarass"
            boolean r2 = r5.optBoolean(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x0126
            if (r3 == 0) goto L_0x0126
            r2 = r4
            goto L_0x0128
        L_0x0126:
            r2 = r18
        L_0x0128:
            com.miui.antispam.db.d.c(r1, r4, r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x012b:
            java.lang.String r2 = "CKRepeatedMarkNum"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x013c
            java.lang.String r2 = "CKRepeatedMarkNum"
            boolean r2 = r5.optBoolean(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b((int) r4, (boolean) r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x013c:
            java.lang.String r2 = "CKCallTransfer"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x014d
            java.lang.String r2 = "CKCallTransfer"
            boolean r2 = r5.optBoolean(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.a((int) r4, (boolean) r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x014d:
            java.lang.String r2 = "CKKeywordsBlack"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x01ac
            java.lang.String r2 = "CKKeywordsBlack"
            org.json.JSONArray r2 = r5.optJSONArray(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r13 = r18
        L_0x015d:
            int r4 = r2.length()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r13 >= r4) goto L_0x01ac
            java.lang.String r4 = r2.optString(r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r21 = r2
            r2 = 1
            boolean r22 = miui.provider.ExtraTelephony.containsKeywords(r1, r4, r2, r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r22 != 0) goto L_0x019b
            android.net.Uri r2 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r2 = android.content.ContentProviderOperation.newInsert(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r22 = r3
            java.lang.String r3 = "data"
            android.content.ContentProviderOperation$Builder r2 = r2.withValue(r3, r4)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r3 = 1
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r2 = r2.withValue(r15, r4)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r3 = r19
            android.content.ContentProviderOperation$Builder r2 = r2.withValue(r3, r4)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation r2 = r2.build()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r4 = r17
            r4.add(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            goto L_0x01a1
        L_0x019b:
            r22 = r3
            r4 = r17
            r3 = r19
        L_0x01a1:
            int r13 = r13 + 1
            r19 = r3
            r17 = r4
            r2 = r21
            r3 = r22
            goto L_0x015d
        L_0x01ac:
            r22 = r3
            r4 = r17
            r3 = r19
            java.lang.String r2 = "CKKeywordsWhite"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x0206
            java.lang.String r2 = "CKKeywordsWhite"
            org.json.JSONArray r2 = r5.optJSONArray(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r17 = r6
            r13 = r18
        L_0x01c4:
            int r6 = r2.length()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r13 >= r6) goto L_0x0208
            java.lang.String r6 = r2.optString(r13)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r19 = r2
            r2 = 4
            r21 = r7
            r7 = 1
            boolean r2 = miui.provider.ExtraTelephony.containsKeywords(r1, r6, r2, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 != 0) goto L_0x01ff
            android.net.Uri r2 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r2 = android.content.ContentProviderOperation.newInsert(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.String r7 = "data"
            android.content.ContentProviderOperation$Builder r2 = r2.withValue(r7, r6)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r6 = 1
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r2 = r2.withValue(r15, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r6 = 4
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r2 = r2.withValue(r3, r6)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation r2 = r2.build()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r4.add(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x01ff:
            int r13 = r13 + 1
            r2 = r19
            r7 = r21
            goto L_0x01c4
        L_0x0206:
            r17 = r6
        L_0x0208:
            r21 = r7
            java.lang.String r2 = "CKPhoneList"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x02cb
            java.lang.String r2 = "CKPhoneList"
            org.json.JSONArray r2 = r5.optJSONArray(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r6 = r18
        L_0x021a:
            int r7 = r2.length()     // Catch:{ Exception -> 0x02b7 }
            if (r6 >= r7) goto L_0x02cb
            org.json.JSONObject r7 = r2.getJSONObject(r6)     // Catch:{ Exception -> 0x02b7 }
            java.lang.String r13 = "number"
            java.lang.String r13 = r7.getString(r13)     // Catch:{ Exception -> 0x02b7 }
            r19 = r2
            r2 = r16
            boolean r16 = r7.has(r2)     // Catch:{ Exception -> 0x02ad }
            if (r16 == 0) goto L_0x0245
            java.lang.String r16 = r7.getString(r2)     // Catch:{ Exception -> 0x0239 }
            goto L_0x0247
        L_0x0239:
            r0 = move-exception
            r6 = r0
            r23 = r8
            r16 = r9
            r24 = r10
            r25 = r11
            goto L_0x02c3
        L_0x0245:
            java.lang.String r16 = ""
        L_0x0247:
            r23 = r8
            r8 = r16
            r16 = r9
            java.lang.String r9 = "state"
            int r9 = r7.getInt(r9)     // Catch:{ Exception -> 0x02ab }
            int r7 = r7.getInt(r3)     // Catch:{ Exception -> 0x02ab }
            r24 = r10
            r10 = 1
            boolean r25 = b.b.a.e.n.a(r1, r13, r9, r7, r10)     // Catch:{ Exception -> 0x02a9 }
            if (r25 != 0) goto L_0x0297
            android.net.Uri r10 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI     // Catch:{ Exception -> 0x02a9 }
            android.content.ContentProviderOperation$Builder r10 = android.content.ContentProviderOperation.newInsert(r10)     // Catch:{ Exception -> 0x02a9 }
            r25 = r11
            java.lang.String r11 = "number"
            android.content.ContentProviderOperation$Builder r10 = r10.withValue(r11, r13)     // Catch:{ Exception -> 0x0295 }
            android.content.ContentProviderOperation$Builder r8 = r10.withValue(r2, r8)     // Catch:{ Exception -> 0x0295 }
            java.lang.String r10 = "state"
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0295 }
            android.content.ContentProviderOperation$Builder r8 = r8.withValue(r10, r9)     // Catch:{ Exception -> 0x0295 }
            r9 = 1
            java.lang.Integer r10 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0295 }
            android.content.ContentProviderOperation$Builder r8 = r8.withValue(r15, r10)     // Catch:{ Exception -> 0x0295 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0295 }
            android.content.ContentProviderOperation$Builder r7 = r8.withValue(r3, r7)     // Catch:{ Exception -> 0x0295 }
            android.content.ContentProviderOperation r7 = r7.build()     // Catch:{ Exception -> 0x0295 }
            r4.add(r7)     // Catch:{ Exception -> 0x0295 }
            goto L_0x0299
        L_0x0295:
            r0 = move-exception
            goto L_0x02c2
        L_0x0297:
            r25 = r11
        L_0x0299:
            int r6 = r6 + 1
            r9 = r16
            r8 = r23
            r10 = r24
            r11 = r25
            r16 = r2
            r2 = r19
            goto L_0x021a
        L_0x02a9:
            r0 = move-exception
            goto L_0x02b4
        L_0x02ab:
            r0 = move-exception
            goto L_0x02b2
        L_0x02ad:
            r0 = move-exception
            r23 = r8
            r16 = r9
        L_0x02b2:
            r24 = r10
        L_0x02b4:
            r25 = r11
            goto L_0x02c2
        L_0x02b7:
            r0 = move-exception
            r23 = r8
            r24 = r10
            r25 = r11
            r2 = r16
            r16 = r9
        L_0x02c2:
            r6 = r0
        L_0x02c3:
            java.lang.String r7 = f7471a     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.String r8 = "restore phone list JSON failed. "
            android.util.Log.e(r7, r8, r6)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            goto L_0x02d5
        L_0x02cb:
            r23 = r8
            r24 = r10
            r25 = r11
            r2 = r16
            r16 = r9
        L_0x02d5:
            java.lang.String r6 = "CKMmsModes"
            boolean r6 = r5.has(r6)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r6 == 0) goto L_0x0300
            java.lang.String r6 = "mms_mode"
            java.lang.String r7 = "CKMmsModes"
            int r5 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r7 = 1
            com.miui.antispam.db.d.b(r1, r6, r7, r5)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            goto L_0x0300
        L_0x02ea:
            r22 = r3
            r21 = r7
            r23 = r8
            r24 = r10
            r25 = r11
            r20 = r13
            r2 = r16
            r4 = r17
            r3 = r19
            r17 = r6
            r16 = r9
        L_0x0300:
            java.lang.String r5 = "sim_id_2"
            r6 = r27
            boolean r5 = r6.has(r5)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r5 == 0) goto L_0x054a
            java.lang.String r5 = "sim_id_2"
            org.json.JSONObject r5 = r6.optJSONObject(r5)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            boolean r7 = r5.has(r14)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r8 = 2
            if (r7 == 0) goto L_0x031e
            boolean r7 = r5.optBoolean(r14)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.a((android.content.Context) r1, (int) r8, (boolean) r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x031e:
            r7 = r20
            boolean r9 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 == 0) goto L_0x032d
            int r7 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.a((android.content.Context) r1, (int) r7, (int) r8)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x032d:
            boolean r7 = r5.has(r12)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x033c
            java.lang.String r7 = "stranger_sms_mode"
            int r9 = r5.optInt(r12)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r7, r8, r9)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x033c:
            r7 = r25
            boolean r9 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 == 0) goto L_0x034d
            java.lang.String r9 = "stranger_call_mode"
            int r7 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r9, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x034d:
            r7 = r24
            boolean r9 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 == 0) goto L_0x035e
            java.lang.String r9 = "contact_sms_mode"
            int r7 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r9, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x035e:
            r7 = r16
            boolean r9 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 == 0) goto L_0x036f
            java.lang.String r9 = "contact_call_mode"
            int r7 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r9, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x036f:
            r7 = r23
            boolean r9 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 == 0) goto L_0x0380
            java.lang.String r9 = "service_sms_mode"
            int r7 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r9, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0380:
            r7 = r21
            boolean r9 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 == 0) goto L_0x0391
            java.lang.String r9 = "empty_call_mode"
            int r7 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r9, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0391:
            r7 = r17
            boolean r9 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 == 0) goto L_0x03a2
            java.lang.String r9 = "oversea_call_mode"
            int r7 = r5.optInt(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b(r1, r9, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x03a2:
            java.lang.String r7 = "CKFraud"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x03bb
            java.lang.String r7 = "CKFraud"
            boolean r7 = r5.optBoolean(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x03b6
            if (r22 == 0) goto L_0x03b6
            r7 = 1
            goto L_0x03b8
        L_0x03b6:
            r7 = r18
        L_0x03b8:
            com.miui.antispam.db.d.b(r1, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x03bb:
            java.lang.String r7 = "CKAgent"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x03d4
            java.lang.String r7 = "CKAgent"
            boolean r7 = r5.optBoolean(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x03cf
            if (r22 == 0) goto L_0x03cf
            r7 = 1
            goto L_0x03d1
        L_0x03cf:
            r7 = r18
        L_0x03d1:
            com.miui.antispam.db.d.a(r1, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x03d4:
            java.lang.String r7 = "CKSell"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x03ed
            java.lang.String r7 = "CKSell"
            boolean r7 = r5.optBoolean(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x03e8
            if (r22 == 0) goto L_0x03e8
            r7 = 1
            goto L_0x03ea
        L_0x03e8:
            r7 = r18
        L_0x03ea:
            com.miui.antispam.db.d.d(r1, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x03ed:
            java.lang.String r7 = "CKHarass"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x0406
            java.lang.String r7 = "CKHarass"
            boolean r7 = r5.optBoolean(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x0401
            if (r22 == 0) goto L_0x0401
            r7 = 1
            goto L_0x0403
        L_0x0401:
            r7 = r18
        L_0x0403:
            com.miui.antispam.db.d.c(r1, r8, r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0406:
            java.lang.String r7 = "CKRepeatedMarkNum"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x0417
            java.lang.String r7 = "CKRepeatedMarkNum"
            boolean r7 = r5.optBoolean(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.b((int) r8, (boolean) r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0417:
            java.lang.String r7 = "CKCallTransfer"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x0428
            java.lang.String r7 = "CKCallTransfer"
            boolean r7 = r5.optBoolean(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            com.miui.antispam.db.d.a((int) r8, (boolean) r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0428:
            java.lang.String r7 = "CKKeywordsBlack"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x0472
            java.lang.String r7 = "CKKeywordsBlack"
            org.json.JSONArray r7 = r5.optJSONArray(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r9 = r18
        L_0x0438:
            int r10 = r7.length()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 >= r10) goto L_0x0472
            java.lang.String r10 = r7.optString(r9)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r11 = 1
            boolean r12 = miui.provider.ExtraTelephony.containsKeywords(r1, r10, r11, r8)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r12 != 0) goto L_0x046e
            android.net.Uri r11 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r11 = android.content.ContentProviderOperation.newInsert(r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.String r12 = "data"
            android.content.ContentProviderOperation$Builder r10 = r11.withValue(r12, r10)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.Integer r11 = java.lang.Integer.valueOf(r8)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r10 = r10.withValue(r15, r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r11 = 1
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r10 = r10.withValue(r3, r12)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation r10 = r10.build()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r4.add(r10)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            goto L_0x046f
        L_0x046e:
            r11 = 1
        L_0x046f:
            int r9 = r9 + 1
            goto L_0x0438
        L_0x0472:
            java.lang.String r7 = "CKKeywordsWhite"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x04ba
            java.lang.String r7 = "CKKeywordsWhite"
            org.json.JSONArray r7 = r5.optJSONArray(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r9 = r18
        L_0x0482:
            int r10 = r7.length()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r9 >= r10) goto L_0x04ba
            java.lang.String r10 = r7.optString(r9)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r11 = 4
            boolean r11 = miui.provider.ExtraTelephony.containsKeywords(r1, r10, r11, r8)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r11 != 0) goto L_0x04b7
            android.net.Uri r11 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r11 = android.content.ContentProviderOperation.newInsert(r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.String r12 = "data"
            android.content.ContentProviderOperation$Builder r10 = r11.withValue(r12, r10)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.Integer r11 = java.lang.Integer.valueOf(r8)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r10 = r10.withValue(r15, r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r11 = 4
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation$Builder r10 = r10.withValue(r3, r11)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            android.content.ContentProviderOperation r10 = r10.build()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r4.add(r10)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x04b7:
            int r9 = r9 + 1
            goto L_0x0482
        L_0x04ba:
            java.lang.String r7 = "CKPhoneList"
            boolean r7 = r5.has(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r7 == 0) goto L_0x0536
            java.lang.String r7 = "CKPhoneList"
            org.json.JSONArray r7 = r5.optJSONArray(r7)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r9 = r18
        L_0x04ca:
            int r10 = r7.length()     // Catch:{ Exception -> 0x052d }
            if (r9 >= r10) goto L_0x0536
            org.json.JSONObject r10 = r7.getJSONObject(r9)     // Catch:{ Exception -> 0x052d }
            java.lang.String r11 = "number"
            java.lang.String r11 = r10.getString(r11)     // Catch:{ Exception -> 0x052d }
            boolean r12 = r10.has(r2)     // Catch:{ Exception -> 0x052d }
            if (r12 == 0) goto L_0x04e5
            java.lang.String r12 = r10.getString(r2)     // Catch:{ Exception -> 0x052d }
            goto L_0x04e7
        L_0x04e5:
            java.lang.String r12 = ""
        L_0x04e7:
            java.lang.String r13 = "state"
            int r13 = r10.getInt(r13)     // Catch:{ Exception -> 0x052d }
            int r10 = r10.getInt(r3)     // Catch:{ Exception -> 0x052d }
            boolean r14 = b.b.a.e.n.a(r1, r11, r13, r10, r8)     // Catch:{ Exception -> 0x052d }
            if (r14 != 0) goto L_0x0529
            android.net.Uri r14 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI     // Catch:{ Exception -> 0x052d }
            android.content.ContentProviderOperation$Builder r14 = android.content.ContentProviderOperation.newInsert(r14)     // Catch:{ Exception -> 0x052d }
            java.lang.String r8 = "number"
            android.content.ContentProviderOperation$Builder r8 = r14.withValue(r8, r11)     // Catch:{ Exception -> 0x052d }
            android.content.ContentProviderOperation$Builder r8 = r8.withValue(r2, r12)     // Catch:{ Exception -> 0x052d }
            java.lang.String r11 = "state"
            java.lang.Integer r12 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x052d }
            android.content.ContentProviderOperation$Builder r8 = r8.withValue(r11, r12)     // Catch:{ Exception -> 0x052d }
            r11 = 2
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x052d }
            android.content.ContentProviderOperation$Builder r8 = r8.withValue(r15, r12)     // Catch:{ Exception -> 0x052d }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x052d }
            android.content.ContentProviderOperation$Builder r8 = r8.withValue(r3, r10)     // Catch:{ Exception -> 0x052d }
            android.content.ContentProviderOperation r8 = r8.build()     // Catch:{ Exception -> 0x052d }
            r4.add(r8)     // Catch:{ Exception -> 0x052d }
        L_0x0529:
            int r9 = r9 + 1
            r8 = 2
            goto L_0x04ca
        L_0x052d:
            r0 = move-exception
            r2 = r0
            java.lang.String r3 = f7471a     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.String r7 = "restore phone list JSON failed. "
            android.util.Log.e(r3, r7, r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0536:
            java.lang.String r2 = "CKMmsModes"
            boolean r2 = r5.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x054a
            java.lang.String r2 = "mms_mode"
            java.lang.String r3 = "CKMmsModes"
            int r3 = r5.optInt(r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            r5 = 2
            com.miui.antispam.db.d.b(r1, r2, r5, r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x054a:
            java.lang.String r2 = "CKAutoUpdateLibrary"
            boolean r2 = r6.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x055b
            java.lang.String r2 = "CKAutoUpdateLibrary"
            boolean r2 = r6.optBoolean(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.d(r1, r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x055b:
            java.lang.String r2 = "CKSimsShareSettings"
            boolean r2 = r6.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x056c
            java.lang.String r2 = "CKSimsShareSettings"
            boolean r2 = r6.optBoolean(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.a((android.content.Context) r1, (boolean) r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x056c:
            java.lang.String r2 = "CKReportedNumberGuideFraud"
            boolean r2 = r6.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x057f
            java.lang.String r2 = "mark_guide_fraud"
            java.lang.String r3 = "CKReportedNumberGuideFraud"
            boolean r3 = r6.optBoolean(r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.b((android.content.Context) r1, (java.lang.String) r2, (boolean) r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x057f:
            java.lang.String r2 = "CKReportedNumberGuideAgent"
            boolean r2 = r6.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x0592
            java.lang.String r2 = "mark_guide_agent"
            java.lang.String r3 = "CKReportedNumberGuideAgent"
            boolean r3 = r6.optBoolean(r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.b((android.content.Context) r1, (java.lang.String) r2, (boolean) r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x0592:
            java.lang.String r2 = "CKReportedNumberGuideSell"
            boolean r2 = r6.has(r2)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            if (r2 == 0) goto L_0x05a5
            java.lang.String r2 = "mark_guide_sell"
            java.lang.String r3 = "CKReportedNumberGuideSell"
            boolean r3 = r6.optBoolean(r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            b.b.a.e.c.b((android.content.Context) r1, (java.lang.String) r2, (boolean) r3)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
        L_0x05a5:
            android.content.ContentResolver r1 = r26.getContentResolver()     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            java.lang.String r2 = "antispam"
            r1.applyBatch(r2, r4)     // Catch:{ RemoteException -> 0x05b1, OperationApplicationException -> 0x05af }
            goto L_0x05ba
        L_0x05af:
            r0 = move-exception
            goto L_0x05b2
        L_0x05b1:
            r0 = move-exception
        L_0x05b2:
            r1 = r0
            java.lang.String r2 = f7471a
            java.lang.String r3 = "restore antispam settings JSON failed. "
            android.util.Log.e(r2, r3, r1)
        L_0x05ba:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.cloudbackup.b.a(android.content.Context, org.json.JSONObject):void");
    }
}
