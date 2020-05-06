package com.miui.powercenter.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class k {

    /* renamed from: a  reason: collision with root package name */
    private static k f7309a;

    /* renamed from: b  reason: collision with root package name */
    private Context f7310b;

    private k(Context context) {
        this.f7310b = context;
    }

    public static k a(Context context) {
        if (f7309a == null) {
            f7309a = new k(context.getApplicationContext());
        }
        return f7309a;
    }

    public long a(String str, String str2) {
        long j = 0;
        try {
            Cursor query = this.f7310b.getContentResolver().query(Uri.parse(str), (String[]) null, (String) null, (String[]) null, (String) null);
            if (query != null && query.moveToFirst()) {
                j = query.getLong(query.getColumnIndex(str2));
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception unused) {
        }
        return j;
    }
}
