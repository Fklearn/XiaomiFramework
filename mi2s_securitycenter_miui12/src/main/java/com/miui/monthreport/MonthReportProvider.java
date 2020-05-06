package com.miui.monthreport;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import java.util.UUID;

public class MonthReportProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private b f5613a;

    /* renamed from: b  reason: collision with root package name */
    private ContentResolver f5614b;

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        long currentTimeMillis = System.currentTimeMillis();
        Integer asInteger = contentValues.getAsInteger("eventType");
        if (asInteger == null) {
            return null;
        }
        if (!contentValues.containsKey("eventId")) {
            contentValues.put("eventId", asInteger + UUID.randomUUID().toString().replace("-", "") + currentTimeMillis);
        }
        if (!contentValues.containsKey("eventTime")) {
            contentValues.put("eventTime", Long.valueOf(currentTimeMillis));
        }
        if (!contentValues.containsKey("moduleName")) {
            contentValues.put("moduleName", "monthReport");
        }
        long a2 = this.f5613a.a(contentValues);
        if (a2 <= 0) {
            return null;
        }
        Uri withAppendedId = ContentUris.withAppendedId(e.f5638a, a2);
        this.f5614b.notifyChange(withAppendedId, (ContentObserver) null);
        return withAppendedId;
    }

    public boolean onCreate() {
        this.f5613a = b.a();
        this.f5614b = getContext().getContentResolver();
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
