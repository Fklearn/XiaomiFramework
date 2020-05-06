package com.miui.gamebooster.service;

import android.database.ContentObserver;
import android.os.Handler;

class o extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4827a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    o(r rVar, Handler handler) {
        super(handler);
        this.f4827a = rVar;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        this.f4827a.s();
    }
}
