package com.miui.appmanager;

import android.util.Log;
import b.b.c.j.C;
import com.miui.appmanager.ApplicationsDetailsActivity;
import com.miui.hybrid.accessory.sdk.HybridAccessoryClient;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.c;
import java.util.ArrayList;
import java.util.Map;

class A implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ApplicationsDetailsActivity f3479a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3480b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ApplicationsDetailsActivity.u f3481c;

    A(ApplicationsDetailsActivity.u uVar, ApplicationsDetailsActivity applicationsDetailsActivity, String str) {
        this.f3481c = uVar;
        this.f3479a = applicationsDetailsActivity;
        this.f3480b = str;
    }

    public void run() {
        if (!this.f3479a.isFinishing()) {
            this.f3479a.v.setEnabled(false);
            this.f3479a.u.setEnabled(false);
            c.a(this.f3481c.f3557a, (int) R.string.uninstall_app_done);
            if (h.i() && !C.b(this.f3479a.P)) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.f3480b);
                try {
                    HybridAccessoryClient.showCreateIconDialog(this.f3481c.f3557a, arrayList, (Map<String, String>) null);
                } catch (Exception e) {
                    Log.e("ApplicationsDetailActivity", "hybrid sdk showCreateIconDialog error", e);
                }
            }
            this.f3479a.finish();
        }
    }
}
