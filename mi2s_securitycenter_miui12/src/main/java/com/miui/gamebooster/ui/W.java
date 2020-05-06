package com.miui.gamebooster.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;

class W implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f5015a;

    W(GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f5015a = gameBoosterRealMainActivity;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.f5015a.g = IMiuiVpnManageService.Stub.asInterface(iBinder);
        boolean z = true;
        try {
            if (!this.f5015a.g.getSupportVpn().contains("xunyou")) {
                this.f5015a.l = false;
            } else {
                this.f5015a.l = true;
                if (this.f5015a.o) {
                    this.f5015a.f();
                }
                this.f5015a.g.registerCallback(this.f5015a.k);
            }
            Intent intent = new Intent();
            intent.setAction("gb_update_adapter_action");
            intent.putExtra("gb_intent_xunyouuser", this.f5015a.l);
            this.f5015a.h.sendBroadcast(intent);
        } catch (Exception e) {
            Log.i(GameBoosterRealMainActivity.f4885a, e.toString());
        }
        String l = GameBoosterRealMainActivity.f4885a;
        StringBuilder sb = new StringBuilder();
        sb.append("mMiuiVpnService :");
        if (this.f5015a.g != null) {
            z = false;
        }
        sb.append(z);
        Log.i(l, sb.toString());
    }

    public void onServiceDisconnected(ComponentName componentName) {
        this.f5015a.g = null;
    }
}
