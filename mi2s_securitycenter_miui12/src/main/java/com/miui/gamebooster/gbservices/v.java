package com.miui.gamebooster.gbservices;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;

class v implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f4383a;

    v(x xVar) {
        this.f4383a = xVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ISecurityCenterNotificationListener unused = this.f4383a.h = ISecurityCenterNotificationListener.Stub.a(iBinder);
        try {
            this.f4383a.h.b(this.f4383a.l);
        } catch (Exception e) {
            Log.e("GameBoxService", "mNoticationListenerBinder:" + e);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        ISecurityCenterNotificationListener unused = this.f4383a.h = null;
    }
}
