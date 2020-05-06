package com.miui.common.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class a extends SQLiteOpenHelper {

    /* renamed from: a  reason: collision with root package name */
    private static a f3833a;

    /* renamed from: b  reason: collision with root package name */
    private SQLiteDatabase f3834b = getReadableDatabase();

    private a(Context context) {
        super(context, "manual_list", (SQLiteDatabase.CursorFactory) null, 1);
    }

    public static synchronized a a(Context context) {
        a aVar;
        synchronized (a.class) {
            if (f3833a == null) {
                f3833a = new a(context.getApplicationContext());
            }
            aVar = f3833a;
        }
        return aVar;
    }

    private void a(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table " + "white_list" + " (" + "_id integer primary key autoincrement, " + "item TEXT, " + "title TEXT, " + "summary TEXT, " + "weight INTEGER" + ");");
    }

    /* access modifiers changed from: protected */
    public int a(String str, String[] strArr) {
        int delete;
        synchronized (a.class) {
            delete = this.f3834b.delete("white_list", str, strArr);
        }
        return delete;
    }

    /* access modifiers changed from: protected */
    public long a(ContentValues contentValues) {
        long insert;
        synchronized (a.class) {
            insert = this.f3834b.insert("white_list", (String) null, contentValues);
        }
        return insert;
    }

    /* access modifiers changed from: protected */
    public Cursor a(String[] strArr, String str, String[] strArr2, String str2) {
        return this.f3834b.query("white_list", strArr, str, strArr2, (String) null, (String) null, str2);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        a(sQLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE white_list;");
        onCreate(sQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }
}
