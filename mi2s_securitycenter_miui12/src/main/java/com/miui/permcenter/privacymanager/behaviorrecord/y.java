package com.miui.permcenter.privacymanager.behaviorrecord;

import android.util.Log;
import b.b.c.j.C;
import com.miui.hybrid.accessory.sdk.HybridAccessoryClient;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.c;
import java.util.ArrayList;
import java.util.Map;

class y implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6467a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f6468b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity.h f6469c;

    y(PrivacyDetailActivity.h hVar, PrivacyDetailActivity privacyDetailActivity, String str) {
        this.f6469c = hVar;
        this.f6467a = privacyDetailActivity;
        this.f6468b = str;
    }

    public void run() {
        if (!this.f6467a.isFinishing()) {
            this.f6467a.h.setEnabled(false);
            this.f6467a.g.setEnabled(false);
            c.a(this.f6469c.f6413a, (int) R.string.uninstall_app_done);
            if (h.i() && !C.b(this.f6467a.H)) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.f6468b);
                try {
                    HybridAccessoryClient.showCreateIconDialog(this.f6469c.f6413a, arrayList, (Map<String, String>) null);
                } catch (Exception e) {
                    Log.e("BehaviorRecord-SINGLE", "hybrid sdk showCreateIconDialog error", e);
                }
            }
            this.f6467a.finish();
        }
    }
}
