package com.miui.permcenter.privacymanager.behaviorrecord;

import android.view.View;
import com.miui.permcenter.b.c;
import com.miui.permcenter.privacymanager.a.a;

class f implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6444a;

    f(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6444a = appBehaviorRecordActivity;
    }

    public void a(View view) {
        if (view != null && (view.getTag() instanceof Integer)) {
            try {
                a aVar = (a) this.f6444a.f.get(((Integer) view.getTag()).intValue());
                if (aVar != null) {
                    this.f6444a.startActivity(PrivacyDetailActivity.a(aVar.f(), aVar.l(), "all_record"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
