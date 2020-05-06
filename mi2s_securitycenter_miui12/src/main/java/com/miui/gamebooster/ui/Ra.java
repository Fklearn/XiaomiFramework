package com.miui.gamebooster.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.securitycenter.p;

class Ra implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsActivity f4973a;

    Ra(SettingsActivity settingsActivity) {
        this.f4973a = settingsActivity;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IFeedbackControl unused = this.f4973a.f4999b = IFeedbackControl.Stub.a(iBinder);
        if (this.f4973a.f4999b != null) {
            if (p.a() < 12) {
                try {
                    int unused2 = this.f4973a.f5000c = this.f4973a.f4999b.O() ? 1 : 0;
                } catch (RemoteException e) {
                    Log.i("SettingsActivity", e.toString());
                }
            } else {
                int unused3 = this.f4973a.f5000c = this.f4973a.f4999b.x();
            }
            Intent intent = new Intent();
            intent.setAction("gb_thermal_supported_action");
            intent.putExtra("gb_thermal_supported", this.f4973a.f5000c);
            this.f4973a.f5001d.sendBroadcast(intent);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IFeedbackControl unused = this.f4973a.f4999b = null;
    }
}
