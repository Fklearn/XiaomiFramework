package com.miui.powercenter.batteryhistory;

import android.database.sqlite.SQLiteDatabase;

/* renamed from: com.miui.powercenter.batteryhistory.k  reason: case insensitive filesystem */
public class C0507k {

    /* renamed from: a  reason: collision with root package name */
    public static final String[] f6899a = {"id", "type", "start_time", "end_time", "shutdown_duration"};

    public static void a(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE histogram(id INTEGER PRIMARY KEY AUTOINCREMENT,type INTEGER NOT NULL,start_time INTEGER,end_time INTEGER,shutdown_duration INTEGER,histogram_data TEXT,battery_data TEXT);");
        sQLiteDatabase.execSQL("CREATE INDEX histogram_time_index ON histogram(start_time ASC)");
    }

    public static void b(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE history;");
        sQLiteDatabase.execSQL("DROP INDEX histogram_time_index;");
    }
}
