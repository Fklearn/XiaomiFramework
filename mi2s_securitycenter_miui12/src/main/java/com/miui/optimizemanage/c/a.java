package com.miui.optimizemanage.c;

import android.content.Context;
import com.miui.applicationlock.c.y;
import com.miui.optimizemanage.OptimizemanageMainActivity;
import com.miui.securitycenter.Application;
import com.xiaomi.ad.feedback.IAdFeedbackListener;

class a extends IAdFeedbackListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ OptimizemanageMainActivity f5882a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f5883b;

    a(c cVar, OptimizemanageMainActivity optimizemanageMainActivity) {
        this.f5883b = cVar;
        this.f5882a = optimizemanageMainActivity;
    }

    public void onFinished(int i) {
        if (i > 0) {
            this.f5883b.a(this.f5882a);
        }
        y.b().b((Context) Application.d());
    }
}
