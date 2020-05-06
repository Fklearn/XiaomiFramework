package com.miui.gamebooster.ui;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;

class X implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f5034a;

    X(GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f5034a = gameBoosterRealMainActivity;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster unused = this.f5034a.q = IGameBooster.Stub.a(iBinder);
        String l = GameBoosterRealMainActivity.f4885a;
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(this.f5034a.q == null);
        Log.i(l, sb.toString());
        if (this.f5034a.q != null) {
            try {
                this.f5034a.q.n();
            } catch (RemoteException e) {
                Log.i(GameBoosterRealMainActivity.f4885a, e.toString());
            }
        }
        return false;
    }
}
