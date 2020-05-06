package com.miui.permcenter.install;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class c extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f6142a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    c(d dVar, Looper looper) {
        super(looper);
        this.f6142a = dVar;
    }

    public void handleMessage(Message message) {
        if (message.what == 10) {
            this.f6142a.j();
        }
    }
}
