package com.xiaomi.stat.a;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.xiaomi.stat.ak;
import com.xiaomi.stat.d.k;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f8378a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f8379b;

    h(c cVar, String str) {
        this.f8379b = cVar;
        this.f8378a = str;
    }

    public void run() {
        String[] strArr;
        String str;
        try {
            SQLiteDatabase writableDatabase = this.f8379b.l.getWritableDatabase();
            if (TextUtils.equals(this.f8378a, ak.b())) {
                str = "sub is null";
                strArr = null;
            } else {
                str = "sub = ?";
                strArr = new String[]{this.f8378a};
            }
            writableDatabase.delete(j.f8382b, str, strArr);
        } catch (Exception e) {
            k.b("EventManager", "removeAllEventsForApp exception: " + e.toString());
        }
    }
}
