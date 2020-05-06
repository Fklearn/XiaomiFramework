package com.miui.gamebooster.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.gamebooster.service.M;
import com.miui.gamebooster.ui.WelcomActivity;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;

class Sa implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WelcomActivity f4982a;

    Sa(WelcomActivity welcomActivity) {
        this.f4982a = welcomActivity;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IMiuiVpnManageService unused = this.f4982a.f5019b = IMiuiVpnManageService.Stub.asInterface(iBinder);
        WelcomActivity welcomActivity = this.f4982a;
        WelcomActivity.b unused2 = welcomActivity.f = new WelcomActivity.b(welcomActivity);
        try {
            if (!this.f4982a.f5019b.getSupportVpn().contains("xunyou")) {
                Log.i("WelcomActivity", "xunyou getSupportVpn:false");
                this.f4982a.finish();
                return;
            }
            this.f4982a.f5019b.registerCallback(this.f4982a.f);
            this.f4982a.f5019b.init("xunyou");
            M unused3 = this.f4982a.g = M.GETSETTINGURL;
            StringBuilder sb = new StringBuilder();
            sb.append("mMiuiVpnService :");
            sb.append(this.f4982a.f5019b == null);
            Log.i("WelcomActivity", sb.toString());
        } catch (Exception e) {
            Log.i("WelcomActivity", "MiuiVpnServiceException:" + e.toString());
            this.f4982a.finish();
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IMiuiVpnManageService unused = this.f4982a.f5019b = null;
    }
}
