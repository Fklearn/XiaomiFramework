package com.miui.applicationlock.c;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/* renamed from: com.miui.applicationlock.c.b  reason: case insensitive filesystem */
class C0258b extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0259c f3295a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0258b(C0259c cVar, Looper looper) {
        super(looper);
        this.f3295a = cVar;
    }

    public void handleMessage(Message message) {
        if (message.what == 1) {
            this.f3295a.e.remove((String) message.obj);
        }
    }
}
