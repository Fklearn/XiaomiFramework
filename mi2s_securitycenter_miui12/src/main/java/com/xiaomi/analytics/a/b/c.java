package com.xiaomi.analytics.a.b;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import com.miui.analytics.ICore;
import com.xiaomi.analytics.a.a.a;

class c implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f8303a;

    c(e eVar) {
        this.f8303a = eVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        boolean unused = this.f8303a.f8306b = true;
        boolean unused2 = this.f8303a.f8307c = false;
        ICore unused3 = this.f8303a.f = ICore.Stub.asInterface(iBinder);
        Log.i(a.a("SysAnalytics"), String.format("onServiceConnected %s, pid:%d, tid:%d", new Object[]{this.f8303a.f, Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid())}));
        synchronized (this.f8303a.f8308d) {
            try {
                this.f8303a.f8308d.notifyAll();
            } catch (Exception e) {
                Log.e(a.a("SysAnalytics"), "onServiceConnected notifyAll exception:", e);
            }
        }
        this.f8303a.d();
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Log.i(a.a("SysAnalytics"), String.format("onServiceDisconnected, pid:%d, tid:%d", new Object[]{Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid())}));
        boolean unused = this.f8303a.f8306b = false;
        ICore unused2 = this.f8303a.f = null;
        boolean unused3 = this.f8303a.f8307c = false;
    }
}
