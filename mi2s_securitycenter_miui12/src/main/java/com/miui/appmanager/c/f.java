package com.miui.appmanager.c;

import com.miui.applicationlock.c.y;
import com.miui.appmanager.AppManagerMainActivity;
import com.xiaomi.ad.feedback.IAdFeedbackListener;

class f extends IAdFeedbackListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3634a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ i f3635b;

    f(i iVar, AppManagerMainActivity appManagerMainActivity) {
        this.f3635b = iVar;
        this.f3634a = appManagerMainActivity;
    }

    public void onFinished(int i) {
        if (i > 0) {
            this.f3635b.a(this.f3634a);
        }
        y.b().b(this.f3634a.getApplicationContext());
    }
}
