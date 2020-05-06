package com.miui.powercenter.quickoptimize;

import android.os.Handler;
import android.os.Message;

class C extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ScanResultFrame f7192a;

    C(ScanResultFrame scanResultFrame) {
        this.f7192a = scanResultFrame;
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        this.f7192a.f();
    }
}
