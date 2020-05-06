package com.miui.superpower.notification;

import android.database.ContentObserver;
import android.os.Handler;

class b extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f8122a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(f fVar, Handler handler) {
        super(handler);
        this.f8122a = fVar;
    }

    public void onChange(boolean z) {
        this.f8122a.f.sendEmptyMessage(4);
    }
}
