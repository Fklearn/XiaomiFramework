package com.miui.gamebooster.gbservices;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.miui.gamebooster.service.IGameBoosterWindow;

class F implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ H f4325a;

    F(H h) {
        this.f4325a = h;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.f4325a.f4330d = IGameBoosterWindow.Stub.a(iBinder);
    }

    public void onServiceDisconnected(ComponentName componentName) {
        this.f4325a.f4330d = null;
    }
}
