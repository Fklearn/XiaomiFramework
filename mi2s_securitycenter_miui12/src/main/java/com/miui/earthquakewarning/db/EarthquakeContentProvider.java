package com.miui.earthquakewarning.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class EarthquakeContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.miui.earthquakewarning.EarthquakeContentProvider";
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    private Context mContext;
    private SQLiteDatabase mDataBase;

    static {
        sUriMatcher.addURI(AUTHORITY, EarthquakeDBHelper.TABLE_NAME, 0);
    }

    public int delete(@NonNull Uri uri, @Nullable String str, @Nullable String[] strArr) {
        if (sUriMatcher.match(uri) == 0) {
            int delete = this.mDataBase.delete(EarthquakeDBHelper.TABLE_NAME, str, strArr);
            if (delete > 0) {
                this.mContext.getContentResolver().notifyChange(uri, (ContentObserver) null);
            }
            return delete;
        }
        throw new IllegalArgumentException("UnSupport Uri : " + uri);
    }

    @Nullable
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (sUriMatcher.match(uri) == 0) {
            long insert = this.mDataBase.insert(EarthquakeDBHelper.TABLE_NAME, (String) null, contentValues);
            if (insert <= -1) {
                return null;
            }
            this.mContext.getContentResolver().notifyChange(uri, (ContentObserver) null);
            return ContentUris.withAppendedId(uri, insert);
        }
        throw new IllegalArgumentException("UnSupport Uri : " + uri);
    }

    public boolean onCreate() {
        this.mContext = getContext();
        this.mDataBase = new EarthquakeDBHelper(this.mContext).getWritableDatabase();
        return true;
    }

    @Nullable
    public Cursor query(@NonNull Uri uri, @Nullable String[] strArr, @Nullable String str, @Nullable String[] strArr2, @Nullable String str2) {
        if (sUriMatcher.match(uri) == 0) {
            return this.mDataBase.query(EarthquakeDBHelper.TABLE_NAME, strArr, str, strArr2, (String) null, (String) null, str2, (String) null);
        }
        throw new IllegalArgumentException("UnSupport Uri : " + uri);
    }

    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String str, @Nullable String[] strArr) {
        if (sUriMatcher.match(uri) == 0) {
            int update = this.mDataBase.update(EarthquakeDBHelper.TABLE_NAME, contentValues, str, strArr);
            if (update > 0) {
                this.mContext.getContentResolver().notifyChange(uri, (ContentObserver) null);
            }
            return update;
        }
        throw new IllegalArgumentException("UnSupport Uri : " + uri);
    }
}
