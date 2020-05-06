package com.miui.optimizecenter.storage;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.miui.securitycenter.Application;
import java.util.concurrent.atomic.AtomicInteger;

public class j {

    /* renamed from: a  reason: collision with root package name */
    private static j f5745a;

    /* renamed from: b  reason: collision with root package name */
    private static volatile AtomicInteger f5746b = new AtomicInteger(0);

    /* renamed from: c  reason: collision with root package name */
    private String f5747c = Application.d().getApplicationContext().getDatabasePath("parse.db").getAbsolutePath();

    /* renamed from: d  reason: collision with root package name */
    private final String f5748d = "parse.db";
    private final String e = "DIR_PARSE";
    private SQLiteDatabase f;

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0062, code lost:
        if (r3 != null) goto L_0x0064;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0064, code lost:
        b.b.i.b.b.a((java.io.OutputStream) r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0076, code lost:
        if (r3 != null) goto L_0x0064;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0079, code lost:
        r7.f = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(r0, (android.database.sqlite.SQLiteDatabase.CursorFactory) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007f, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0089  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private j() {
        /*
            r7 = this;
            r7.<init>()
            java.lang.String r0 = "parse.db"
            r7.f5748d = r0
            java.lang.String r1 = "DIR_PARSE"
            r7.e = r1
            com.miui.securitycenter.Application r1 = com.miui.securitycenter.Application.d()
            android.content.Context r1 = r1.getApplicationContext()
            java.io.File r0 = r1.getDatabasePath(r0)
            java.lang.String r0 = r0.getAbsolutePath()
            r7.f5747c = r0
            java.io.File r0 = new java.io.File
            java.lang.String r1 = r7.f5747c
            r0.<init>(r1)
            r1 = 0
            boolean r2 = r0.exists()     // Catch:{ Exception -> 0x006b, all -> 0x0068 }
            if (r2 != 0) goto L_0x005b
            com.miui.securitycenter.Application r2 = com.miui.securitycenter.Application.d()     // Catch:{ Exception -> 0x006b, all -> 0x0068 }
            android.content.Context r2 = r2.getApplicationContext()     // Catch:{ Exception -> 0x006b, all -> 0x0068 }
            android.content.res.Resources r2 = r2.getResources()     // Catch:{ Exception -> 0x006b, all -> 0x0068 }
            r3 = 2131689491(0x7f0f0013, float:1.9007999E38)
            java.io.InputStream r2 = r2.openRawResource(r3)     // Catch:{ Exception -> 0x006b, all -> 0x0068 }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0058, all -> 0x0056 }
            java.lang.String r4 = r7.f5747c     // Catch:{ Exception -> 0x0058, all -> 0x0056 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0058, all -> 0x0056 }
            r4 = 7168(0x1c00, float:1.0045E-41)
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0054 }
        L_0x0049:
            int r5 = r2.read(r4)     // Catch:{ Exception -> 0x0054 }
            if (r5 <= 0) goto L_0x005d
            r6 = 0
            r3.write(r4, r6, r5)     // Catch:{ Exception -> 0x0054 }
            goto L_0x0049
        L_0x0054:
            r4 = move-exception
            goto L_0x006e
        L_0x0056:
            r0 = move-exception
            goto L_0x0082
        L_0x0058:
            r4 = move-exception
            r3 = r1
            goto L_0x006e
        L_0x005b:
            r2 = r1
            r3 = r2
        L_0x005d:
            if (r2 == 0) goto L_0x0062
            b.b.i.b.b.a((java.io.InputStream) r2)
        L_0x0062:
            if (r3 == 0) goto L_0x0079
        L_0x0064:
            b.b.i.b.b.a((java.io.OutputStream) r3)
            goto L_0x0079
        L_0x0068:
            r0 = move-exception
            r2 = r1
            goto L_0x0082
        L_0x006b:
            r4 = move-exception
            r2 = r1
            r3 = r2
        L_0x006e:
            r4.printStackTrace()     // Catch:{ all -> 0x0080 }
            if (r2 == 0) goto L_0x0076
            b.b.i.b.b.a((java.io.InputStream) r2)
        L_0x0076:
            if (r3 == 0) goto L_0x0079
            goto L_0x0064
        L_0x0079:
            android.database.sqlite.SQLiteDatabase r0 = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(r0, r1)
            r7.f = r0
            return
        L_0x0080:
            r0 = move-exception
            r1 = r3
        L_0x0082:
            if (r2 == 0) goto L_0x0087
            b.b.i.b.b.a((java.io.InputStream) r2)
        L_0x0087:
            if (r1 == 0) goto L_0x008c
            b.b.i.b.b.a((java.io.OutputStream) r1)
        L_0x008c:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.optimizecenter.storage.j.<init>():void");
    }

    public static j b() {
        if (f5745a == null) {
            f5745a = new j();
        }
        return f5745a;
    }

    /* JADX WARNING: type inference failed for: r5v1, types: [java.lang.String[]] */
    /* JADX WARNING: type inference failed for: r5v2, types: [android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r5v4 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<java.lang.String> a(java.lang.String r5) {
        /*
            r4 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "select _id, PATH, PACKAGE_NAME from "
            r1.append(r2)
            java.lang.String r2 = "DIR_PARSE"
            r1.append(r2)
            java.lang.String r2 = " where PACKAGE_NAME = "
            r1.append(r2)
            java.lang.String r2 = "'"
            r1.append(r2)
            r1.append(r5)
            r1.append(r2)
            r5 = 0
            android.database.sqlite.SQLiteDatabase r2 = r4.f     // Catch:{ all -> 0x0072 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0072 }
            android.database.Cursor r5 = r2.rawQuery(r1, r5)     // Catch:{ all -> 0x0072 }
            int r1 = r5.getColumnCount()     // Catch:{ all -> 0x0072 }
            if (r1 > 0) goto L_0x003b
            if (r5 == 0) goto L_0x003a
            r5.close()
        L_0x003a:
            return r0
        L_0x003b:
            java.io.File r1 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ all -> 0x0072 }
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ all -> 0x0072 }
        L_0x0043:
            boolean r2 = r5.moveToNext()     // Catch:{ all -> 0x0072 }
            if (r2 == 0) goto L_0x006c
            java.lang.String r2 = "PATH"
            int r2 = r5.getColumnIndex(r2)     // Catch:{ all -> 0x0072 }
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x0072 }
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0072 }
            if (r3 != 0) goto L_0x0043
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0072 }
            r3.<init>()     // Catch:{ all -> 0x0072 }
            r3.append(r1)     // Catch:{ all -> 0x0072 }
            r3.append(r2)     // Catch:{ all -> 0x0072 }
            java.lang.String r2 = r3.toString()     // Catch:{ all -> 0x0072 }
            r0.add(r2)     // Catch:{ all -> 0x0072 }
            goto L_0x0043
        L_0x006c:
            if (r5 == 0) goto L_0x0071
            r5.close()
        L_0x0071:
            return r0
        L_0x0072:
            r0 = move-exception
            if (r5 == 0) goto L_0x0078
            r5.close()
        L_0x0078:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.optimizecenter.storage.j.a(java.lang.String):java.util.List");
    }

    public synchronized void a() {
        int decrementAndGet = f5746b.decrementAndGet();
        Log.i("DbManager", "openDb: cnt close = " + f5746b);
        if (this.f != null && this.f.isOpen() && decrementAndGet == 0) {
            this.f.close();
        }
    }

    public synchronized SQLiteDatabase c() {
        f5746b.incrementAndGet();
        Log.i("DbManager", "openDb: cnt open = " + f5746b);
        if (!this.f.isOpen()) {
            this.f = SQLiteDatabase.openDatabase(this.f5747c, (SQLiteDatabase.CursorFactory) null, 0);
        }
        return this.f;
    }
}
