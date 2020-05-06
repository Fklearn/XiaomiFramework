package com.miui.securityscan.h.a;

import android.os.Handler;
import android.os.Message;
import com.google.android.exoplayer2.DefaultRenderersFactory;

class b extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f7716a;

    b(c cVar) {
        this.f7716a = cVar;
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        int i = message.what;
        if (i == 1) {
            this.f7716a.c();
            sendEmptyMessageDelayed(2, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } else if (i == 2 && this.f7716a.e != null) {
            this.f7716a.e.a();
        }
    }
}
