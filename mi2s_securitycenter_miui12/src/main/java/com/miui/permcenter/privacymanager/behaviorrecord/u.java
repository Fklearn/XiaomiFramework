package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.Activity;
import android.view.View;
import com.miui.permcenter.b.c;
import com.miui.permcenter.privacymanager.a.a;
import com.miui.permcenter.privacymanager.a.b;

class u implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6463a;

    u(PrivacyDetailActivity privacyDetailActivity) {
        this.f6463a = privacyDetailActivity;
    }

    /* JADX WARNING: type inference failed for: r6v10, types: [android.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    public void a(View view) {
        if (view != null && (view.getTag() instanceof Integer)) {
            try {
                int intValue = ((Integer) view.getTag()).intValue();
                a aVar = (a) this.f6463a.A.get(intValue);
                if (aVar == null) {
                    return;
                }
                if (aVar.b(b.f6330b) && aVar.k() == null) {
                    return;
                }
                if (aVar.m() && !aVar.b(b.f6329a)) {
                    return;
                }
                if (aVar.k() != null) {
                    this.f6463a.A.addAll(intValue, aVar.k());
                    aVar.a();
                    this.f6463a.z();
                } else if (this.f6463a.aa != null && this.f6463a.aa.containsKey(Long.valueOf(aVar.i()))) {
                    o.a((Activity) this.f6463a, aVar, ((Integer) this.f6463a.aa.get(Long.valueOf(aVar.i()))).intValue(), (String) this.f6463a.Z.get(Long.valueOf(aVar.i())), this.f6463a.ja);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
