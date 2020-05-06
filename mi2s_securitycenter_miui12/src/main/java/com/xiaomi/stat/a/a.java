package com.xiaomi.stat.a;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class a extends SQLiteOpenHelper {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8360a = "mistat_ev";

    /* renamed from: b  reason: collision with root package name */
    private static final int f8361b = 1;

    /* renamed from: c  reason: collision with root package name */
    private static final String f8362c = "CREATE TABLE events (_id INTEGER PRIMARY KEY AUTOINCREMENT,e TEXT,eg TEXT,tp TEXT,ps TEXT,ts INTEGER,sub TEXT,is_am INTEGER,priority INTEGER)";

    public a(Context context) {
        super(context, "mistat_ev", (SQLiteDatabase.CursorFactory) null, 1);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(f8362c);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }
}
