package com.miui.securityscan.d;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.miui.securitycenter.service.CloudDataUpdateService;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import miui.util.IOUtils;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static c f7689a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public a f7690b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public List<String> f7691c = new ArrayList();

    /* renamed from: d  reason: collision with root package name */
    private Context f7692d;

    private c(Context context) {
        this.f7690b = new a(context);
        this.f7692d = context;
    }

    public static c a(Context context) {
        if (f7689a == null) {
            f7689a = new c(context.getApplicationContext());
        }
        return f7689a;
    }

    /* JADX INFO: finally extract failed */
    public int a(String str) {
        try {
            SQLiteDatabase writableDatabase = this.f7690b.getWritableDatabase();
            int delete = writableDatabase.delete("no_kill_pkg", "pkg_name = ?", new String[]{str});
            IOUtils.closeQuietly(writableDatabase);
            return delete;
        } catch (Exception e) {
            e.printStackTrace();
            IOUtils.closeQuietly((Closeable) null);
            return -1;
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
    }

    public a a() {
        return this.f7690b;
    }

    public long b(String str) {
        SQLiteDatabase sQLiteDatabase = null;
        try {
            SQLiteDatabase sQLiteDatabase2 = this.f7690b.getWritableDatabase();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("pkg_name", str);
                long insert = sQLiteDatabase2.insert("no_kill_pkg", (String) null, contentValues);
                IOUtils.closeQuietly(sQLiteDatabase2);
                return insert;
            } catch (Exception e) {
                e = e;
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
        } catch (Exception e2) {
            e = e2;
            e.printStackTrace();
            IOUtils.closeQuietly(sQLiteDatabase);
            return -1;
        }
    }

    public List<String> b() {
        return this.f7691c;
    }

    public void c() {
        Calendar instance = Calendar.getInstance();
        instance.set(11, (int) (Math.random() * 23.0d));
        instance.set(12, (int) (Math.random() * 60.0d));
        Context context = this.f7692d;
        ((AlarmManager) this.f7692d.getSystemService("alarm")).setRepeating(1, instance.getTimeInMillis(), 86400000, PendingIntent.getService(context, 10005, new Intent(context, CloudDataUpdateService.class), 0));
    }

    public void d() {
        new b(this, new HashSet()).execute(new Void[0]);
    }
}
