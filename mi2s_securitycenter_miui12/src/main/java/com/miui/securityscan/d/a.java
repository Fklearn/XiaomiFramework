package com.miui.securityscan.d;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class a extends SQLiteOpenHelper {
    public a(Context context) {
        super(context, "cloud_no_kill_pkg.db", (SQLiteDatabase.CursorFactory) null, 2);
    }

    private void a(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table if not exists " + "tb_incompatible_app_list" + " (" + "_id integer primary key autoincrement, " + "pkg_name TEXT, " + "pkg_ver TEXT, " + "pkg_status integer" + ");");
    }

    private void b(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table if not exists " + "tb_os_ver" + " (" + "_id integer primary key autoincrement, " + "os_ver TEXT" + ");");
    }

    private void c(SQLiteDatabase sQLiteDatabase) {
        b(sQLiteDatabase);
        a(sQLiteDatabase);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table " + "no_kill_pkg" + " (" + "_id integer primary key autoincrement, " + "pkg_name TEXT" + ");");
        c(sQLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE no_kill_pkg;");
        onCreate(sQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i == 1) {
            c(sQLiteDatabase);
        }
    }
}
