package com.miui.gamebooster.gbservices;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;

class I implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f4336a;

    I(L l) {
        this.f4336a = l;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IMiuiVpnManageService unused = this.f4336a.f = IMiuiVpnManageService.Stub.asInterface(iBinder);
        try {
            this.f4336a.f.registerCallback(this.f4336a.i);
            this.f4336a.f();
        } catch (Exception e) {
            Log.e("XunyouBoosterService", "mMiuiVpnService:" + e);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("mMiuiVpnService :");
        sb.append(this.f4336a.f == null);
        Log.i("XunyouBoosterService", sb.toString());
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IMiuiVpnManageService unused = this.f4336a.f = null;
    }
}
