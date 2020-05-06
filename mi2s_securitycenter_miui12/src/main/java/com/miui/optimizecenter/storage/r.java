package com.miui.optimizecenter.storage;

import android.os.Message;
import java.util.List;

class r implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s f5769a;

    r(s sVar) {
        this.f5769a = sVar;
    }

    public void run() {
        m a2 = m.a(this.f5769a.f5772c);
        List<String> c2 = this.f5769a.i != null ? this.f5769a.i.c() : null;
        if (c2 != null) {
            u.PICTURE.a().f5755c = a2.d(c2);
            u.AUDIO.a().f5755c = a2.b(c2);
            u.VIDEO.a().f5755c = a2.e(c2);
            u.APK.a().f5755c = a2.a(c2);
            u.DOC.a().f5755c = a2.c(c2);
            Message.obtain(this.f5769a.h, 4).sendToTarget();
        }
    }
}
