package com.miui.antispam.policy.a;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.guardprovider.aidl.IURLScanServer;

class h implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f2380a;

    h(i iVar) {
        this.f2380a = iVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.i(i.f2381a, "on GPService Connected");
        IURLScanServer unused = this.f2380a.f2384d = IURLScanServer.Stub.a(iBinder);
        this.f2380a.f2383c.countDown();
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Log.i(i.f2381a, "on GPService DisConnected");
        IURLScanServer unused = this.f2380a.f2384d = null;
    }
}
