package com.miui.permcenter.settings;

import com.miui.appmanager.AppManageUtils;
import com.miui.permcenter.settings.t;
import java.util.Iterator;
import java.util.List;

class o implements t.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f6557a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ t f6558b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ PrivacyMonitorOpenActivity f6559c;

    o(PrivacyMonitorOpenActivity privacyMonitorOpenActivity, List list, t tVar) {
        this.f6559c = privacyMonitorOpenActivity;
        this.f6557a = list;
        this.f6558b = tVar;
    }

    public void a(k kVar, int i) {
        AppManageUtils.a(this.f6559c.f6507b, kVar.b(), kVar.c());
        Iterator it = this.f6557a.iterator();
        while (it.hasNext()) {
            if (((k) it.next()).a(kVar)) {
                it.remove();
            }
        }
        this.f6558b.notifyDataSetChanged();
        if (this.f6557a.size() == 0) {
            this.f6559c.f6506a.dismiss();
        }
    }

    public void b(k kVar, int i) {
        this.f6559c.a(kVar);
    }
}
