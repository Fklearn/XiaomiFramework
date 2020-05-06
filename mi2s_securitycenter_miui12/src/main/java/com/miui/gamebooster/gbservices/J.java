package com.miui.gamebooster.gbservices;

import android.os.RemoteException;
import android.util.Log;
import com.miui.gamebooster.service.M;

class J implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f4337a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ K f4338b;

    J(K k, int i) {
        this.f4338b = k;
        this.f4337a = i;
    }

    public void run() {
        try {
            if (!this.f4338b.f4339a.f4341b && this.f4337a == 100) {
                boolean unused = this.f4338b.f4339a.f4341b = true;
            }
            if (this.f4338b.f4339a.f4341b) {
                this.f4338b.f4339a.f.connectVpn(this.f4338b.f4339a.e.a());
                Log.i("XunyouBoosterService", "connect:" + this.f4338b.f4339a.e.a());
                M unused2 = this.f4338b.f4339a.g = M.CONNECTVPN;
                boolean unused3 = this.f4338b.f4339a.f4341b = false;
            }
        } catch (RemoteException e) {
            Log.e("XunyouBoosterService", "RemoteException:" + e);
        }
    }
}
