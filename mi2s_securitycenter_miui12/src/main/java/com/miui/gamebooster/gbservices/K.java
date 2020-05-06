package com.miui.gamebooster.gbservices;

import android.util.Log;
import b.b.c.c.a.a;
import com.miui.gamebooster.service.M;
import com.miui.gamebooster.service.MiuiVpnManageServiceCallback;

class K extends MiuiVpnManageServiceCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f4339a;

    K(L l) {
        this.f4339a = l;
    }

    public boolean isVpnConnected() {
        return super.isVpnConnected();
    }

    public void onVpnStateChanged(int i, int i2, String str) {
        String str2;
        super.onVpnStateChanged(i, i2, str);
        if (this.f4339a.f != null) {
            a.a(new J(this, i2));
            if (this.f4339a.g == M.CONNECTVPN && i2 == 1001) {
                str2 = "vpn booster success";
            } else {
                if (this.f4339a.g == M.CONNECTVPN && i2 == 1002) {
                    str2 = "vpn booster failed";
                }
                Log.i("XunyouBoosterService", "VpnType:" + i + " " + "VpnState:" + i2 + " " + "Vpndata:" + str);
            }
            Log.i("XunyouBoosterService", str2);
            M unused = this.f4339a.g = M.INIT;
            Log.i("XunyouBoosterService", "VpnType:" + i + " " + "VpnState:" + i2 + " " + "Vpndata:" + str);
        }
    }
}
