package com.miui.powercenter.batteryhistory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class r extends SQLiteOpenHelper {
    public r(Context context) {
        super(context, "battery_history.db", (SQLiteDatabase.CursorFactory) null, 1);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        try {
            C0518w.a(sQLiteDatabase);
            C0507k.a(sQLiteDatabase);
            X.a(sQLiteDatabase);
        } catch (Exception e) {
            Log.e("BatteryHistory", "exception when create battery_history DB. ", e);
        }
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        C0518w.b(sQLiteDatabase);
        C0507k.b(sQLiteDatabase);
        X.a(sQLiteDatabase);
        onCreate(sQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        C0518w.b(sQLiteDatabase);
        C0507k.b(sQLiteDatabase);
        X.b(sQLiteDatabase);
        onCreate(sQLiteDatabase);
    }
}
