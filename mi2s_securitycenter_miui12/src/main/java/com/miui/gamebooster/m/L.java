package com.miui.gamebooster.m;

import android.database.ContentObserver;
import android.os.Handler;

class L extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4452a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    L(N n, Handler handler) {
        super(handler);
        this.f4452a = n;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        if (C0384o.a(this.f4452a.f.getContentResolver(), this.f4452a.i, 0, -2) == 0 && this.f4452a.f4457d) {
            this.f4452a.i();
        }
    }
}
