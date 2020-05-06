package com.xiaomi.stat.a;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import com.xiaomi.stat.MiStatParams;
import com.xiaomi.stat.a;
import com.xiaomi.stat.a.l;
import com.xiaomi.stat.ak;
import com.xiaomi.stat.d.k;
import com.xiaomi.stat.d.m;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8367a = "EventManager";

    /* renamed from: b  reason: collision with root package name */
    private static final int f8368b = 10;

    /* renamed from: c  reason: collision with root package name */
    private static final int f8369c = 0;

    /* renamed from: d  reason: collision with root package name */
    private static final int f8370d = 300;
    private static final int e = 122880;
    private static final int f = 55;
    private static final int g = 2;
    private static final String h = "priority DESC, _id ASC";
    private static final int i = 7;
    private static final long j = 52428800;
    private static c k;
    /* access modifiers changed from: private */
    public a l;
    private File m;

    private c() {
        Context a2 = ak.a();
        this.l = new a(a2);
        this.m = a2.getDatabasePath(j.f8381a);
    }

    public static c a() {
        if (k == null) {
            synchronized (c.class) {
                if (k == null) {
                    k = new c();
                }
            }
        }
        return k;
    }

    private void a(MiStatParams miStatParams) {
        miStatParams.putString(l.a.n, com.xiaomi.stat.d.c.b());
        miStatParams.putString(l.a.o, a.g);
        miStatParams.putString(l.a.p, m.c());
        miStatParams.putString(l.a.q, m.d());
        miStatParams.putString(l.a.r, com.xiaomi.stat.d.l.b(ak.a()));
        miStatParams.putString(l.a.s, m.a(ak.a()));
        miStatParams.putString(l.a.t, Build.MANUFACTURER);
        miStatParams.putString(l.a.u, Build.MODEL);
        miStatParams.putString(l.a.v, m.b());
    }

    private boolean a(b[] bVarArr, String str, String str2, boolean z) {
        for (b a2 : bVarArr) {
            if (a2.a(str, str2, z)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:30|(4:31|32|33|(14:34|35|36|37|38|39|40|41|42|43|44|45|46|47))|58|59|(2:90|61)(3:62|93|63)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:58:0x013c */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x014a A[Catch:{ Exception -> 0x0185, all -> 0x0183 }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01a1  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0144 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.xiaomi.stat.a.k b(com.xiaomi.stat.a.b[] r35) {
        /*
            r34 = this;
            r1 = r34
            r0 = r35
            java.lang.String r2 = "ps"
            java.lang.String r3 = "ts"
            java.lang.String r4 = "tp"
            java.lang.String r5 = "eg"
            java.lang.String r6 = "e"
            int r8 = r0.length     // Catch:{ Exception -> 0x018b, all -> 0x0188 }
            r9 = 0
            r10 = 1
            if (r8 != r10) goto L_0x001c
            r8 = r0[r9]     // Catch:{ Exception -> 0x018b, all -> 0x0188 }
            java.lang.String r8 = r8.a()     // Catch:{ Exception -> 0x018b, all -> 0x0188 }
            r14 = r8
            r8 = r9
            goto L_0x001e
        L_0x001c:
            r8 = r10
            r14 = 0
        L_0x001e:
            com.xiaomi.stat.a.a r11 = r1.l     // Catch:{ Exception -> 0x018b, all -> 0x0188 }
            android.database.sqlite.SQLiteDatabase r11 = r11.getReadableDatabase()     // Catch:{ Exception -> 0x018b, all -> 0x0188 }
            java.lang.String r12 = "events"
            r13 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            java.lang.String r18 = "priority DESC, _id ASC"
            android.database.Cursor r11 = r11.query(r12, r13, r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x018b, all -> 0x0188 }
            java.lang.String r12 = "_id"
            int r12 = r11.getColumnIndex(r12)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            int r13 = r11.getColumnIndex(r6)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            int r14 = r11.getColumnIndex(r5)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            int r15 = r11.getColumnIndex(r4)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            int r9 = r11.getColumnIndex(r3)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            int r7 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            java.lang.String r10 = "sub"
            int r10 = r11.getColumnIndex(r10)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r19 = r2
            java.lang.String r2 = "is_am"
            int r2 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r20 = r3
            org.json.JSONArray r3 = new org.json.JSONArray     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r3.<init>()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r21 = r3
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r3.<init>()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r22 = 0
        L_0x006a:
            boolean r23 = r11.moveToNext()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            if (r23 == 0) goto L_0x0168
            r24 = r3
            r23 = r4
            long r3 = r11.getLong(r12)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r25 = r12
            java.lang.String r12 = r11.getString(r13)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r26 = r13
            java.lang.String r13 = r11.getString(r14)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r27 = r14
            java.lang.String r14 = r11.getString(r15)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r28 = r3
            long r3 = r11.getLong(r9)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r30 = r9
            java.lang.String r9 = r11.getString(r7)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r31 = r7
            java.lang.String r7 = r11.getString(r10)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r32 = r10
            int r10 = r11.getInt(r2)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r33 = r2
            r2 = 1
            if (r10 != r2) goto L_0x00a9
            r10 = r2
            goto L_0x00aa
        L_0x00a9:
            r10 = 0
        L_0x00aa:
            if (r8 == 0) goto L_0x00bf
            boolean r7 = r1.a(r0, r7, r13, r10)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            if (r7 == 0) goto L_0x00b3
            goto L_0x00bf
        L_0x00b3:
            r4 = r19
            r13 = r20
            r3 = r21
            r12 = r23
            r1 = r24
            goto L_0x014c
        L_0x00bf:
            int r7 = r9.length()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            int r7 = r7 * 2
            int r7 = r7 + 55
            int r22 = r22 + r7
            boolean r7 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            if (r7 != 0) goto L_0x00d7
            int r7 = r12.length()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            int r7 = r7 * 2
            int r22 = r22 + r7
        L_0x00d7:
            boolean r7 = android.text.TextUtils.isEmpty(r13)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            if (r7 != 0) goto L_0x00e5
            int r7 = r13.length()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            int r7 = r7 * 2
            int r22 = r22 + r7
        L_0x00e5:
            r7 = r22
            r10 = 122880(0x1e000, float:1.72192E-40)
            if (r7 <= r10) goto L_0x00f3
            r3 = r21
            r1 = r24
            r2 = 0
            goto L_0x016c
        L_0x00f3:
            org.json.JSONObject r10 = new org.json.JSONObject     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r10.<init>()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r10.put(r6, r12)     // Catch:{ JSONException -> 0x0132 }
            r10.put(r5, r13)     // Catch:{ JSONException -> 0x0132 }
            r12 = r23
            r10.put(r12, r14)     // Catch:{ JSONException -> 0x012b }
            r13 = r20
            r10.put(r13, r3)     // Catch:{ JSONException -> 0x0128 }
            java.lang.String r3 = "eid"
            r0 = r28
            r10.put(r3, r0)     // Catch:{ JSONException -> 0x0128 }
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0128 }
            r3.<init>(r9)     // Catch:{ JSONException -> 0x0128 }
            r4 = r19
            r10.put(r4, r3)     // Catch:{ JSONException -> 0x012f }
            r3 = r21
            r3.put(r10)     // Catch:{ JSONException -> 0x013a }
            java.lang.Long r0 = java.lang.Long.valueOf(r0)     // Catch:{ JSONException -> 0x013a }
            r1 = r24
            r1.add(r0)     // Catch:{ JSONException -> 0x013c }
            goto L_0x013c
        L_0x0128:
            r4 = r19
            goto L_0x012f
        L_0x012b:
            r4 = r19
            r13 = r20
        L_0x012f:
            r3 = r21
            goto L_0x013a
        L_0x0132:
            r4 = r19
            r13 = r20
            r3 = r21
            r12 = r23
        L_0x013a:
            r1 = r24
        L_0x013c:
            int r0 = r1.size()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r9 = 300(0x12c, float:4.2E-43)
            if (r0 < r9) goto L_0x014a
            boolean r9 = r11.isLast()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r2 = r9
            goto L_0x016c
        L_0x014a:
            r22 = r7
        L_0x014c:
            r0 = r35
            r21 = r3
            r19 = r4
            r4 = r12
            r20 = r13
            r12 = r25
            r13 = r26
            r14 = r27
            r9 = r30
            r7 = r31
            r10 = r32
            r2 = r33
            r3 = r1
            r1 = r34
            goto L_0x006a
        L_0x0168:
            r1 = r3
            r3 = r21
            r2 = 1
        L_0x016c:
            int r0 = r1.size()     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            if (r0 <= 0) goto L_0x017d
            com.xiaomi.stat.a.k r0 = new com.xiaomi.stat.a.k     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            r0.<init>(r3, r1, r2)     // Catch:{ Exception -> 0x0185, all -> 0x0183 }
            if (r11 == 0) goto L_0x017c
            r11.close()
        L_0x017c:
            return r0
        L_0x017d:
            if (r11 == 0) goto L_0x019b
            r11.close()
            goto L_0x019b
        L_0x0183:
            r0 = move-exception
            goto L_0x019f
        L_0x0185:
            r0 = move-exception
            r7 = r11
            goto L_0x018d
        L_0x0188:
            r0 = move-exception
            r11 = 0
            goto L_0x019f
        L_0x018b:
            r0 = move-exception
            r7 = 0
        L_0x018d:
            java.lang.String r1 = "EventManager"
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x019d }
            com.xiaomi.stat.d.k.b(r1, r0)     // Catch:{ all -> 0x019d }
            if (r7 == 0) goto L_0x019b
            r7.close()
        L_0x019b:
            r1 = 0
            return r1
        L_0x019d:
            r0 = move-exception
            r11 = r7
        L_0x019f:
            if (r11 == 0) goto L_0x01a4
            r11.close()
        L_0x01a4:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.a.c.b(com.xiaomi.stat.a.b[]):com.xiaomi.stat.a.k");
    }

    /* access modifiers changed from: private */
    public void b(l lVar) {
        d();
        SQLiteDatabase writableDatabase = this.l.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("e", lVar.f8388a);
        contentValues.put("eg", lVar.f8389b);
        contentValues.put("tp", lVar.f8390c);
        contentValues.put("ts", Long.valueOf(lVar.e));
        if (c(lVar)) {
            a((MiStatParams) lVar.f8391d);
        }
        contentValues.put("ps", lVar.f8391d.toJsonString());
        contentValues.put(j.i, lVar.f);
        contentValues.put(j.j, Integer.valueOf(lVar.g ? 1 : 0));
        contentValues.put(j.k, Integer.valueOf(TextUtils.equals(lVar.f8389b, l.a.h) ? 10 : 0));
        writableDatabase.insert(j.f8382b, (String) null, contentValues);
    }

    /* access modifiers changed from: private */
    public void b(ArrayList<Long> arrayList) {
        if (arrayList != null && arrayList.size() != 0) {
            try {
                SQLiteDatabase writableDatabase = this.l.getWritableDatabase();
                StringBuilder sb = new StringBuilder(((Long.toString(arrayList.get(0).longValue()).length() + 1) * arrayList.size()) + 16);
                sb.append("_id");
                sb.append(" in (");
                sb.append(arrayList.get(0));
                int size = arrayList.size();
                for (int i2 = 1; i2 < size; i2++) {
                    sb.append(",");
                    sb.append(arrayList.get(i2));
                }
                sb.append(")");
                int delete = writableDatabase.delete(j.f8382b, sb.toString(), (String[]) null);
                k.c(f8367a, "deleted events number " + delete);
            } catch (Exception unused) {
            }
        }
    }

    private boolean c(l lVar) {
        return !lVar.f8390c.startsWith(l.a.w);
    }

    private void d() {
        if (this.m.exists() && this.m.length() >= 52428800) {
            k.e(f8367a, "database too big: " + this.m.length());
            this.l.getWritableDatabase().delete(j.f8382b, (String) null, (String[]) null);
        }
    }

    public k a(b[] bVarArr) {
        FutureTask futureTask = new FutureTask(new e(this, bVarArr));
        com.xiaomi.stat.c.a(futureTask);
        try {
            return (k) futureTask.get();
        } catch (InterruptedException | ExecutionException unused) {
            return null;
        }
    }

    public void a(l lVar) {
        com.xiaomi.stat.c.a(new d(this, lVar));
        k.c(f8367a, "add event: name=" + lVar.f8388a);
    }

    public void a(String str) {
        com.xiaomi.stat.c.a(new h(this, str));
    }

    public void a(ArrayList<Long> arrayList) {
        FutureTask futureTask = new FutureTask(new f(this, arrayList), (Object) null);
        com.xiaomi.stat.c.a(futureTask);
        try {
            futureTask.get();
        } catch (InterruptedException | ExecutionException unused) {
        }
    }

    public void b() {
        com.xiaomi.stat.c.a(new g(this));
    }

    public long c() {
        FutureTask futureTask = new FutureTask(new i(this));
        com.xiaomi.stat.c.a(futureTask);
        try {
            return ((Long) futureTask.get()).longValue();
        } catch (InterruptedException | ExecutionException unused) {
            return -1;
        }
    }
}
