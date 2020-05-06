package com.miui.superpower;

import android.database.ContentObserver;
import android.os.Handler;
import com.miui.superpower.b.g;

class f extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f8103a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    f(o oVar, Handler handler) {
        super(handler);
        this.f8103a = oVar;
    }

    public void onChange(boolean z) {
        this.f8103a.l.set(g.a());
        if (this.f8103a.l.get()) {
            this.f8103a.e.post(new e(this));
        }
    }
}
