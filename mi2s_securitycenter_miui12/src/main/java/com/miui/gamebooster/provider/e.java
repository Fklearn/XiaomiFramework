package com.miui.gamebooster.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import b.b.c.j.B;

public class e extends SQLiteOpenHelper {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4750a = "com.miui.gamebooster.provider.e";

    public e(Context context) {
        super(context, "gamebooster.db", (SQLiteDatabase.CursorFactory) null, 6);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Log.d(f4750a, "create table :gamebooster_table");
        sQLiteDatabase.execSQL("CREATE TABLE gamebooster_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT,  package_name TEXT NOT NULL, app_name TEXT NOT NULL, package_uid INTEGER NOT NULL, pop_game TEXT, flag_white INTEGER, is_del INTEGER, sort_index INTEGER, settings_gs INTEGER, settings_ts INTEGER, settings_edge INTEGER, settings_hdr INTEGER, settings_4d INTEGER );");
        sQLiteDatabase.execSQL("CREATE TABLE quickreply_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT,  package_name TEXT NOT NULL, package_uid INTEGER NOT NULL );");
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        String str = f4750a;
        Log.d(str, "Downgrading database for gamebooster.db form version " + i + " to " + i2);
        sQLiteDatabase.beginTransaction();
        try {
            sQLiteDatabase.execSQL("DROP TABLE gamebooster_table");
            sQLiteDatabase.execSQL("DROP TABLE quickreply_table");
            sQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(f4750a, e.toString());
        } catch (Throwable th) {
            sQLiteDatabase.endTransaction();
            throw th;
        }
        sQLiteDatabase.endTransaction();
        onCreate(sQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.d(f4750a, "Upgrading database for gamebooster.db form version " + i + " to " + i2);
        if (i == 1) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE gamebooster_table ADD COLUMN pop_game TEXT ");
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(f4750a, e.toString());
            } catch (Throwable th) {
                sQLiteDatabase.endTransaction();
                throw th;
            }
            sQLiteDatabase.endTransaction();
            i = 2;
        }
        if (i == 2) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE gamebooster_table ADD COLUMN package_uid integer ");
                ContentValues contentValues = new ContentValues();
                contentValues.put("package_uid", Integer.valueOf(B.c()));
                sQLiteDatabase.update("gamebooster_table", contentValues, (String) null, (String[]) null);
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e2) {
                Log.e(f4750a, e2.toString());
            } catch (Throwable th2) {
                sQLiteDatabase.endTransaction();
                throw th2;
            }
            sQLiteDatabase.endTransaction();
            i = 3;
        }
        if (i == 3) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE gamebooster_table ADD COLUMN flag_white INTEGER ");
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("flag_white", 0);
                sQLiteDatabase.update("gamebooster_table", contentValues2, (String) null, (String[]) null);
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e3) {
                Log.e(f4750a, e3.toString());
            } catch (Throwable th3) {
                sQLiteDatabase.endTransaction();
                throw th3;
            }
            sQLiteDatabase.endTransaction();
            i = 4;
        }
        if (i == 4) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE gamebooster_table ADD COLUMN settings_gs INTEGER");
                sQLiteDatabase.execSQL("ALTER TABLE gamebooster_table ADD COLUMN settings_ts INTEGER");
                sQLiteDatabase.execSQL("ALTER TABLE gamebooster_table ADD COLUMN settings_edge INTEGER");
                sQLiteDatabase.execSQL("ALTER TABLE gamebooster_table ADD COLUMN settings_hdr INTEGER");
                sQLiteDatabase.execSQL("ALTER TABLE gamebooster_table ADD COLUMN settings_4d INTEGER");
                ContentValues contentValues3 = new ContentValues();
                contentValues3.put("settings_gs", -1);
                contentValues3.put("settings_ts", -1);
                contentValues3.put("settings_edge", -1);
                contentValues3.put("settings_hdr", -1);
                contentValues3.put("settings_4d", 0);
                sQLiteDatabase.update("gamebooster_table", contentValues3, (String) null, (String[]) null);
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e4) {
                Log.e(f4750a, e4.toString());
            } catch (Throwable th4) {
                sQLiteDatabase.endTransaction();
                throw th4;
            }
            sQLiteDatabase.endTransaction();
            i = 5;
        }
        if (i == 5) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("CREATE TABLE quickreply_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT,  package_name TEXT NOT NULL, package_uid INTEGER NOT NULL );");
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e5) {
                Log.e(f4750a, e5.toString());
            } catch (Throwable th5) {
                sQLiteDatabase.endTransaction();
                throw th5;
            }
            sQLiteDatabase.endTransaction();
        }
    }
}
