package com.miui.gamebooster.gbservices;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.miui.gamebooster.service.IGameBoosterWindow;

class u implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f4382a;

    u(x xVar) {
        this.f4382a = xVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.f4382a.g = IGameBoosterWindow.Stub.a(iBinder);
    }

    public void onServiceDisconnected(ComponentName componentName) {
        this.f4382a.g = null;
    }
}
