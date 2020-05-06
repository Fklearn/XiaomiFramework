package com.miui.gamebooster.gbservices;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;

/* renamed from: com.miui.gamebooster.gbservices.k  reason: case insensitive filesystem */
class C0368k implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AntiMsgAccessibilityService f4359a;

    C0368k(AntiMsgAccessibilityService antiMsgAccessibilityService) {
        this.f4359a = antiMsgAccessibilityService;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ISecurityCenterNotificationListener unused = this.f4359a.n = ISecurityCenterNotificationListener.Stub.a(iBinder);
        try {
            this.f4359a.n.b(this.f4359a.v);
        } catch (Exception e) {
            Log.e("AntiMsgAccessibilityService", "mNoticationListenerBinder:" + e);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        ISecurityCenterNotificationListener unused = this.f4359a.n = null;
    }
}
