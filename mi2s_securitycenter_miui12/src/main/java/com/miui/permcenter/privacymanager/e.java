package com.miui.permcenter.privacymanager;

import android.os.Message;
import b.b.c.j.B;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;

class e extends IForegroundInfoListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f6478a;

    e(f fVar) {
        this.f6478a = fVar;
    }

    public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
        this.f6478a.o.setInfo(foregroundInfo.mForegroundPackageName, B.c(foregroundInfo.mForegroundUid));
        if (this.f6478a.k.size() > 0) {
            this.f6478a.f6482d.removeMessages(258);
            Message obtainMessage = this.f6478a.f6482d.obtainMessage(258);
            if (this.f6478a.o.isSameInfo(this.f6478a.m)) {
                this.f6478a.f6482d.sendMessage(obtainMessage);
            } else {
                this.f6478a.f6482d.sendMessageDelayed(obtainMessage, 1000);
            }
        }
    }
}
