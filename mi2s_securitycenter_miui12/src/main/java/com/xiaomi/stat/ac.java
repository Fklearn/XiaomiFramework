package com.xiaomi.stat;

import android.database.Cursor;
import com.xiaomi.stat.ab;
import java.util.concurrent.Callable;

class ac implements Callable<Cursor> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ab f8407a;

    ac(ab abVar) {
        this.f8407a = abVar;
    }

    /* renamed from: a */
    public Cursor call() {
        try {
            return this.f8407a.g.getWritableDatabase().query(ab.a.f8404b, (String[]) null, (String) null, (String[]) null, (String) null, (String) null, (String) null);
        } catch (Exception unused) {
            return null;
        }
    }
}
