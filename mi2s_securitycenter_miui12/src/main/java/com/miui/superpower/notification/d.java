package com.miui.superpower.notification;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;

class d implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f8124a;

    d(f fVar) {
        this.f8124a = fVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (!this.f8124a.j.get()) {
            Message obtainMessage = this.f8124a.f.obtainMessage();
            obtainMessage.obj = ISecurityCenterNotificationListener.Stub.a(iBinder);
            obtainMessage.what = 6;
            this.f8124a.f.sendMessage(obtainMessage);
            return;
        }
        this.f8124a.f8126a.unbindService(this.f8124a.h);
    }

    public void onServiceDisconnected(ComponentName componentName) {
        ISecurityCenterNotificationListener unused = this.f8124a.i = null;
    }
}
