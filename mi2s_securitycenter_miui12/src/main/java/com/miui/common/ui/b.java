package com.miui.common.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.common.ui.MediaTextureView;

class b extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MediaTextureView.a f3865a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(MediaTextureView.a aVar, Looper looper) {
        super(looper);
        this.f3865a = aVar;
    }

    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            this.f3865a.c();
        } else if (i != 2) {
            if (i != 3) {
            }
        } else {
            this.f3865a.e();
        }
    }
}
