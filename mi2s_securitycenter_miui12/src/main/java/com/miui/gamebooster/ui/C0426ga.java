package com.miui.gamebooster.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.preference.Preference;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.securitycenter.p;

/* renamed from: com.miui.gamebooster.ui.ga  reason: case insensitive filesystem */
class C0426ga implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterSettingFragment f5066a;

    C0426ga(GameBoosterSettingFragment gameBoosterSettingFragment) {
        this.f5066a = gameBoosterSettingFragment;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IFeedbackControl unused = this.f5066a.f4890b = IFeedbackControl.Stub.a(iBinder);
        if (this.f5066a.f4890b != null) {
            if (p.a() < 12) {
                try {
                    int unused2 = this.f5066a.f4892d = this.f5066a.f4890b.O() ? 1 : 0;
                } catch (RemoteException e) {
                    Log.i(GameBoosterSettingFragment.f4889a, e.toString());
                }
            } else {
                int unused3 = this.f5066a.f4892d = this.f5066a.f4890b.x();
            }
            if (this.f5066a.f4892d > 0 && this.f5066a.J == 0) {
                GameBoosterSettingFragment gameBoosterSettingFragment = this.f5066a;
                int unused4 = gameBoosterSettingFragment.J = gameBoosterSettingFragment.f4892d;
                this.f5066a.f.b((Preference) this.f5066a.p);
                this.f5066a.f4891c.post(new C0424fa(this));
            }
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IFeedbackControl unused = this.f5066a.f4890b = null;
    }
}
