package com.miui.permcenter.privacymanager.b;

import android.os.Bundle;
import android.os.Message;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;

class n extends IForegroundInfoListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f6384a;

    n(o oVar) {
        this.f6384a = oVar;
    }

    public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
        Message obtainMessage = this.f6384a.k.obtainMessage(1);
        Bundle bundle = new Bundle();
        bundle.putString("curr_pkg", foregroundInfo.mForegroundPackageName);
        bundle.putString("prev_pkg", foregroundInfo.mLastForegroundPackageName);
        obtainMessage.setData(bundle);
        this.f6384a.k.sendMessageDelayed(obtainMessage, 300);
    }
}
