package com.miui.gamebooster.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.miui.gamebooster.c.a;
import java.io.Closeable;
import miui.util.IOUtils;

public class d {

    /* renamed from: a  reason: collision with root package name */
    public static final Uri f4747a = Uri.parse("content://com.miui.gamebooster/gamebooster");

    /* renamed from: b  reason: collision with root package name */
    public static final Uri f4748b = Uri.parse("content://com.miui.gamebooster/getshared_prefs");

    /* renamed from: c  reason: collision with root package name */
    public static final Uri f4749c = Uri.parse("content://com.miui.gamebooster/del_icon");

    public static void a(Context context) {
        Cursor cursor = null;
        try {
            cursor = d(context);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String string = cursor.getString(cursor.getColumnIndex("package_name"));
                String string2 = cursor.getString(cursor.getColumnIndex("app_name"));
                String string3 = cursor.getString(cursor.getColumnIndex("pop_game"));
                int i = cursor.getInt(cursor.getColumnIndex("package_uid"));
                int i2 = cursor.getInt(cursor.getColumnIndex("flag_white"));
                if (!TextUtils.isEmpty(string)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("app_name", string2);
                    contentValues.put("package_name", string);
                    contentValues.put("package_uid", Integer.valueOf(i));
                    contentValues.put("pop_game", string3);
                    contentValues.put("flag_white", Integer.valueOf(i2));
                    context.getContentResolver().insert(b.f4744a, contentValues);
                }
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.i("GameBoosterRestoreDBHelp", e.toString());
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
    }

    public static void b(Context context) {
        Cursor cursor = null;
        try {
            cursor = e(context);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                a.a(context);
                Boolean valueOf = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("pref_wlan_change_protection"))));
                Boolean valueOf2 = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("pref_first_open_game_booster"))));
                Boolean valueOf3 = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("pref_open_game_booster"))));
                Boolean valueOf4 = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("pref_game_net_priority_state"))));
                Boolean valueOf5 = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("pref_app_self_start_state"))));
                Boolean valueOf6 = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("pref_anti_keyboard"))));
                Boolean valueOf7 = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("pref_anti_disturb_msg_mode"))));
                a.da(valueOf.booleanValue());
                a.H(valueOf2.booleanValue());
                a.L(valueOf3.booleanValue());
                a.V(valueOf4.booleanValue());
                a.C(valueOf5.booleanValue());
                a.B(valueOf6.booleanValue());
                a.z(valueOf7.booleanValue());
            }
        } catch (Exception e) {
            Log.i("GameBoosterRestoreDBHelp", e.toString());
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
    }

    public static void c(Context context) {
        try {
            context.getContentResolver().delete(f4749c, (String) null, (String[]) null);
        } catch (IllegalArgumentException e) {
            Log.i("GameBoosterRestoreDBHelp", e.toString());
        }
    }

    public static Cursor d(Context context) {
        return context.getContentResolver().query(f4747a, (String[]) null, (String) null, (String[]) null, (String) null);
    }

    public static Cursor e(Context context) {
        return context.getContentResolver().query(f4748b, (String[]) null, (String) null, (String[]) null, (String) null);
    }
}
