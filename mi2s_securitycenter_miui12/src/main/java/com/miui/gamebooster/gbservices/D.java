package com.miui.gamebooster.gbservices;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;

class D implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ E f4320a;

    D(E e) {
        this.f4320a = e;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IFeedbackControl unused = this.f4320a.f4323c = IFeedbackControl.Stub.a(iBinder);
        if (this.f4320a.f4323c != null && this.f4320a.f4321a) {
            try {
                this.f4320a.f4323c.c(1, this.f4320a.f.a());
            } catch (RemoteException e) {
                Log.i("GameBoosterService", e.toString());
            }
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IFeedbackControl unused = this.f4320a.f4323c = null;
    }
}
