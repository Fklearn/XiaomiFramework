package com.miui.securityadd.input;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

class e extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ InputProvider f7453a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    e(InputProvider inputProvider, Handler handler) {
        super(handler);
        this.f7453a = inputProvider;
    }

    public void onChange(boolean z) {
        Context context = this.f7453a.getContext();
        if (context != null) {
            g.l(context);
        }
    }
}
