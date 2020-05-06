package com.miui.gamebooster.ui;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;

class Ga implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SelectGameActivity f4884a;

    Ga(SelectGameActivity selectGameActivity) {
        this.f4884a = selectGameActivity;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster unused = this.f4884a.k = IGameBooster.Stub.a(iBinder);
        String l = SelectGameActivity.f4983a;
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(this.f4884a.k == null);
        Log.i(l, sb.toString());
        if (this.f4884a.k != null) {
            try {
                this.f4884a.k.n();
            } catch (RemoteException e) {
                Log.i(SelectGameActivity.f4983a, e.toString());
            }
        }
        return false;
    }
}
