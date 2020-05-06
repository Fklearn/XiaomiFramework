package com.miui.securityadd.input;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

class c extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ InputProvider f7451a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    c(InputProvider inputProvider, Handler handler) {
        super(handler);
        this.f7451a = inputProvider;
    }

    public void onChange(boolean z) {
        Context context = this.f7451a.getContext();
        if (context != null) {
            boolean unused = InputProvider.f7444a = g.j(context);
        }
    }
}
