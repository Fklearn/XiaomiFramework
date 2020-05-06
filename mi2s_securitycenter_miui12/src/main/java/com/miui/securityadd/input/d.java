package com.miui.securityadd.input;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

class d extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ InputProvider f7452a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    d(InputProvider inputProvider, Handler handler) {
        super(handler);
        this.f7452a = inputProvider;
    }

    public void onChange(boolean z) {
        Context context = this.f7452a.getContext();
        if (context != null) {
            boolean unused = InputProvider.f7445b = g.f(context).booleanValue();
        }
    }
}
