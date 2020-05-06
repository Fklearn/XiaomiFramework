package com.miui.gamebooster.ui;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;

class Na implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SelectGameLandActivity f4949a;

    Na(SelectGameLandActivity selectGameLandActivity) {
        this.f4949a = selectGameLandActivity;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster unused = this.f4949a.m = IGameBooster.Stub.a(iBinder);
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(this.f4949a.m == null);
        Log.i("SelectGameLandActivity", sb.toString());
        if (this.f4949a.m != null) {
            try {
                this.f4949a.m.n();
            } catch (RemoteException e) {
                Log.i("SelectGameLandActivity", e.toString());
            }
        }
        return false;
    }
}
