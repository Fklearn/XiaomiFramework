package com.miui.superpower.notification;

import android.database.ContentObserver;
import android.os.Handler;
import com.miui.powercenter.utils.o;

class a extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f8121a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    a(f fVar, Handler handler) {
        super(handler);
        this.f8121a = fVar;
    }

    public void onChange(boolean z) {
        if (!o.m(this.f8121a.f8126a)) {
            this.f8121a.f.sendEmptyMessage(2);
        }
    }
}
