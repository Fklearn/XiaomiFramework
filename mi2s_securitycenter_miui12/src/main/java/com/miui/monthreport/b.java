package com.miui.monthreport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import b.b.c.j.d;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.securitycenter.Application;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import miui.util.IOUtils;

class b {

    /* renamed from: a  reason: collision with root package name */
    private static b f5617a;

    /* renamed from: b  reason: collision with root package name */
    private SQLiteDatabase f5618b;

    private class a extends SQLiteOpenHelper {
        public a(Context context) {
            super(context, "month_report.db", (SQLiteDatabase.CursorFactory) null, 2);
        }

        private void a(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE report_json (_id INTEGER PRIMARY KEY AUTOINCREMENT, eventId TEXT, eventTime INTEGER, pkgName TEXT, eventType INTEGER, version INTEGER, data TEXT, moduleName TEXT);");
        }

        private void b(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("ALTER TABLE report_json ADD COLUMN moduleName TEXT DEFAULT monthReport;");
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            a(sQLiteDatabase);
        }

        public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            sQLiteDatabase.execSQL("DROP TABLE report_json;");
            onCreate(sQLiteDatabase);
        }

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            if (i == 1) {
                b(sQLiteDatabase);
            }
        }
    }

    /* renamed from: com.miui.monthreport.b$b  reason: collision with other inner class name */
    public static class C0054b {

        /* renamed from: a  reason: collision with root package name */
        String f5620a;

        /* renamed from: b  reason: collision with root package name */
        int f5621b;

        /* renamed from: c  reason: collision with root package name */
        int f5622c;

        /* renamed from: d  reason: collision with root package name */
        long f5623d;
        String e;
        String f;
        String g;
    }

    private b(Context context) {
        this.f5618b = new a(context).getWritableDatabase();
    }

    public static synchronized b a() {
        b bVar;
        synchronized (b.class) {
            if (f5617a == null) {
                f5617a = new b(Application.d());
            }
            bVar = f5617a;
        }
        return bVar;
    }

    /* access modifiers changed from: private */
    public void b(List<Integer> list) {
        SQLiteDatabase sQLiteDatabase;
        if (!list.isEmpty()) {
            synchronized (b.class) {
                this.f5618b.beginTransaction();
                try {
                    for (Integer intValue : list) {
                        int intValue2 = intValue.intValue();
                        this.f5618b.delete("report_json", "_id=?", new String[]{String.valueOf(intValue2)});
                    }
                    this.f5618b.setTransactionSuccessful();
                    sQLiteDatabase = this.f5618b;
                } catch (Exception e) {
                    try {
                        e.printStackTrace();
                        sQLiteDatabase = this.f5618b;
                    } catch (Throwable th) {
                        this.f5618b.endTransaction();
                        throw th;
                    }
                }
                sQLiteDatabase.endTransaction();
            }
        }
    }

    private int c() {
        int delete;
        synchronized (b.class) {
            delete = this.f5618b.delete("report_json", "eventId is null or trim(eventId) = ''", (String[]) null);
        }
        return delete;
    }

    public int a(long j) {
        int i;
        synchronized (b.class) {
            try {
                i = this.f5618b.delete("report_json", "eventTime < ?", new String[]{String.valueOf(j)});
            } catch (Exception e) {
                e.printStackTrace();
                i = -1;
            }
        }
        return i;
    }

    public int a(String str) {
        Cursor cursor = null;
        try {
            cursor = this.f5618b.query("report_json", new String[]{"count(*)"}, "moduleName=?", new String[]{str}, (String) null, (String) null, (String) null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                int i = cursor.getInt(0);
                IOUtils.closeQuietly(cursor);
                return i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return -1;
    }

    public long a(ContentValues contentValues) {
        long j;
        synchronized (b.class) {
            try {
                j = this.f5618b.insert("report_json", (String) null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
                j = -1;
            }
        }
        return j;
    }

    public Exception a(List<String> list) {
        if (list.isEmpty()) {
            return null;
        }
        synchronized (b.class) {
            try {
                this.f5618b.beginTransaction();
                for (String str : list) {
                    this.f5618b.delete("report_json", "eventId=?", new String[]{str});
                }
                this.f5618b.setTransactionSuccessful();
                this.f5618b.endTransaction();
            } catch (Exception e) {
                this.f5618b.endTransaction();
                return e;
            } catch (Throwable th) {
                this.f5618b.endTransaction();
                throw th;
            }
        }
        return null;
    }

    public List<C0054b> a(String str, int i) {
        Cursor cursor;
        try {
            c();
            cursor = this.f5618b.query("report_json", (String[]) null, "moduleName=?", new String[]{str}, (String) null, (String) null, "eventTime asc", String.valueOf(i));
            if (cursor != null) {
                try {
                    if (cursor.getCount() > 0) {
                        ArrayList arrayList = new ArrayList(cursor.getCount());
                        ArrayList arrayList2 = new ArrayList();
                        while (cursor.moveToNext()) {
                            int i2 = cursor.getInt(0);
                            try {
                                C0054b bVar = new C0054b();
                                bVar.f5620a = cursor.getString(cursor.getColumnIndex("eventId"));
                                bVar.f5621b = cursor.getInt(cursor.getColumnIndex("eventType"));
                                bVar.f5622c = cursor.getInt(cursor.getColumnIndex("version"));
                                bVar.f5623d = cursor.getLong(cursor.getColumnIndex("eventTime"));
                                bVar.e = cursor.getString(cursor.getColumnIndex("pkgName"));
                                bVar.f = cursor.getString(cursor.getColumnIndex(DataSchemeDataSource.SCHEME_DATA));
                                bVar.g = cursor.getString(cursor.getColumnIndex("moduleName"));
                                arrayList.add(bVar);
                            } catch (Exception e) {
                                e.printStackTrace();
                                arrayList2.add(Integer.valueOf(i2));
                            }
                        }
                        if (!arrayList2.isEmpty()) {
                            d.a(new a(this, arrayList2));
                        }
                        IOUtils.closeQuietly(cursor);
                        return arrayList;
                    }
                } catch (Exception e2) {
                    e = e2;
                    try {
                        e.printStackTrace();
                        IOUtils.closeQuietly(cursor);
                        return null;
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(cursor);
                        throw th;
                    }
                }
            }
        } catch (Exception e3) {
            e = e3;
            cursor = null;
            e.printStackTrace();
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

    public List<String> b() {
        Throwable th;
        Cursor cursor;
        Cursor cursor2 = null;
        try {
            ArrayList arrayList = new ArrayList();
            cursor = this.f5618b.query(true, "report_json", new String[]{"moduleName"}, (String) null, (String[]) null, (String) null, (String) null, (String) null, (String) null);
            if (cursor != null) {
                try {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            try {
                                arrayList.add(cursor.getString(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        IOUtils.closeQuietly(cursor);
                        return arrayList;
                    }
                } catch (Exception e2) {
                    e = e2;
                }
            }
        } catch (Exception e3) {
            e = e3;
            cursor = null;
            try {
                e.printStackTrace();
                IOUtils.closeQuietly(cursor);
                return null;
            } catch (Throwable th2) {
                th = th2;
                cursor2 = cursor;
                IOUtils.closeQuietly(cursor2);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            IOUtils.closeQuietly(cursor2);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return null;
    }
}
