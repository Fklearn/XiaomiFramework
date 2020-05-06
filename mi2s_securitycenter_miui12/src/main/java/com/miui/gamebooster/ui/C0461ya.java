package com.miui.gamebooster.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;

/* renamed from: com.miui.gamebooster.ui.ya  reason: case insensitive filesystem */
class C0461ya implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Ba f5129a;

    C0461ya(Ba ba) {
        this.f5129a = ba;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IMiuiVpnManageService unused = this.f5129a.k = IMiuiVpnManageService.Stub.asInterface(iBinder);
        try {
            Boolean unused2 = this.f5129a.l = Boolean.valueOf(this.f5129a.k.getSettingEx("xunyou", "xunyou_wifi_accel_switch", "false"));
        } catch (Exception e) {
            Log.i("PerformanceSettingsFrag", e.toString());
        }
        a.U(this.f5129a.l.booleanValue());
        boolean z = false;
        this.f5129a.g.a(this.f5129a.l.booleanValue(), false, false);
        StringBuilder sb = new StringBuilder();
        sb.append("mMiuiVpnService :");
        if (this.f5129a.k == null) {
            z = true;
        }
        sb.append(z);
        Log.i("PerformanceSettingsFrag", sb.toString());
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IMiuiVpnManageService unused = this.f5129a.k = null;
    }
}
