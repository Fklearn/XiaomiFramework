package com.miui.earthquakewarning.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EarthquakeDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "earthquake_warning.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "earthquake";
    private static final String TAG = "EarthquakeDBHelper";

    public EarthquakeDBHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    private void updateDatabase(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.i(TAG, "Database update: from " + i + " to " + i2);
        if (i2 != 1) {
            Log.e(TAG, "Illegal update request. Got " + i2 + ", expected " + 1);
            throw new IllegalArgumentException();
        } else if (i > i2) {
            Log.e(TAG, "Illegal update request: can't downgrade from " + i + " to " + i2 + ". Did you forget to wipe data?");
            throw new IllegalArgumentException("Autotask db version");
        } else if (i < 1) {
            Log.i(TAG, "Upgrading autotask database from version " + i + " to " + i2 + ", which will destroy all old data");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS earthquake");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS earthquake ( _id INTEGER PRIMARY KEY AUTOINCREMENT,eventID INTEGER,index_ew INTEGER default 1,magnitude REAL,longitude REAL,latitude REAL,myLongitude REAL,myLatitude REAL,epicenter TEXT,startTime REAL,signature TEXT,distance TEXT,intensity TEXT,depth REAL,type REAL,updateTime REAL,warntime REAL);");
        }
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        updateDatabase(sQLiteDatabase, 0, 1);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        updateDatabase(sQLiteDatabase, i, i2);
    }
}
