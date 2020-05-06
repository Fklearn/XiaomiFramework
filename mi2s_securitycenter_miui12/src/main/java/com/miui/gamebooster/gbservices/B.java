package com.miui.gamebooster.gbservices;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.miui.gamebooster.service.IGameBoosterTelecomeManager;

class B implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C f4315a;

    B(C c2) {
        this.f4315a = c2;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IGameBoosterTelecomeManager unused = this.f4315a.i = IGameBoosterTelecomeManager.Stub.a(iBinder);
        try {
            this.f4315a.i.P();
        } catch (Exception unused2) {
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IGameBoosterTelecomeManager unused = this.f4315a.i = null;
    }
}
