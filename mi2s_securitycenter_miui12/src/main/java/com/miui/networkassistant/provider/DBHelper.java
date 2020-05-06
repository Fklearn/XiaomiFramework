package com.miui.networkassistant.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "traffic.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TAG = "NA_DBHelper";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 3);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i <= 3) {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS traffic_saving_white_list");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS traffic_saving_pending_compress_list");
        }
    }
}
