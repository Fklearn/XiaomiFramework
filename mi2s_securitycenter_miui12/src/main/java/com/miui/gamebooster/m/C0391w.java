package com.miui.gamebooster.m;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import b.b.c.j.B;
import com.miui.gamebooster.provider.b;
import java.io.Closeable;
import miui.util.IOUtils;
import miui.util.Log;

/* renamed from: com.miui.gamebooster.m.w  reason: case insensitive filesystem */
public class C0391w {
    public static Cursor a(Context context, int i) {
        return context.getContentResolver().query(b.f4744a, (String[]) null, "app_name IS NOT NULL AND package_name IS NOT NULL AND package_uid IS NOT NULL AND pop_game IS NULL AND flag_white=?", new String[]{String.valueOf(i)}, (String) null);
    }

    public static Cursor a(Context context, String str) {
        return context.getContentResolver().query(b.f4744a, (String[]) null, "pop_game=? AND flag_white=?", new String[]{str, String.valueOf(0)}, (String) null);
    }

    public static Cursor a(Context context, String str, int i, int i2) {
        try {
            return context.getContentResolver().query(b.f4744a, (String[]) null, "package_name=? AND flag_white=? AND package_uid=?", new String[]{str, String.valueOf(i), String.valueOf(B.c(i2))}, (String) null);
        } catch (Exception e) {
            Log.e("GameBoosterTableHelper", "queryAdvanceSettingsValue", e);
            return null;
        }
    }

    public static void a(Context context) {
        context.getContentResolver().delete(b.f4744a, "pop_game IS NULL  AND flag_white=?", new String[]{String.valueOf(0)});
    }

    public static void a(Context context, String str, int i) {
        int c2 = B.c(i);
        if (str != null && str.length() > 0) {
            String[] strArr = {str, String.valueOf(0), String.valueOf(c2)};
            ContentValues contentValues = new ContentValues();
            contentValues.put("settings_gs", -1);
            contentValues.put("settings_ts", -1);
            contentValues.put("settings_edge", -1);
            contentValues.put("settings_hdr", -1);
            contentValues.put("settings_4d", 0);
            try {
                context.getContentResolver().update(b.f4744a, contentValues, "package_name=? AND flag_white=? AND package_uid=?", strArr);
            } catch (Exception e) {
                Log.e("GameBoosterTableHelper", "updateAdvanceSettingsValue", e);
            }
        }
    }

    public static void a(Context context, String str, int i, String str2, int i2) {
        int c2 = B.c(i);
        if (str != null && str.length() > 0) {
            String[] strArr = {str, String.valueOf(0), String.valueOf(c2)};
            ContentValues contentValues = new ContentValues();
            contentValues.put(str2, Integer.valueOf(i2));
            try {
                context.getContentResolver().update(b.f4744a, contentValues, "package_name=? AND flag_white=? AND package_uid=?", strArr);
            } catch (Exception e) {
                Log.e("GameBoosterTableHelper", "updateAdvanceSettingsValue", e);
            }
        }
    }

    public static void a(Context context, String str, int i, boolean z, int i2) {
        if (!z) {
            i = B.c(i);
        }
        context.getContentResolver().delete(b.f4745b, "package_name=? AND package_uid=? AND flag_white=?", new String[]{str, String.valueOf(i), String.valueOf(i2)});
    }

    public static void a(Context context, String str, String str2, int i, int i2) {
        int c2 = B.c(i);
        Cursor cursor = null;
        try {
            cursor = b(context, str2, c2, i2);
            if (cursor != null && cursor.moveToFirst()) {
                IOUtils.closeQuietly(cursor);
                return;
            }
        } catch (Exception e) {
            Log.e("GameBoosterTableHelper", e.toString());
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        if (str != null && str.length() > 0 && str2 != null && str2.length() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_name", str);
            contentValues.put("package_name", str2);
            contentValues.put("package_uid", Integer.valueOf(c2));
            contentValues.put("flag_white", Integer.valueOf(i2));
            contentValues.put("settings_gs", -1);
            contentValues.put("settings_ts", -1);
            contentValues.put("settings_edge", -1);
            contentValues.put("settings_hdr", -1);
            contentValues.put("settings_4d", 0);
            try {
                context.getContentResolver().insert(b.f4744a, contentValues);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            android.util.Log.i("GameBoosterTableHelper", b.f4744a.toString());
        }
    }

    public static void a(Context context, String str, String str2, int i, int i2, int i3) {
        int c2 = B.c(i);
        if (str != null && str.length() > 0 && str2 != null && str2.length() > 0) {
            String[] strArr = {str, str2, String.valueOf(i2), String.valueOf(c2)};
            ContentValues contentValues = new ContentValues();
            contentValues.put("sort_index", Integer.valueOf(i3));
            try {
                context.getContentResolver().update(b.f4744a, contentValues, "app_name=? AND package_name=? AND flag_white=? AND package_uid=?", strArr);
            } catch (Exception e) {
                Log.i("GameBoosterTableHelper", e.toString());
            }
        }
    }

    public static Cursor b(Context context, String str, int i, int i2) {
        return context.getContentResolver().query(b.f4744a, (String[]) null, "package_name=? AND package_uid=? AND flag_white=?", new String[]{str, String.valueOf(i), String.valueOf(i2)}, (String) null);
    }

    public static boolean b(Context context) {
        Cursor cursor = null;
        try {
            boolean z = true;
            cursor = context.getContentResolver().query(b.f4744a, (String[]) null, "app_name IS NOT NULL AND package_name IS NOT NULL AND package_uid IS NOT NULL AND pop_game IS NULL AND flag_white=?", new String[]{String.valueOf(0)}, (String) null);
            if (cursor == null) {
                return false;
            }
            if (cursor.getCount() <= 0) {
                z = false;
            }
            IOUtils.closeQuietly(cursor);
            return z;
        } catch (Exception e) {
            Log.e("GameBoosterTableHelper", e.toString());
            return false;
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }
}
