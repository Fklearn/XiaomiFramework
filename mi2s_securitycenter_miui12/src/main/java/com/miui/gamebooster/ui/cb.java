package com.miui.gamebooster.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;

class cb implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WifiBoosterDetail f5054a;

    cb(WifiBoosterDetail wifiBoosterDetail) {
        this.f5054a = wifiBoosterDetail;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IMiuiVpnManageService unused = this.f5054a.f5031b = IMiuiVpnManageService.Stub.asInterface(iBinder);
        try {
            Boolean unused2 = this.f5054a.h = Boolean.valueOf(this.f5054a.f5031b.getSettingEx("xunyou", "xunyou_wifi_accel_switch", "false"));
        } catch (Exception e) {
            Log.i(WifiBoosterDetail.f5030a, e.toString());
        }
        this.f5054a.f5032c.setChecked(this.f5054a.h.booleanValue());
        String l = WifiBoosterDetail.f5030a;
        StringBuilder sb = new StringBuilder();
        sb.append("mMiuiVpnService :");
        sb.append(this.f5054a.f5031b == null);
        Log.i(l, sb.toString());
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IMiuiVpnManageService unused = this.f5054a.f5031b = null;
    }
}
