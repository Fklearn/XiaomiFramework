package com.miui.gamebooster.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import java.io.Closeable;
import miui.util.IOUtils;
import miui.util.Log;

public class a {
    public static Cursor a(Context context, int i) {
        return context.getContentResolver().query(b.f4744a, (String[]) null, "app_name IS NOT NULL AND package_name IS NOT NULL AND package_uid IS NOT NULL AND pop_game IS NULL AND flag_white=?", new String[]{String.valueOf(i)}, (String) null);
    }

    public static void a(Context context) {
        context.getContentResolver().delete(b.f4744a, "pop_game IS NOT NULL", (String[]) null);
    }

    public static void a(Context context, String str) {
        context.getContentResolver().delete(c.f4746a, "package_name =?", new String[]{str});
    }

    public static synchronized void a(Context context, String str, int i) {
        synchronized (a.class) {
            try {
                Cursor d2 = d(context, str);
                if (d2 == null || d2.getCount() <= 0) {
                    IOUtils.closeQuietly(d2);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("package_name", str);
                    contentValues.put("package_uid", Integer.valueOf(i));
                    context.getContentResolver().insert(c.f4746a, contentValues);
                    return;
                }
                IOUtils.closeQuietly(d2);
            } catch (Throwable th) {
                IOUtils.closeQuietly((Closeable) null);
                throw th;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0082, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void a(android.content.Context r3, java.lang.String r4, java.lang.String r5, int r6, int r7, java.lang.Boolean r8) {
        /*
            java.lang.Class<com.miui.gamebooster.provider.a> r0 = com.miui.gamebooster.provider.a.class
            monitor-enter(r0)
            int r6 = b.b.c.j.B.c(r6)     // Catch:{ all -> 0x0083 }
            boolean r1 = a(r3, r5, r6, r7)     // Catch:{ all -> 0x0083 }
            if (r1 == 0) goto L_0x000f
            monitor-exit(r0)
            return
        L_0x000f:
            if (r4 == 0) goto L_0x0081
            int r1 = r4.length()     // Catch:{ all -> 0x0083 }
            if (r1 <= 0) goto L_0x0081
            if (r5 == 0) goto L_0x0081
            int r1 = r5.length()     // Catch:{ all -> 0x0083 }
            if (r1 <= 0) goto L_0x0081
            android.content.ContentValues r1 = new android.content.ContentValues     // Catch:{ all -> 0x0083 }
            r1.<init>()     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = "app_name"
            r1.put(r2, r4)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "package_name"
            r1.put(r4, r5)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "package_uid"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0083 }
            r1.put(r4, r6)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "flag_white"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x0083 }
            r1.put(r4, r6)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "settings_gs"
            r6 = -1
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0083 }
            r1.put(r4, r7)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "settings_ts"
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0083 }
            r1.put(r4, r7)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "settings_edge"
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0083 }
            r1.put(r4, r7)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "settings_hdr"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0083 }
            r1.put(r4, r6)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "settings_4d"
            r6 = 0
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0083 }
            r1.put(r4, r6)     // Catch:{ all -> 0x0083 }
            android.content.ContentResolver r4 = r3.getContentResolver()     // Catch:{ all -> 0x0083 }
            android.net.Uri r6 = com.miui.gamebooster.provider.b.f4744a     // Catch:{ all -> 0x0083 }
            r4.insert(r6, r1)     // Catch:{ all -> 0x0083 }
            boolean r4 = r8.booleanValue()     // Catch:{ all -> 0x0083 }
            if (r4 == 0) goto L_0x0081
            com.miui.gamebooster.service.GameBoosterService.a((java.lang.String) r5, (android.content.Context) r3)     // Catch:{ all -> 0x0083 }
        L_0x0081:
            monitor-exit(r0)
            return
        L_0x0083:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.provider.a.a(android.content.Context, java.lang.String, java.lang.String, int, int, java.lang.Boolean):void");
    }

    public static boolean a(Context context, String str, int i, int i2) {
        Cursor cursor = null;
        try {
            cursor = b(context, str, i, i2);
            if (cursor != null && cursor.moveToFirst()) {
                IOUtils.closeQuietly(cursor);
                return true;
            }
        } catch (Exception e) {
            Log.e("GameBoosterHelper", e.toString());
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return false;
    }

    public static Cursor b(Context context) {
        return context.getContentResolver().query(c.f4746a, new String[]{"package_name"}, "package_name IS NOT NULL ", (String[]) null, (String) null);
    }

    public static Cursor b(Context context, String str, int i, int i2) {
        return context.getContentResolver().query(b.f4744a, (String[]) null, "package_name=? AND package_uid=? AND flag_white=?", new String[]{str, String.valueOf(i), String.valueOf(i2)}, (String) null);
    }

    public static void b(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_name", "none");
            contentValues.put("package_name", "none");
            contentValues.put("package_uid", -1);
            contentValues.put("pop_game", str);
            contentValues.put("flag_white", 0);
            contentValues.put("settings_gs", -1);
            contentValues.put("settings_ts", -1);
            contentValues.put("settings_edge", -1);
            contentValues.put("settings_hdr", -1);
            contentValues.put("settings_4d", 0);
            context.getContentResolver().insert(b.f4744a, contentValues);
        }
    }

    public static Cursor c(Context context, String str) {
        return context.getContentResolver().query(b.f4744a, (String[]) null, "pop_game=?", new String[]{str}, (String) null);
    }

    private static Cursor d(Context context, String str) {
        return context.getContentResolver().query(c.f4746a, (String[]) null, "package_name =? ", new String[]{str}, (String) null);
    }
}
