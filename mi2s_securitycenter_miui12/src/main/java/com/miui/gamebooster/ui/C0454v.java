package com.miui.gamebooster.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.miui.applicationlock.c.K;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;

/* renamed from: com.miui.gamebooster.ui.v  reason: case insensitive filesystem */
class C0454v implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f5120a;

    C0454v(N n) {
        this.f5120a = n;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IMiuiVpnManageService unused = this.f5120a.o = IMiuiVpnManageService.Stub.asInterface(iBinder);
        boolean z = false;
        new C0433k(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        try {
            this.f5120a.o.registerCallback(this.f5120a.p);
            Activity activity = this.f5120a.getActivity();
            if (activity != null && activity.getIntent().getBooleanExtra("top", false) && K.c(this.f5120a.mAppContext)) {
                this.f5120a.q.a(114, new Object());
            }
        } catch (Exception e) {
            Log.i(N.f4939a, e.toString());
        }
        String e2 = N.f4939a;
        StringBuilder sb = new StringBuilder();
        sb.append("mMiuiVpnService :");
        if (this.f5120a.o == null) {
            z = true;
        }
        sb.append(z);
        Log.i(e2, sb.toString());
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IMiuiVpnManageService unused = this.f5120a.o = null;
    }
}
