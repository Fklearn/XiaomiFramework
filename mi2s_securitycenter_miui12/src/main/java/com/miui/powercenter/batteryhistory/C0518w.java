package com.miui.powercenter.batteryhistory;

import android.database.sqlite.SQLiteDatabase;

/* renamed from: com.miui.powercenter.batteryhistory.w  reason: case insensitive filesystem */
public class C0518w {

    /* renamed from: a  reason: collision with root package name */
    public static final String[] f6936a = {"id", "time"};

    public static void a(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE history(id INTEGER PRIMARY KEY AUTOINCREMENT,time INTEGER NOT NULL,utc_time INTEGER NOT NULL,utc_time_display TEXT NOT NULL,data TEXT);");
        sQLiteDatabase.execSQL("CREATE INDEX history_time_index ON history(time ASC)");
    }

    public static void b(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE history;");
        sQLiteDatabase.execSQL("DROP INDEX history_time_index;");
    }
}
