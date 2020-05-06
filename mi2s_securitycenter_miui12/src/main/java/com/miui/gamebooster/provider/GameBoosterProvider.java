package com.miui.gamebooster.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import b.b.o.g.e;
import com.miui.applicationlock.c.K;
import com.miui.common.persistence.b;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0385p;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.Z;
import com.miui.gamebooster.m.da;
import miui.os.Build;

public class GameBoosterProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4741a = "com.miui.gamebooster.provider.GameBoosterProvider";

    /* renamed from: b  reason: collision with root package name */
    private static final UriMatcher f4742b = new UriMatcher(-1);

    /* renamed from: c  reason: collision with root package name */
    private e f4743c;

    static {
        f4742b.addURI("com.miui.securitycenter.gamebooster", "gamebooster", 1);
        f4742b.addURI("com.miui.securitycenter.gamebooster", "gamebooster/*", 2);
        f4742b.addURI("com.miui.securitycenter.gamebooster", "gamebooster_entrace", 3);
        f4742b.addURI("com.miui.securitycenter.gamebooster", "gamebooster_analytics", 4);
        f4742b.addURI("com.miui.securitycenter.gamebooster", "gamebooster_xunyoubooster", 5);
        f4742b.addURI("com.miui.securitycenter.gamebooster", "gamebooster_getcpu", 7);
        f4742b.addURI("com.miui.securitycenter.gamebooster", "gamebooster_getgpu", 8);
        f4742b.addURI("com.miui.securitycenter.gamebooster", "quickreply_table", 9);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002f, code lost:
        if (r0 == null) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0032, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0023, code lost:
        if (r0 != null) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0025, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int a() {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            android.database.sqlite.SQLiteQueryBuilder r2 = new android.database.sqlite.SQLiteQueryBuilder     // Catch:{ Exception -> 0x002b }
            r2.<init>()     // Catch:{ Exception -> 0x002b }
            java.lang.String r3 = "gamebooster_table"
            r2.setTables(r3)     // Catch:{ Exception -> 0x002b }
            com.miui.gamebooster.provider.e r3 = r10.f4743c     // Catch:{ Exception -> 0x002b }
            android.database.sqlite.SQLiteDatabase r3 = r3.getReadableDatabase()     // Catch:{ Exception -> 0x002b }
            r4 = 0
            java.lang.String r5 = "app_name IS NOT NULL AND package_name IS NOT NULL AND pop_game IS NULL"
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x002b }
            if (r0 == 0) goto L_0x0023
            int r1 = r0.getCount()     // Catch:{ Exception -> 0x002b }
        L_0x0023:
            if (r0 == 0) goto L_0x0032
        L_0x0025:
            r0.close()
            goto L_0x0032
        L_0x0029:
            r1 = move-exception
            goto L_0x0033
        L_0x002b:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x0029 }
            if (r0 == 0) goto L_0x0032
            goto L_0x0025
        L_0x0032:
            return r1
        L_0x0033:
            if (r0 == 0) goto L_0x0038
            r0.close()
        L_0x0038:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.provider.GameBoosterProvider.a():int");
    }

    private Cursor a(Uri uri) {
        boolean z;
        try {
            z = ((Boolean) e.a(Class.forName("android.provider.MiuiSettings$Secure"), Boolean.TYPE, "getBoolean", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, getContext().getContentResolver(), "game_toggle_total_history_1", false)).booleanValue();
        } catch (Exception e) {
            Log.e("GameBoosterReflectUtils", e.toString());
            z = false;
        }
        a.a(getContext());
        int a2 = a();
        if (uri == null) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"game_toggle_total_history_1", "game_games_num_1"});
        matrixCursor.addRow(new Object[]{Boolean.valueOf(z), Integer.valueOf(a2)});
        return matrixCursor;
    }

    private Cursor b(Uri uri) {
        String a2 = da.a();
        if (uri == null || a2 == null) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"cpu_data"});
        matrixCursor.addRow(new Object[]{a2});
        return matrixCursor;
    }

    private Cursor c(Uri uri) {
        String valueOf = String.valueOf(C0385p.a());
        if (uri == null || valueOf == null) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"gpu_data"});
        matrixCursor.addRow(new Object[]{valueOf});
        return matrixCursor;
    }

    private Cursor d(Uri uri) {
        long a2 = b.a("gamebooster_xunyou_cache_time", -1);
        a.a(getContext());
        boolean z = a.y(false) && a.o(true) && K.c(getContext()) && a.a(getContext()).k(true) && a2 > System.currentTimeMillis();
        if (uri == null) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"xunyou_booster_status"});
        matrixCursor.addRow(new Object[]{Boolean.valueOf(z)});
        return matrixCursor;
    }

    private Cursor e(Uri uri) {
        MatrixCursor matrixCursor = null;
        boolean b2 = Z.b(getContext(), (String) null);
        if (uri != null) {
            matrixCursor = new MatrixCursor(new String[]{"is_has_game_booster", "is_has_games"});
            boolean z = Build.IS_INTERNATIONAL_BUILD;
            boolean d2 = C0393y.d(getContext());
            Object[] objArr = new Object[2];
            objArr[0] = Boolean.valueOf(!z && !b2);
            objArr[1] = Boolean.valueOf(d2);
            matrixCursor.addRow(objArr);
        }
        return matrixCursor;
    }

    private void f(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        int i;
        Log.d(f4741a, "delete");
        SQLiteDatabase writableDatabase = this.f4743c.getWritableDatabase();
        int match = f4742b.match(uri);
        if (match == 1 || match == 2) {
            i = writableDatabase.delete("gamebooster_table", str, strArr);
        } else if (match == 9) {
            i = writableDatabase.delete("quickreply_table", str, strArr);
        } else {
            throw new IllegalArgumentException("UnKnow URI:" + uri);
        }
        if (i > 0) {
            f(uri);
        }
        return i;
    }

    public String getType(Uri uri) {
        int match = f4742b.match(uri);
        if (match == 1) {
            return "vnd.android.cursor.dir/vnd.miui.gamebooster";
        }
        if (match == 2) {
            return "vnd.android.cursor.item/vnd.miui.gamebooster";
        }
        throw new IllegalArgumentException("UnKnow URI:" + uri);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0056, code lost:
        if (r0 > 0) goto L_0x0027;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0059, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0025, code lost:
        if (r0 > 0) goto L_0x0027;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.net.Uri insert(android.net.Uri r6, android.content.ContentValues r7) {
        /*
            r5 = this;
            java.lang.String r0 = f4741a
            java.lang.String r1 = "insert"
            android.util.Log.d(r0, r1)
            android.content.UriMatcher r0 = f4742b
            int r0 = r0.match(r6)
            r1 = 1
            r2 = 0
            r4 = 0
            if (r0 == r1) goto L_0x0048
            r1 = 9
            if (r0 != r1) goto L_0x0031
            com.miui.gamebooster.provider.e r0 = r5.f4743c
            android.database.sqlite.SQLiteDatabase r0 = r0.getWritableDatabase()
            java.lang.String r1 = "quickreply_table"
            long r0 = r0.insert(r1, r4, r7)
            int r7 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r7 <= 0) goto L_0x0059
        L_0x0027:
            r5.f(r6)
            android.net.Uri r6 = com.miui.gamebooster.provider.b.f4744a
            android.net.Uri r6 = android.content.ContentUris.withAppendedId(r6, r0)
            return r6
        L_0x0031:
            java.lang.IllegalArgumentException r7 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "UnKnow URI:"
            r0.append(r1)
            r0.append(r6)
            java.lang.String r6 = r0.toString()
            r7.<init>(r6)
            throw r7
        L_0x0048:
            com.miui.gamebooster.provider.e r0 = r5.f4743c
            android.database.sqlite.SQLiteDatabase r0 = r0.getWritableDatabase()
            java.lang.String r1 = "gamebooster_table"
            long r0 = r0.insert(r1, r4, r7)
            int r7 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r7 <= 0) goto L_0x0059
            goto L_0x0027
        L_0x0059:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.provider.GameBoosterProvider.insert(android.net.Uri, android.content.ContentValues):android.net.Uri");
    }

    public boolean onCreate() {
        this.f4743c = new e(getContext());
        return false;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Log.d(f4741a, "query");
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        switch (f4742b.match(uri)) {
            case 1:
                sQLiteQueryBuilder.setTables("gamebooster_table");
                break;
            case 2:
                sQLiteQueryBuilder.setTables("gamebooster_table");
                sQLiteQueryBuilder.appendWhere("_id=" + uri.getPathSegments().get(1));
                break;
            case 3:
                return e(uri);
            case 4:
                return a(uri);
            case 5:
                return d(uri);
            case 7:
                return b(uri);
            case 8:
                return c(uri);
            case 9:
                sQLiteQueryBuilder.setTables("quickreply_table");
                break;
            default:
                throw new IllegalArgumentException("UnKnow URI:" + uri);
        }
        if (f4742b.match(uri) != 9 && (str2 == null || str2.length() == 0)) {
            str2 = "sort_index ASC";
        }
        return sQLiteQueryBuilder.query(this.f4743c.getReadableDatabase(), strArr, str, strArr2, (String) null, (String) null, str2);
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int i;
        Log.d(f4741a, "update");
        SQLiteDatabase writableDatabase = this.f4743c.getWritableDatabase();
        int match = f4742b.match(uri);
        if (match == 1) {
            i = writableDatabase.update("gamebooster_table", contentValues, str, strArr);
        } else if (match == 2) {
            i = writableDatabase.update("gamebooster_table", contentValues, "_id = ?", new String[]{String.valueOf(ContentUris.parseId(uri))});
        } else {
            throw new IllegalArgumentException("UnKnow URI:" + uri);
        }
        if (i > 0) {
            f(uri);
        }
        return i;
    }
}
