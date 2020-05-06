package com.miui.superpower;

import android.database.ContentObserver;
import android.os.Handler;
import com.miui.superpower.b.g;

class h extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f8105a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    h(o oVar, Handler handler) {
        super(handler);
        this.f8105a = oVar;
    }

    public void onChange(boolean z) {
        this.f8105a.m.set(g.b());
        if (this.f8105a.m.get()) {
            this.f8105a.e.post(new g(this));
        }
    }
}
