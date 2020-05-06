package com.miui.gamebooster.service;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.miui.gamebooster.service.GameBoosterTelecomManager;

class w extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterTelecomManager.b f4839a;

    w(GameBoosterTelecomManager.b bVar) {
        this.f4839a = bVar;
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (message.what == 1) {
            this.f4839a.f.setCallDuration(this.f4839a.a(SystemClock.uptimeMillis() - this.f4839a.e));
            if (!this.f4839a.g) {
                sendEmptyMessageDelayed(1, 500);
            }
        }
    }
}
