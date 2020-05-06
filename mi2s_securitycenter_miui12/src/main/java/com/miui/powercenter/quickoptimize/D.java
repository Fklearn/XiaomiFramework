package com.miui.powercenter.quickoptimize;

import android.os.Message;
import b.b.c.i.b;

class D extends b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ScanResultFrame f7193a;

    D(ScanResultFrame scanResultFrame) {
        this.f7193a = scanResultFrame;
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (message.what == 1) {
            this.f7193a.j();
        }
    }
}
