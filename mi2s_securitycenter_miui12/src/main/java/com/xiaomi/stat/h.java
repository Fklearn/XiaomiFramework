package com.xiaomi.stat;

import android.text.TextUtils;
import com.xiaomi.stat.a.l;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f8577a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e f8578b;

    h(e eVar, String str) {
        this.f8578b = eVar;
        this.f8577a = str;
    }

    public void run() {
        if (b.a() && !TextUtils.equals(b.h(), this.f8577a)) {
            b.b(this.f8577a);
            if (this.f8578b.g()) {
                this.f8578b.a(l.a(this.f8577a));
            }
        }
    }
}
