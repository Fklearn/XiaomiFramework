package com.miui.securityscan.i;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.miui.securitycenter.Application;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.CloudPushConstants;
import miui.util.IOUtils;

public class m {

    /* renamed from: a  reason: collision with root package name */
    private static final Uri f7737a = Uri.parse("content://com.miui.securitycenter.remoteprovider/whitelist");

    public static int a() {
        return Application.d().getContentResolver().delete(f7737a, (String) null, (String[]) null);
    }

    public static Uri a(String str) {
        ContentResolver contentResolver = Application.d().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CloudPushConstants.XML_ITEM, str);
        return contentResolver.insert(f7737a, contentValues);
    }

    public static int b(String str) {
        return Application.d().getContentResolver().delete(f7737a, "item=?", new String[]{str});
    }

    public static List<String> b() {
        Cursor cursor;
        try {
            cursor = Application.d().getContentResolver().query(f7737a, new String[]{CloudPushConstants.XML_ITEM}, (String) null, (String[]) null, (String) null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        ArrayList arrayList = new ArrayList();
                        do {
                            arrayList.add(cursor.getString(cursor.getColumnIndex(CloudPushConstants.XML_ITEM)));
                        } while (cursor.moveToNext());
                        IOUtils.closeQuietly(cursor);
                        return arrayList;
                    }
                } catch (Exception e) {
                    e = e;
                    try {
                        Log.e("ProviderUtils", e.toString());
                        IOUtils.closeQuietly(cursor);
                        return null;
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(cursor);
                        throw th;
                    }
                }
            }
        } catch (Exception e2) {
            e = e2;
            cursor = null;
            Log.e("ProviderUtils", e.toString());
            IOUtils.closeQuietly(cursor);
            return null;
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return null;
    }

    public static boolean c(String str) {
        ContentResolver contentResolver = Application.d().getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(f7737a, (String[]) null, "item=?", new String[]{str}, (String) null);
            if (!(cursor == null || cursor.getCount() == 0)) {
                IOUtils.closeQuietly(cursor);
                return true;
            }
        } catch (Exception e) {
            Log.e("ProviderUtils", e.toString());
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return false;
    }
}
