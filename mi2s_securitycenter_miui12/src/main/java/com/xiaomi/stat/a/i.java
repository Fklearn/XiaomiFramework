package com.xiaomi.stat.a;

import android.database.DatabaseUtils;
import java.util.concurrent.Callable;

class i implements Callable<Long> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f8380a;

    i(c cVar) {
        this.f8380a = cVar;
    }

    /* renamed from: a */
    public Long call() {
        return Long.valueOf(DatabaseUtils.queryNumEntries(this.f8380a.l.getReadableDatabase(), j.f8382b));
    }
}
