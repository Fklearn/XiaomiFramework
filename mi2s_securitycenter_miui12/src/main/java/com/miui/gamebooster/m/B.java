package com.miui.gamebooster.m;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

class B implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f4448a;

    B(Context context) {
        this.f4448a = context;
    }

    public void run() {
        String a2 = C0384o.a("android.content.MiuiIntent", "ACTION_CAPTURE_SCREENSHOT");
        if (a2 != null) {
            this.f4448a.sendBroadcastAsUser(new Intent(a2), UserHandle.CURRENT);
        }
    }
}
