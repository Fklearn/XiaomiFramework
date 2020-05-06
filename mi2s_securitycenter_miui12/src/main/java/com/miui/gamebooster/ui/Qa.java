package com.miui.gamebooster.ui;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;

class Qa implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsActivity f4965a;

    Qa(SettingsActivity settingsActivity) {
        this.f4965a = settingsActivity;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster unused = this.f4965a.f4998a = IGameBooster.Stub.a(iBinder);
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(this.f4965a.f4998a == null);
        Log.i("SettingsActivity", sb.toString());
        if (this.f4965a.f4998a != null) {
            try {
                this.f4965a.f4998a.n();
            } catch (RemoteException e) {
                Log.i("SettingsActivity", e.toString());
            }
        }
        return false;
    }
}
