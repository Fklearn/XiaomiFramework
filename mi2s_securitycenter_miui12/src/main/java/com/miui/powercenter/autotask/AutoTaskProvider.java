package com.miui.powercenter.autotask;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class AutoTaskProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static final UriMatcher f6688a = new UriMatcher(-1);

    /* renamed from: b  reason: collision with root package name */
    private ca f6689b;

    static {
        f6688a.addURI(AutoTask.AUTHORITY, AutoTask.TABLE_NAME, 1);
        f6688a.addURI(AutoTask.AUTHORITY, "autotasks/#", 2);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        Log.d("AutoTaskProvider", "delete auto task " + uri.toString());
        SQLiteDatabase writableDatabase = this.f6689b.getWritableDatabase();
        int match = f6688a.match(uri);
        if (match != 1) {
            if (match == 2) {
                str = "_id=" + uri.getPathSegments().get(1);
            } else {
                throw new IllegalArgumentException("Unknown uri " + uri);
            }
        }
        int delete = writableDatabase.delete(AutoTask.TABLE_NAME, str, strArr);
        getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
        return delete;
    }

    public String getType(Uri uri) {
        int match = f6688a.match(uri);
        if (match == 1) {
            return "vnd.android.cursor.dir/autotasks";
        }
        if (match == 2) {
            return "vnd.android.cursor.item/autotasks";
        }
        throw new IllegalArgumentException("Unknown uri " + uri);
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d("AutoTaskProvider", "insert auto task " + uri.toString());
        SQLiteDatabase writableDatabase = this.f6689b.getWritableDatabase();
        if (f6688a.match(uri) == 1) {
            long insert = writableDatabase.insert(AutoTask.TABLE_NAME, (String) null, contentValues);
            if (insert >= 0) {
                Uri withAppendedId = ContentUris.withAppendedId(AutoTask.CONTENT_URI, insert);
                if (withAppendedId != null) {
                    getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
                    getContext().getContentResolver().notifyChange(withAppendedId, (ContentObserver) null);
                }
                return withAppendedId;
            }
            throw new SQLException("Failed to insert row");
        }
        throw new IllegalArgumentException("Unknown uri " + uri);
    }

    public boolean onCreate() {
        Log.d("AutoTaskProvider", "onCreate");
        this.f6689b = new ca(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        int match = f6688a.match(uri);
        if (match == 1) {
            sQLiteQueryBuilder.setTables(AutoTask.TABLE_NAME);
        } else if (match == 2) {
            sQLiteQueryBuilder.setTables(AutoTask.TABLE_NAME);
            sQLiteQueryBuilder.appendWhere("_id=");
            sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
        } else {
            throw new IllegalArgumentException("Unknown uri " + uri);
        }
        Cursor query = sQLiteQueryBuilder.query(this.f6689b.getWritableDatabase(), strArr, str, strArr2, (String) null, (String) null, str2);
        if (query == null) {
            Log.e("AutoTaskProvider", "Query failed");
        }
        return query;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        Log.d("AutoTaskProvider", "update auto task " + uri.toString());
        SQLiteDatabase writableDatabase = this.f6689b.getWritableDatabase();
        if (f6688a.match(uri) == 2) {
            long parseLong = Long.parseLong(uri.getPathSegments().get(1));
            int update = writableDatabase.update(AutoTask.TABLE_NAME, contentValues, "_id=" + parseLong, (String[]) null);
            getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
            return update;
        }
        throw new IllegalArgumentException("Unknown uri " + uri);
    }
}
