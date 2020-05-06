package com.miui.common.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.common.ui.ExoTextureView;

class a extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ExoTextureView.a f3864a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    a(ExoTextureView.a aVar, Looper looper) {
        super(looper);
        this.f3864a = aVar;
    }

    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            this.f3864a.d();
        } else if (i == 2) {
            this.f3864a.f();
        }
    }
}
