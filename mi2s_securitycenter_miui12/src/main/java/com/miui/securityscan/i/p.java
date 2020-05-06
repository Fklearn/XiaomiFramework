package com.miui.securityscan.i;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.io.Closeable;
import miui.util.IOUtils;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private static final Uri f7739a = Uri.parse("content://com.miui.systemAdSolution.adSwitch/adSwitch/");

    public static synchronized void a(Context context, String str, boolean z) {
        synchronized (p.class) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("adPackage", str);
            contentValues.put("adSwitchOff", Boolean.valueOf(z));
            try {
                context.getContentResolver().update(f7739a, contentValues, (String) null, (String[]) null);
            } catch (Exception e) {
                Log.e("ScreenAdUtils", "update screen ad state error", e);
            }
        }
        return;
    }

    public static boolean a(Context context, String str) {
        boolean z = false;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(f7739a, (String[]) null, str, (String[]) null, (String) null);
            if (cursor != null) {
                z = cursor.getExtras().getBoolean("adSwitchOff", false);
            }
        } catch (Exception e) {
            Log.e("ScreenAdUtils", "query screen ad state error", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return z;
    }
}
