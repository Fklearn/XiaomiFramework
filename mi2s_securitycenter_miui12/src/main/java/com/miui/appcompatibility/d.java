package com.miui.appcompatibility;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import b.b.o.g.e;
import com.miui.appcompatibility.data.PackageData;
import com.miui.securityscan.d.a;
import com.miui.securityscan.d.c;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import miui.util.IOUtils;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static d f3076a;

    /* renamed from: b  reason: collision with root package name */
    private a f3077b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f3078c;

    /* renamed from: d  reason: collision with root package name */
    private com.miui.appcompatibility.a.d f3079d;
    /* access modifiers changed from: private */
    public int e = 0;

    private d(Context context) {
        this.f3078c = context;
        this.f3077b = c.a(context).a();
    }

    private synchronized int a(String str) {
        int delete;
        try {
            SQLiteDatabase writableDatabase = this.f3077b.getWritableDatabase();
            delete = writableDatabase.delete("tb_incompatible_app_list", "pkg_name = ?", new String[]{str});
            IOUtils.closeQuietly(writableDatabase);
        } catch (Exception e2) {
            try {
                e2.printStackTrace();
                IOUtils.closeQuietly((Closeable) null);
                return -1;
            } catch (Throwable th) {
                IOUtils.closeQuietly((Closeable) null);
                throw th;
            }
        }
        return delete;
    }

    private synchronized long a(PackageData packageData) {
        Log.d("AppCompatManager", "insertToDb");
        SQLiteDatabase sQLiteDatabase = null;
        try {
            SQLiteDatabase sQLiteDatabase2 = this.f3077b.getWritableDatabase();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("pkg_name", packageData.getPkg());
                contentValues.put("pkg_ver", packageData.getVer());
                contentValues.put("pkg_status", Integer.valueOf(packageData.getStatus()));
                long insert = sQLiteDatabase2.insert("tb_incompatible_app_list", (String) null, contentValues);
                IOUtils.closeQuietly(sQLiteDatabase2);
                return insert;
            } catch (Exception e2) {
                e = e2;
                sQLiteDatabase = sQLiteDatabase2;
                try {
                    e.printStackTrace();
                    IOUtils.closeQuietly(sQLiteDatabase);
                    return -1;
                } catch (Throwable th) {
                    th = th;
                    sQLiteDatabase2 = sQLiteDatabase;
                    IOUtils.closeQuietly(sQLiteDatabase2);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeQuietly(sQLiteDatabase2);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            IOUtils.closeQuietly(sQLiteDatabase);
            return -1;
        }
    }

    public static void a(Context context) {
        b.b.c.j.d.a(new c(context));
    }

    public static void a(Context context, String str) {
        List<String> c2 = c(context);
        for (int i = 0; i < c2.size(); i++) {
            if (c2.get(i).equals(str)) {
                c2.remove(i);
            }
        }
        c(context, c2);
    }

    public static void a(Context context, List<String> list) {
        if (list != null) {
            List<String> c2 = c(context);
            c2.addAll(list);
            c(context, c2);
        }
    }

    private synchronized int b(PackageData packageData) {
        int update;
        Log.d("AppCompatManager", "updateToDb");
        try {
            SQLiteDatabase writableDatabase = this.f3077b.getWritableDatabase();
            String[] strArr = {packageData.getPkg()};
            ContentValues contentValues = new ContentValues();
            contentValues.put("pkg_name", packageData.getPkg());
            contentValues.put("pkg_ver", packageData.getVer());
            contentValues.put("pkg_status", Integer.valueOf(packageData.getStatus()));
            update = writableDatabase.update("tb_incompatible_app_list", contentValues, "pkg_name = ?", strArr);
            IOUtils.closeQuietly(writableDatabase);
        } catch (Exception e2) {
            try {
                e2.printStackTrace();
                return -1;
            } finally {
                IOUtils.closeQuietly((Closeable) null);
            }
        }
        return update;
    }

    public static d b(Context context) {
        d dVar;
        synchronized (d.class) {
            if (f3076a == null) {
                f3076a = new d(context);
            }
            dVar = f3076a;
        }
        return dVar;
    }

    public static void b(Context context, String str) {
        List<String> c2 = c(context);
        c2.add(str);
        c(context, c2);
    }

    public static void b(Context context, List<String> list) {
        List<String> c2 = c(context);
        for (int i = 0; i < c2.size(); i++) {
            String str = c2.get(i);
            for (int i2 = 0; i2 < list.size(); i2++) {
                if (str.equals(list.get(i2))) {
                    c2.remove(i);
                }
            }
        }
        c(context, c2);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: android.database.sqlite.SQLiteDatabase} */
    /* JADX WARNING: type inference failed for: r2v1, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r2v2 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x006f A[SYNTHETIC, Splitter:B:31:0x006f] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x007a A[SYNTHETIC, Splitter:B:38:0x007a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean b(java.lang.String r13) {
        /*
            r12 = this;
            monitor-enter(r12)
            r0 = 0
            r1 = 1
            com.miui.securityscan.d.a r2 = r12.f3077b     // Catch:{ Exception -> 0x0068, all -> 0x0065 }
            android.database.sqlite.SQLiteDatabase r2 = r2.getWritableDatabase()     // Catch:{ Exception -> 0x0068, all -> 0x0065 }
            java.lang.String r3 = "_id"
            java.lang.String r4 = "os_ver"
            java.lang.String[] r5 = new java.lang.String[]{r3, r4}     // Catch:{ Exception -> 0x0063 }
            java.lang.String r6 = "os_ver = ?"
            java.lang.String[] r7 = new java.lang.String[r1]     // Catch:{ Exception -> 0x0063 }
            r11 = 0
            r7[r11] = r13     // Catch:{ Exception -> 0x0063 }
            java.lang.String r4 = "tb_os_ver"
            r8 = 0
            r9 = 0
            r10 = 0
            r3 = r2
            android.database.Cursor r0 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0063 }
            int r13 = r0.getCount()     // Catch:{ Exception -> 0x0063 }
            if (r13 <= 0) goto L_0x0059
        L_0x0028:
            boolean r13 = r0.moveToNext()     // Catch:{ Exception -> 0x0063 }
            if (r13 == 0) goto L_0x004f
            java.lang.String r13 = "os_ver"
            int r13 = r0.getColumnIndex(r13)     // Catch:{ Exception -> 0x0063 }
            java.lang.String r13 = r0.getString(r13)     // Catch:{ Exception -> 0x0063 }
            java.lang.String r3 = "AppCompatManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0063 }
            r4.<init>()     // Catch:{ Exception -> 0x0063 }
            java.lang.String r5 = " isEqualsCurOsver os_ver="
            r4.append(r5)     // Catch:{ Exception -> 0x0063 }
            r4.append(r13)     // Catch:{ Exception -> 0x0063 }
            java.lang.String r13 = r4.toString()     // Catch:{ Exception -> 0x0063 }
            android.util.Log.d(r3, r13)     // Catch:{ Exception -> 0x0063 }
            goto L_0x0028
        L_0x004f:
            if (r0 == 0) goto L_0x0054
            r0.close()     // Catch:{ all -> 0x0081 }
        L_0x0054:
            miui.util.IOUtils.closeQuietly(r2)     // Catch:{ all -> 0x0081 }
            monitor-exit(r12)
            return r1
        L_0x0059:
            if (r0 == 0) goto L_0x005e
            r0.close()     // Catch:{ all -> 0x0081 }
        L_0x005e:
            miui.util.IOUtils.closeQuietly(r2)     // Catch:{ all -> 0x0081 }
            monitor-exit(r12)
            return r11
        L_0x0063:
            r13 = move-exception
            goto L_0x006a
        L_0x0065:
            r13 = move-exception
            r2 = r0
            goto L_0x0078
        L_0x0068:
            r13 = move-exception
            r2 = r0
        L_0x006a:
            r13.printStackTrace()     // Catch:{ all -> 0x0077 }
            if (r0 == 0) goto L_0x0072
            r0.close()     // Catch:{ all -> 0x0081 }
        L_0x0072:
            miui.util.IOUtils.closeQuietly(r2)     // Catch:{ all -> 0x0081 }
            monitor-exit(r12)
            return r1
        L_0x0077:
            r13 = move-exception
        L_0x0078:
            if (r0 == 0) goto L_0x007d
            r0.close()     // Catch:{ all -> 0x0081 }
        L_0x007d:
            miui.util.IOUtils.closeQuietly(r2)     // Catch:{ all -> 0x0081 }
            throw r13     // Catch:{ all -> 0x0081 }
        L_0x0081:
            r13 = move-exception
            monitor-exit(r12)
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appcompatibility.d.b(java.lang.String):boolean");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: android.database.sqlite.SQLiteDatabase} */
    /* JADX WARNING: type inference failed for: r1v1, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r1v2 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005a A[SYNTHETIC, Splitter:B:21:0x005a] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0066 A[SYNTHETIC, Splitter:B:29:0x0066] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized int c(java.lang.String r11) {
        /*
            r10 = this;
            monitor-enter(r10)
            r0 = 0
            com.miui.securityscan.d.a r1 = r10.f3077b     // Catch:{ Exception -> 0x0053, all -> 0x0050 }
            android.database.sqlite.SQLiteDatabase r1 = r1.getWritableDatabase()     // Catch:{ Exception -> 0x0053, all -> 0x0050 }
            java.lang.String r2 = "_id"
            java.lang.String r3 = "pkg_name"
            java.lang.String r4 = "pkg_ver"
            java.lang.String r5 = "pkg_status"
            java.lang.String[] r4 = new java.lang.String[]{r2, r3, r4, r5}     // Catch:{ Exception -> 0x004e }
            java.lang.String r5 = "pkg_name = ?"
            r2 = 1
            java.lang.String[] r6 = new java.lang.String[r2]     // Catch:{ Exception -> 0x004e }
            r2 = 0
            r6[r2] = r11     // Catch:{ Exception -> 0x004e }
            java.lang.String r3 = "tb_incompatible_app_list"
            r7 = 0
            r8 = 0
            r9 = 0
            r2 = r1
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x004e }
            java.lang.String r11 = "AppCompatManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004e }
            r2.<init>()     // Catch:{ Exception -> 0x004e }
            java.lang.String r3 = "queryFromDb count ="
            r2.append(r3)     // Catch:{ Exception -> 0x004e }
            int r3 = r0.getCount()     // Catch:{ Exception -> 0x004e }
            r2.append(r3)     // Catch:{ Exception -> 0x004e }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x004e }
            android.util.Log.d(r11, r2)     // Catch:{ Exception -> 0x004e }
            int r11 = r0.getCount()     // Catch:{ Exception -> 0x004e }
            if (r0 == 0) goto L_0x0049
            r0.close()     // Catch:{ all -> 0x006d }
        L_0x0049:
            miui.util.IOUtils.closeQuietly(r1)     // Catch:{ all -> 0x006d }
            monitor-exit(r10)
            return r11
        L_0x004e:
            r11 = move-exception
            goto L_0x0055
        L_0x0050:
            r11 = move-exception
            r1 = r0
            goto L_0x0064
        L_0x0053:
            r11 = move-exception
            r1 = r0
        L_0x0055:
            r11.printStackTrace()     // Catch:{ all -> 0x0063 }
            if (r0 == 0) goto L_0x005d
            r0.close()     // Catch:{ all -> 0x006d }
        L_0x005d:
            miui.util.IOUtils.closeQuietly(r1)     // Catch:{ all -> 0x006d }
            r11 = -1
            monitor-exit(r10)
            return r11
        L_0x0063:
            r11 = move-exception
        L_0x0064:
            if (r0 == 0) goto L_0x0069
            r0.close()     // Catch:{ all -> 0x006d }
        L_0x0069:
            miui.util.IOUtils.closeQuietly(r1)     // Catch:{ all -> 0x006d }
            throw r11     // Catch:{ all -> 0x006d }
        L_0x006d:
            r11 = move-exception
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appcompatibility.d.c(java.lang.String):int");
    }

    private static List<String> c(Context context) {
        ArrayList arrayList = new ArrayList();
        try {
            return (List) e.a(Class.forName("miui.security.appcompatibility.AppCompatibilityManager"), List.class, "getIncompatibleAppList", (Class<?>[]) new Class[]{Context.class}, context);
        } catch (Exception e2) {
            Log.e("AppCompatManager", "getIncompatibleAppList exception: ", e2);
            return arrayList;
        }
    }

    private static List<String> c(Context context, List<String> list) {
        try {
            e.a(Class.forName("miui.security.appcompatibility.AppCompatibilityManager"), List.class, "setIncompatibleAppList", (Class<?>[]) new Class[]{Context.class, List.class}, context, list);
            return null;
        } catch (Exception e2) {
            Log.e("AppCompatManager", "setIncompatibleAppList exception: ", e2);
            return null;
        }
    }

    private synchronized long d(String str) {
        SQLiteDatabase sQLiteDatabase = null;
        try {
            SQLiteDatabase sQLiteDatabase2 = this.f3077b.getWritableDatabase();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("os_ver", str);
                long insert = sQLiteDatabase2.insert("tb_os_ver", (String) null, contentValues);
                IOUtils.closeQuietly(sQLiteDatabase2);
                return insert;
            } catch (Exception e2) {
                e = e2;
                sQLiteDatabase = sQLiteDatabase2;
                try {
                    e.printStackTrace();
                    IOUtils.closeQuietly(sQLiteDatabase);
                    return -1;
                } catch (Throwable th) {
                    th = th;
                    sQLiteDatabase2 = sQLiteDatabase;
                    IOUtils.closeQuietly(sQLiteDatabase2);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeQuietly(sQLiteDatabase2);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            IOUtils.closeQuietly(sQLiteDatabase);
            return -1;
        }
    }

    /* access modifiers changed from: private */
    public List<PackageData> d(Context context) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        ArrayList arrayList = new ArrayList();
        for (PackageInfo next : installedPackages) {
            if ((next.applicationInfo.flags & 1) == 0) {
                String str = next.packageName;
                if (!str.contains("xiaomi") && !str.contains("miui")) {
                    PackageData packageData = new PackageData();
                    packageData.setPkg(str);
                    packageData.setVer(String.valueOf(next.versionCode));
                    arrayList.add(packageData);
                }
            }
        }
        return arrayList;
    }

    private void d() {
        List<PackageData> d2 = d(this.f3078c);
        for (PackageData next : d2) {
            if (c(next.getPkg()) > 0) {
                a(next.getPkg());
                a(this.f3078c, next.getPkg());
            }
        }
        for (PackageData next2 : e()) {
            boolean z = false;
            for (PackageData pkg : d2) {
                if (next2.getPkg().equals(pkg.getPkg())) {
                    z = true;
                }
            }
            if (!z) {
                a(next2.getPkg());
                a(this.f3078c, next2.getPkg());
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: android.database.sqlite.SQLiteDatabase} */
    /* JADX WARNING: type inference failed for: r2v1, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r2v2 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x007e A[SYNTHETIC, Splitter:B:26:0x007e] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0089 A[SYNTHETIC, Splitter:B:33:0x0089] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized java.util.List<com.miui.appcompatibility.data.PackageData> e() {
        /*
            r11 = this;
            monitor-enter(r11)
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0090 }
            r0.<init>()     // Catch:{ all -> 0x0090 }
            r1 = 0
            com.miui.securityscan.d.a r2 = r11.f3077b     // Catch:{ Exception -> 0x0077, all -> 0x0074 }
            android.database.sqlite.SQLiteDatabase r2 = r2.getWritableDatabase()     // Catch:{ Exception -> 0x0077, all -> 0x0074 }
            java.lang.String r3 = "_id"
            java.lang.String r4 = "pkg_name"
            java.lang.String r5 = "pkg_ver"
            java.lang.String r6 = "pkg_status"
            java.lang.String[] r5 = new java.lang.String[]{r3, r4, r5, r6}     // Catch:{ Exception -> 0x0072 }
            java.lang.String r4 = "tb_incompatible_app_list"
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r3 = r2
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = "AppCompatManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0072 }
            r4.<init>()     // Catch:{ Exception -> 0x0072 }
            java.lang.String r5 = "queryFromDb count ="
            r4.append(r5)     // Catch:{ Exception -> 0x0072 }
            int r5 = r1.getCount()     // Catch:{ Exception -> 0x0072 }
            r4.append(r5)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0072 }
            android.util.Log.d(r3, r4)     // Catch:{ Exception -> 0x0072 }
        L_0x003f:
            boolean r3 = r1.moveToNext()     // Catch:{ Exception -> 0x0072 }
            if (r3 == 0) goto L_0x0068
            java.lang.String r3 = "pkg_name"
            int r3 = r1.getColumnIndex(r3)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = r1.getString(r3)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r4 = "pkg_ver"
            int r4 = r1.getColumnIndex(r4)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r4 = r1.getString(r4)     // Catch:{ Exception -> 0x0072 }
            com.miui.appcompatibility.data.PackageData r5 = new com.miui.appcompatibility.data.PackageData     // Catch:{ Exception -> 0x0072 }
            r5.<init>()     // Catch:{ Exception -> 0x0072 }
            r5.setPkg(r3)     // Catch:{ Exception -> 0x0072 }
            r5.setVer(r4)     // Catch:{ Exception -> 0x0072 }
            r0.add(r5)     // Catch:{ Exception -> 0x0072 }
            goto L_0x003f
        L_0x0068:
            if (r1 == 0) goto L_0x006d
            r1.close()     // Catch:{ all -> 0x0090 }
        L_0x006d:
            miui.util.IOUtils.closeQuietly(r2)     // Catch:{ all -> 0x0090 }
            monitor-exit(r11)
            return r0
        L_0x0072:
            r3 = move-exception
            goto L_0x0079
        L_0x0074:
            r0 = move-exception
            r2 = r1
            goto L_0x0087
        L_0x0077:
            r3 = move-exception
            r2 = r1
        L_0x0079:
            r3.printStackTrace()     // Catch:{ all -> 0x0086 }
            if (r1 == 0) goto L_0x0081
            r1.close()     // Catch:{ all -> 0x0090 }
        L_0x0081:
            miui.util.IOUtils.closeQuietly(r2)     // Catch:{ all -> 0x0090 }
            monitor-exit(r11)
            return r0
        L_0x0086:
            r0 = move-exception
        L_0x0087:
            if (r1 == 0) goto L_0x008c
            r1.close()     // Catch:{ all -> 0x0090 }
        L_0x008c:
            miui.util.IOUtils.closeQuietly(r2)     // Catch:{ all -> 0x0090 }
            throw r0     // Catch:{ all -> 0x0090 }
        L_0x0090:
            r0 = move-exception
            monitor-exit(r11)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appcompatibility.d.e():java.util.List");
    }

    private static void e(Context context) {
        context.sendBroadcast(new Intent("com.miui.action.appcompatibility.update"));
    }

    private void f() {
        String valueOf = String.valueOf(Build.VERSION.SDK_INT);
        Log.d("AppCompatManager", "init() os_ver=" + valueOf);
        if (!b(valueOf)) {
            i();
            d(valueOf);
        }
        d();
    }

    private void g() {
        LocalBroadcastManager.getInstance(this.f3078c).registerReceiver(new com.miui.appcompatibility.a.c(), new IntentFilter("com.miui.appcompatibility.receiver.AppCompatStateReceiver"));
    }

    private void h() {
        this.f3079d = new com.miui.appcompatibility.a.d();
        LocalBroadcastManager.getInstance(this.f3078c).registerReceiver(this.f3079d, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:10:0x0019=Splitter:B:10:0x0019, B:22:0x002f=Splitter:B:22:0x002f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void i() {
        /*
            r4 = this;
            monitor-enter(r4)
            java.lang.String r0 = "AppCompatManager"
            java.lang.String r1 = " resetDB="
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x0033 }
            r0 = 0
            com.miui.securityscan.d.a r1 = r4.f3077b     // Catch:{ Exception -> 0x0024, all -> 0x001f }
            android.database.sqlite.SQLiteDatabase r1 = r1.getWritableDatabase()     // Catch:{ Exception -> 0x0024, all -> 0x001f }
            java.lang.String r2 = "tb_os_ver"
            r1.delete(r2, r0, r0)     // Catch:{ Exception -> 0x001d }
            java.lang.String r2 = "tb_incompatible_app_list"
            r1.delete(r2, r0, r0)     // Catch:{ Exception -> 0x001d }
        L_0x0019:
            miui.util.IOUtils.closeQuietly(r1)     // Catch:{ all -> 0x0033 }
            goto L_0x002c
        L_0x001d:
            r0 = move-exception
            goto L_0x0028
        L_0x001f:
            r1 = move-exception
            r3 = r1
            r1 = r0
            r0 = r3
            goto L_0x002f
        L_0x0024:
            r1 = move-exception
            r3 = r1
            r1 = r0
            r0 = r3
        L_0x0028:
            r0.printStackTrace()     // Catch:{ all -> 0x002e }
            goto L_0x0019
        L_0x002c:
            monitor-exit(r4)
            return
        L_0x002e:
            r0 = move-exception
        L_0x002f:
            miui.util.IOUtils.closeQuietly(r1)     // Catch:{ all -> 0x0033 }
            throw r0     // Catch:{ all -> 0x0033 }
        L_0x0033:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appcompatibility.d.i():void");
    }

    /* access modifiers changed from: private */
    public void j() {
        if (this.f3079d != null) {
            LocalBroadcastManager.getInstance(this.f3078c).unregisterReceiver(this.f3079d);
            this.f3079d = null;
        }
    }

    public void a() {
        Log.d("AppCompatManager", "AppCompatManager-init()");
        f();
        g();
    }

    public void a(String str, String str2, int i) {
        if (i == 0) {
            a(this.f3078c, str);
            a(str);
        } else {
            PackageData packageData = new PackageData();
            packageData.setPkg(str);
            packageData.setVer(str2);
            packageData.setStatus(i);
            b(this.f3078c, str);
            if (c(str) > 0) {
                b(packageData);
            } else {
                a(packageData);
            }
        }
        e(this.f3078c);
    }

    public void a(List<PackageData> list) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList<PackageData> arrayList3 = new ArrayList<>();
        ArrayList<PackageData> arrayList4 = new ArrayList<>();
        for (PackageData next : list) {
            if (next.getStatus() != 0) {
                arrayList.add(next.getPkg());
                arrayList3.add(next);
            } else {
                arrayList2.add(next.getPkg());
                arrayList4.add(next);
            }
        }
        if (arrayList.size() > 0) {
            a(this.f3078c, (List<String>) arrayList);
            e(this.f3078c);
        }
        if (arrayList2.size() > 0) {
            b(this.f3078c, (List<String>) arrayList2);
            e(this.f3078c);
        }
        for (PackageData packageData : arrayList3) {
            if (!(packageData == null || packageData.getPkg() == null || c(packageData.getPkg()) > 0)) {
                a(packageData);
            }
        }
        for (PackageData pkg : arrayList4) {
            a(pkg.getPkg());
        }
    }

    public void b() {
        if (Build.VERSION.SDK_INT >= 23 && n.a(this.f3078c) && !j.a() && n.a()) {
            this.e++;
            Log.d("AppCompatManager", "initData()");
            new b(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public void c() {
        h();
    }
}
