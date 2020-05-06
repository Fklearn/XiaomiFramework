package com.miui.securitycenter.utils;

import android.content.Context;
import com.miui.securitycenter.h;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7541a;

    e(Context context) {
        this.f7541a = context;
    }

    public void run() {
        f.c(this.f7541a);
        h.a(this.f7541a, true);
    }
}
