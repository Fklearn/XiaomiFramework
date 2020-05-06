package com.miui.gamebooster.mutiwindow;

import android.util.Log;
import com.miui.gamebooster.mutiwindow.l;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;

class k extends IForegroundInfoListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l f4656a;

    k(l lVar) {
        this.f4656a = lVar;
    }

    public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
        Log.i("ProcessMonitor", "onForegroundInfoChanged: " + foregroundInfo);
        l.a aVar = (l.a) this.f4656a.f4659c.get(j.MULTI_WINDOW);
        if (aVar != null) {
            aVar.onForegroundInfoChanged(foregroundInfo);
        }
        l.a aVar2 = (l.a) this.f4656a.f4659c.get(j.GAMEBOOSTER);
        if (aVar2 != null) {
            aVar2.onForegroundInfoChanged(foregroundInfo);
        }
        l.a aVar3 = (l.a) this.f4656a.f4659c.get(j.VIDEO_TOOLBOX);
        if (aVar3 != null) {
            aVar3.onForegroundInfoChanged(foregroundInfo);
        }
    }
}
