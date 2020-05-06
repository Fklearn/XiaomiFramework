package com.xiaomi.analytics.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.miui.networkassistant.config.Constants;
import com.xiaomi.analytics.a.a.a;

class g extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f8316a;

    g(i iVar) {
        this.f8316a = iVar;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            try {
                String action = intent.getAction();
                if (Constants.System.ACTION_SCREEN_OFF.equals(action)) {
                    long unused = this.f8316a.n = System.currentTimeMillis();
                    boolean unused2 = this.f8316a.m = true;
                    if (this.f8316a.q != null) {
                        this.f8316a.a((long) this.f8316a.l());
                    } else {
                        this.f8316a.e.unregisterReceiver(this.f8316a.u);
                        a.a("SdkManager", "pending dex is null, unregister");
                    }
                } else if (Constants.System.ACTION_SCREEN_ON.equals(action)) {
                    boolean unused3 = this.f8316a.m = false;
                }
                a.a("SdkManager", "screen off : " + this.f8316a.m);
            } catch (Exception e) {
                a.a("SdkManager", "mScreenReceiver onReceive e", e);
            }
        }
    }
}
