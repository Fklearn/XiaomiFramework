package com.miui.powercenter.autotask;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import miui.cloud.CloudPushConstants;

public class ca extends SQLiteOpenHelper {
    public ca(Context context) {
        super(context, AutoTask.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    private void a(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.i("TaskDBHelper", "Database update: from " + i + " to " + i2);
        if (i2 != 1) {
            Log.e("TaskDBHelper", "Illegal update request. Got " + i2 + ", expected " + 1);
            throw new IllegalArgumentException();
        } else if (i > i2) {
            Log.e("TaskDBHelper", "Illegal update request: can't downgrade from " + i + " to " + i2 + ". Did you forget to wipe data?");
            throw new IllegalArgumentException("Autotask db version");
        } else if (i < 1) {
            Log.i("TaskDBHelper", "Upgrading autotask database from version " + i + " to " + i2 + ", which will destroy all old data");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS autotasks");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS autotasks ( _id  INTEGER PRIMARY KEY,name TEXT,enabled BOOLEAN default 0, condition TEXT,operation TEXT,repeat_type INTEGER default 127,task_started BOOLEAN default 0,restore_operation TEXT,restore_level INTEGER default 0);");
            for (AutoTask next : AutoTask.getInitAutoTaskList()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(CloudPushConstants.XML_NAME, next.getName());
                contentValues.put("enabled", Boolean.valueOf(next.getEnabled()));
                contentValues.put("condition", next.getConditionString());
                contentValues.put("operation", next.getOperationString());
                contentValues.put("repeat_type", Integer.valueOf(next.getRepeatType()));
                contentValues.put("task_started", Boolean.valueOf(next.getStarted()));
                contentValues.put("restore_operation", next.getRestoreOperationString());
                contentValues.put("restore_level", Integer.valueOf(next.getRestoreLevel()));
                sQLiteDatabase.insert(AutoTask.TABLE_NAME, (String) null, contentValues);
            }
        }
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        a(sQLiteDatabase, 0, 1);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        a(sQLiteDatabase, i, i2);
    }
}
