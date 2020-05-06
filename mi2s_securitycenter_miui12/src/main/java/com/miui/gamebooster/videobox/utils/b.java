package com.miui.gamebooster.videobox.utils;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import com.miui.gamebooster.m.C0384o;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f5200a;

    b(Context context) {
        this.f5200a = context;
    }

    public void run() {
        String a2 = C0384o.a("android.content.MiuiIntent", "ACTION_CAPTURE_SCREENSHOT");
        if (a2 != null) {
            this.f5200a.sendBroadcastAsUser(new Intent(a2), UserHandle.CURRENT);
        }
    }
}
