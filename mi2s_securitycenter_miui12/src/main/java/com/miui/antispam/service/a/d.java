package com.miui.antispam.service.a;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import com.miui.activityutil.o;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private Thread f2408a;

    /* renamed from: b  reason: collision with root package name */
    private Context f2409b;

    public d(Context context) {
        this.f2409b = context;
    }

    /* access modifiers changed from: private */
    public void b() {
        ContentResolver contentResolver = this.f2409b.getContentResolver();
        Cursor query = contentResolver.query(Telephony.Mms.Inbox.CONTENT_URI.buildUpon().appendQueryParameter("blocked_flag", o.f2310b).build(), new String[]{"count(*)"}, "advanced_seen < 3", (String[]) null, (String) null);
        int i = 0;
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    i = query.getInt(0);
                }
            } finally {
                query.close();
            }
        }
        if (i != 0 && !Thread.currentThread().isInterrupted()) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("seen", 1);
            contentValues.put("advanced_seen", 3);
            contentResolver.update(Telephony.Mms.Inbox.CONTENT_URI.buildUpon().appendQueryParameter("blocked_flag", o.f2310b).build(), contentValues, "advanced_seen < 3", (String[]) null);
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        ContentResolver contentResolver = this.f2409b.getContentResolver();
        Cursor query = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI.buildUpon().appendQueryParameter("blocked_flag", o.f2310b).build(), new String[]{"count(*)"}, "advanced_seen<3", (String[]) null, (String) null);
        int i = 0;
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    i = query.getInt(0);
                }
            } finally {
                query.close();
            }
        }
        if (i != 0 && !Thread.currentThread().isInterrupted()) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("seen", 1);
            contentValues.put("advanced_seen", 3);
            contentResolver.update(Telephony.Sms.Inbox.CONTENT_URI.buildUpon().appendQueryParameter("blocked_flag", o.f2310b).build(), contentValues, "advanced_seen < 3", (String[]) null);
        }
    }

    public void a() {
        Thread thread = this.f2408a;
        if (thread != null) {
            thread.interrupt();
        }
        this.f2408a = new Thread(new c(this));
        this.f2408a.start();
    }
}
