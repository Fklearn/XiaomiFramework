package com.miui.securityscan.d;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import java.util.HashSet;
import miui.util.IOUtils;

class b extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ HashSet f7687a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f7688b;

    b(c cVar, HashSet hashSet) {
        this.f7688b = cVar;
        this.f7687a = hashSet;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        Cursor cursor;
        SQLiteDatabase sQLiteDatabase;
        try {
            sQLiteDatabase = this.f7688b.f7690b.getWritableDatabase();
            try {
                cursor = sQLiteDatabase.query("no_kill_pkg", new String[]{"pkg_name"}, (String) null, (String[]) null, (String) null, (String) null, (String) null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            do {
                                this.f7687a.add(cursor.getString(cursor.getColumnIndex("pkg_name")));
                            } while (cursor.moveToNext());
                            IOUtils.closeQuietly(sQLiteDatabase);
                            IOUtils.closeQuietly(cursor);
                            return null;
                        }
                    } catch (Exception e) {
                        e = e;
                        try {
                            e.printStackTrace();
                            IOUtils.closeQuietly(sQLiteDatabase);
                            IOUtils.closeQuietly(cursor);
                            return null;
                        } catch (Throwable th) {
                            th = th;
                            IOUtils.closeQuietly(sQLiteDatabase);
                            IOUtils.closeQuietly(cursor);
                            throw th;
                        }
                    }
                }
                IOUtils.closeQuietly(sQLiteDatabase);
                IOUtils.closeQuietly(cursor);
                return null;
            } catch (Exception e2) {
                e = e2;
                cursor = null;
                e.printStackTrace();
                IOUtils.closeQuietly(sQLiteDatabase);
                IOUtils.closeQuietly(cursor);
                return null;
            } catch (Throwable th2) {
                th = th2;
                cursor = null;
                IOUtils.closeQuietly(sQLiteDatabase);
                IOUtils.closeQuietly(cursor);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            sQLiteDatabase = null;
            cursor = null;
            e.printStackTrace();
            IOUtils.closeQuietly(sQLiteDatabase);
            IOUtils.closeQuietly(cursor);
            return null;
        } catch (Throwable th3) {
            th = th3;
            sQLiteDatabase = null;
            cursor = null;
            IOUtils.closeQuietly(sQLiteDatabase);
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        this.f7688b.f7691c.clear();
        this.f7688b.f7691c.addAll(this.f7687a);
    }
}
