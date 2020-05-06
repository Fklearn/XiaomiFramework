package com.xiaomi.stat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.FileObserver;
import com.xiaomi.stat.d.k;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ab {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8399a = "MiStatPref";

    /* renamed from: b  reason: collision with root package name */
    private static final String f8400b = "true";

    /* renamed from: c  reason: collision with root package name */
    private static final String f8401c = "false";
    private static ab e;

    /* renamed from: d  reason: collision with root package name */
    private FileObserver f8402d;
    private Map<String, String> f = new HashMap();
    /* access modifiers changed from: private */
    public SQLiteOpenHelper g;

    private static class a extends SQLiteOpenHelper {

        /* renamed from: a  reason: collision with root package name */
        public static final String f8403a = "mistat_pf";

        /* renamed from: b  reason: collision with root package name */
        public static final String f8404b = "pref";

        /* renamed from: c  reason: collision with root package name */
        public static final String f8405c = "pref_key";

        /* renamed from: d  reason: collision with root package name */
        public static final String f8406d = "pref_value";
        private static final int e = 1;
        private static final String f = "_id";
        private static final String g = "CREATE TABLE pref (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,pref_key TEXT,pref_value TEXT)";

        public a(Context context) {
            super(context, f8403a, (SQLiteDatabase.CursorFactory) null, 1);
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(g);
        }

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    private ab() {
        Context a2 = ak.a();
        this.g = new a(a2);
        b();
        c(a2.getDatabasePath(a.f8403a).getAbsolutePath());
    }

    public static ab a() {
        if (e == null) {
            synchronized (ab.class) {
                if (e == null) {
                    e = new ab();
                }
            }
        }
        return e;
    }

    /* access modifiers changed from: private */
    public void b() {
        FutureTask futureTask = new FutureTask(new ac(this));
        try {
            c.a(futureTask);
            Cursor cursor = null;
            try {
                cursor = (Cursor) futureTask.get(2, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException unused) {
            }
            if (cursor != null) {
                this.f.clear();
                try {
                    k.c(f8399a, "load pref from db");
                    int columnIndex = cursor.getColumnIndex(a.f8405c);
                    int columnIndex2 = cursor.getColumnIndex(a.f8406d);
                    while (cursor.moveToNext()) {
                        String string = cursor.getString(columnIndex);
                        String string2 = cursor.getString(columnIndex2);
                        this.f.put(string, string2);
                        k.c(f8399a, "key=" + string + " ,value=" + string2);
                    }
                } catch (Exception unused2) {
                } catch (Throwable th) {
                    cursor.close();
                    throw th;
                }
                cursor.close();
            }
        } catch (RejectedExecutionException e2) {
            k.c(f8399a, "load data execute failed with " + e2);
        }
    }

    private void c(String str) {
        this.f8402d = new ad(this, str);
        this.f8402d.startWatching();
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(8:14|15|16|17|18|19|20|21) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0050 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c(java.lang.String r5, java.lang.String r6) {
        /*
            r4 = this;
            monitor-enter(r4)
            r0 = 1
            boolean r1 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x006b }
            if (r1 == 0) goto L_0x0018
            java.util.Map<java.lang.String, java.lang.String> r1 = r4.f     // Catch:{ all -> 0x006b }
            boolean r1 = r1.containsKey(r5)     // Catch:{ all -> 0x006b }
            if (r1 == 0) goto L_0x0016
            java.util.Map<java.lang.String, java.lang.String> r1 = r4.f     // Catch:{ all -> 0x006b }
            r1.remove(r5)     // Catch:{ all -> 0x006b }
            goto L_0x001d
        L_0x0016:
            r0 = 0
            goto L_0x001d
        L_0x0018:
            java.util.Map<java.lang.String, java.lang.String> r1 = r4.f     // Catch:{ all -> 0x006b }
            r1.put(r5, r6)     // Catch:{ all -> 0x006b }
        L_0x001d:
            java.lang.String r1 = "MiStatPref"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x006b }
            r2.<init>()     // Catch:{ all -> 0x006b }
            java.lang.String r3 = "put value: key="
            r2.append(r3)     // Catch:{ all -> 0x006b }
            r2.append(r5)     // Catch:{ all -> 0x006b }
            java.lang.String r3 = " ,value="
            r2.append(r3)     // Catch:{ all -> 0x006b }
            r2.append(r6)     // Catch:{ all -> 0x006b }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x006b }
            com.xiaomi.stat.d.k.c(r1, r2)     // Catch:{ all -> 0x006b }
            if (r0 != 0) goto L_0x003f
            monitor-exit(r4)     // Catch:{ all -> 0x006b }
            return
        L_0x003f:
            com.xiaomi.stat.ae r0 = new com.xiaomi.stat.ae     // Catch:{ all -> 0x006b }
            r0.<init>(r4, r6, r5)     // Catch:{ all -> 0x006b }
            java.util.concurrent.FutureTask r5 = new java.util.concurrent.FutureTask     // Catch:{ all -> 0x006b }
            r6 = 0
            r5.<init>(r0, r6)     // Catch:{ all -> 0x006b }
            com.xiaomi.stat.c.a(r5)     // Catch:{ RejectedExecutionException -> 0x0052 }
            r5.get()     // Catch:{ InterruptedException | ExecutionException -> 0x0050 }
        L_0x0050:
            monitor-exit(r4)     // Catch:{ all -> 0x006b }
            return
        L_0x0052:
            r5 = move-exception
            java.lang.String r6 = "MiStatPref"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x006b }
            r0.<init>()     // Catch:{ all -> 0x006b }
            java.lang.String r1 = "execute failed with "
            r0.append(r1)     // Catch:{ all -> 0x006b }
            r0.append(r5)     // Catch:{ all -> 0x006b }
            java.lang.String r5 = r0.toString()     // Catch:{ all -> 0x006b }
            com.xiaomi.stat.d.k.c(r6, r5)     // Catch:{ all -> 0x006b }
            monitor-exit(r4)     // Catch:{ all -> 0x006b }
            return
        L_0x006b:
            r5 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x006b }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.ab.c(java.lang.String, java.lang.String):void");
    }

    public float a(String str, float f2) {
        synchronized (this) {
            if (this.f.containsKey(str)) {
                try {
                    float floatValue = Float.valueOf(this.f.get(str)).floatValue();
                    return floatValue;
                } catch (NumberFormatException unused) {
                    return f2;
                }
            }
        }
    }

    public int a(String str, int i) {
        synchronized (this) {
            if (this.f.containsKey(str)) {
                try {
                    int intValue = Integer.valueOf(this.f.get(str)).intValue();
                    return intValue;
                } catch (NumberFormatException unused) {
                    return i;
                }
            }
        }
    }

    public long a(String str, long j) {
        synchronized (this) {
            if (this.f.containsKey(str)) {
                try {
                    long longValue = Long.valueOf(this.f.get(str)).longValue();
                    return longValue;
                } catch (NumberFormatException unused) {
                    return j;
                }
            }
        }
    }

    public String a(String str, String str2) {
        synchronized (this) {
            if (!this.f.containsKey(str)) {
                return str2;
            }
            String str3 = this.f.get(str);
            return str3;
        }
    }

    public boolean a(String str) {
        boolean containsKey;
        synchronized (this) {
            containsKey = this.f.containsKey(str);
        }
        return containsKey;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0028, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(java.lang.String r2, boolean r3) {
        /*
            r1 = this;
            monitor-enter(r1)
            java.util.Map<java.lang.String, java.lang.String> r0 = r1.f     // Catch:{ all -> 0x0029 }
            boolean r0 = r0.containsKey(r2)     // Catch:{ all -> 0x0029 }
            if (r0 == 0) goto L_0x0027
            java.util.Map<java.lang.String, java.lang.String> r0 = r1.f     // Catch:{ all -> 0x0029 }
            java.lang.Object r2 = r0.get(r2)     // Catch:{ all -> 0x0029 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x0029 }
            java.lang.String r0 = "true"
            boolean r0 = r0.equalsIgnoreCase(r2)     // Catch:{ all -> 0x0029 }
            if (r0 == 0) goto L_0x001c
            r2 = 1
            monitor-exit(r1)     // Catch:{ all -> 0x0029 }
            return r2
        L_0x001c:
            java.lang.String r0 = "false"
            boolean r2 = r0.equalsIgnoreCase(r2)     // Catch:{ all -> 0x0029 }
            if (r2 == 0) goto L_0x0027
            r2 = 0
            monitor-exit(r1)     // Catch:{ all -> 0x0029 }
            return r2
        L_0x0027:
            monitor-exit(r1)     // Catch:{ all -> 0x0029 }
            return r3
        L_0x0029:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0029 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.ab.a(java.lang.String, boolean):boolean");
    }

    public void b(String str) {
        b(str, (String) null);
    }

    public void b(String str, float f2) {
        c(str, Float.toString(f2));
    }

    public void b(String str, int i) {
        c(str, Integer.toString(i));
    }

    public void b(String str, long j) {
        c(str, Long.toString(j));
    }

    public void b(String str, String str2) {
        c(str, str2);
    }

    public void b(String str, boolean z) {
        c(str, Boolean.toString(z));
    }
}
