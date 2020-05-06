package com.miui.gamebooster.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.miui.gamebooster.m.F;
import com.miui.gamebooster.m.ja;
import com.xiaomi.migameservice.IGameCenterInterface;

class D implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4754a;

    D(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4754a = gameBoxWindowManagerService;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IGameCenterInterface unused = this.f4754a.t = IGameCenterInterface.Stub.a(iBinder);
        try {
            if (F.a(this.f4754a.l)) {
                boolean z = true;
                if (ja.a("key_gb_record_ai", this.f4754a.l)) {
                    this.f4754a.t.h(this.f4754a.l);
                    z = false;
                }
                if (ja.a("key_gb_record_manual", this.f4754a.l)) {
                    this.f4754a.t.m(30);
                    this.f4754a.t.i(this.f4754a.l);
                    if (this.f4754a.f4774c != null) {
                        this.f4754a.f4774c.a();
                    }
                    z = false;
                }
                if (!z) {
                    this.f4754a.t.a(546542, this.f4754a.r);
                }
            }
        } catch (RemoteException e) {
            Log.e("GameBoxWindowManager", "service error", e);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Log.e("GameBoxWindowManager", "gamecenter service disconnected " + componentName);
        IGameCenterInterface unused = this.f4754a.t = null;
    }
}
