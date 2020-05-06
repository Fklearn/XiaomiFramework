package com.miui.firstaidkit;

import com.miui.firstaidkit.a.a;
import com.miui.firstaidkit.j;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0558e;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class e implements a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j.b f3933a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f3934b;

    e(j jVar, j.b bVar) {
        this.f3934b = jVar;
        this.f3933a = bVar;
    }

    public void a(int i, int i2, String str) {
        if (!this.f3934b.f3944b) {
            this.f3934b.e.a(n.PERFORMANCE, new C0558e(i, i2, str));
            return;
        }
        throw new InterruptedException();
    }

    public void a(List<AbsModel> list, int i, int i2) {
        if (list != null) {
            try {
                this.f3934b.h.e(list);
            } catch (Exception e) {
                Log.e("FirstAidKitManager", "startScanPerformance", e);
                return;
            }
        }
        int unused = this.f3934b.i = this.f3934b.i + i2;
        this.f3934b.e.a(n.PERFORMANCE, new C0558e(O.f.FINISH, i2));
        if (this.f3933a != null) {
            this.f3933a.a(n.PERFORMANCE);
        }
    }

    public void b() {
    }
}
