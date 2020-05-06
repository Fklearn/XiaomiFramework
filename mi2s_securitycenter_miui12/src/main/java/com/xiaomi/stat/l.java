package com.xiaomi.stat;

import android.text.TextUtils;
import com.xiaomi.stat.b.g;
import com.xiaomi.stat.d.m;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8585a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f8586b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f8587c;

    l(e eVar, boolean z, String str) {
        this.f8587c = eVar;
        this.f8585a = z;
        this.f8586b = str;
    }

    public void run() {
        if (!m.a()) {
            b.c(this.f8585a);
            g.a().a(this.f8585a);
        }
        if (b.e() && !TextUtils.isEmpty(this.f8586b)) {
            b.a(this.f8586b);
            g.a().a(this.f8586b);
        }
    }
}
