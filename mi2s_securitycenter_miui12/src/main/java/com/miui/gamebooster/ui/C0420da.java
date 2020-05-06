package com.miui.gamebooster.ui;

import android.os.Handler;
import android.os.Message;
import com.miui.gamebooster.gamead.d;

/* renamed from: com.miui.gamebooster.ui.da  reason: case insensitive filesystem */
class C0420da extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f5057a;

    C0420da(GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f5057a = gameBoosterRealMainActivity;
    }

    public void handleMessage(Message message) {
        this.f5057a.a("VIEW", (d) message.obj);
    }
}
