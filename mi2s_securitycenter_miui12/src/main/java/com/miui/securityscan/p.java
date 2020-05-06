package com.miui.securityscan;

import android.os.MessageQueue;
import com.miui.securitycenter.h;

class p implements MessageQueue.IdleHandler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7809a;

    p(L l) {
        this.f7809a = l;
    }

    public boolean queueIdle() {
        L l = this.f7809a;
        l.c(!l.h);
        if (!this.f7809a.h || !h.i()) {
            return false;
        }
        this.f7809a.m.postDelayed(new C0544d(this), 200);
        return false;
    }
}
