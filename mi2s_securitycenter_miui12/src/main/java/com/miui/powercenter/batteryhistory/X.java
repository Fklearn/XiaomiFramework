package com.miui.powercenter.batteryhistory;

import android.database.sqlite.SQLiteDatabase;

public class X {
    public static void a(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE shutdown(id INTEGER PRIMARY KEY AUTOINCREMENT,shutdown_time INTEGER NOT NULL,shutdown_duration INTEGER NOT NULL);");
        sQLiteDatabase.execSQL("CREATE INDEX shutdown_time_index ON shutdown(shutdown_time ASC)");
    }

    public static void b(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE shutdown;");
        sQLiteDatabase.execSQL("DROP INDEX shutdown_time_index;");
    }
}
