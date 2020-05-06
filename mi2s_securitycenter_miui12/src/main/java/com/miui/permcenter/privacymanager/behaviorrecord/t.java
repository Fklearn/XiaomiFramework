package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.Activity;
import android.view.View;
import com.miui.permcenter.b.c;
import com.miui.permcenter.privacymanager.a.a;

class t implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6462a;

    t(PrivacyDetailActivity privacyDetailActivity) {
        this.f6462a = privacyDetailActivity;
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    /* JADX WARNING: type inference failed for: r0v9, types: [android.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    public void a(View view) {
        if (view != null && (view.getTag() instanceof Long)) {
            try {
                a aVar = new a(this.f6462a, this.f6462a.F, this.f6462a.F, (String) null, ((Long) view.getTag()).longValue(), 0, 1, (String) null, (String) null, 1, this.f6462a.H, 1);
                if (this.f6462a.aa.containsKey(Long.valueOf(aVar.i()))) {
                    o.a((Activity) this.f6462a, aVar, ((Integer) this.f6462a.aa.get(Long.valueOf(aVar.i()))).intValue(), (String) this.f6462a.Z.get(Long.valueOf(aVar.i())), this.f6462a.ja);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
