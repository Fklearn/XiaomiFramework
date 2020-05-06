package com.miui.permcenter;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;

class i implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f6110a;

    i(j jVar) {
        this.f6110a = jVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ISecurityCenterNotificationListener unused = this.f6110a.f6171d = ISecurityCenterNotificationListener.Stub.a(iBinder);
        try {
            this.f6110a.f6171d.b(this.f6110a.g);
        } catch (Exception e) {
            String a2 = j.f6168a;
            Log.e(a2, "mNoticationListenerBinder:" + e);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        ISecurityCenterNotificationListener unused = this.f6110a.f6171d = null;
    }
}
