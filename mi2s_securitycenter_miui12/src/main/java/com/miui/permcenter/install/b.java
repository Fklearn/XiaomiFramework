package com.miui.permcenter.install;

import android.os.Handler;
import android.os.Message;

class b extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AdbInstallActivity f6141a;

    b(AdbInstallActivity adbInstallActivity) {
        this.f6141a = adbInstallActivity;
    }

    public void handleMessage(Message message) {
        if (message.what == 10) {
            this.f6141a.m.removeMessages(10);
            AdbInstallActivity.c(this.f6141a);
            if (this.f6141a.l < 0) {
                this.f6141a.a();
                return;
            }
            this.f6141a.m.sendEmptyMessageDelayed(10, 1000);
            this.f6141a.b();
        }
    }
}
