package com.miui.firstaidkit;

import com.miui.firstaidkit.a.a;
import com.miui.firstaidkit.j;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0558e;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class h implements a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j.b f3939a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f3940b;

    h(j jVar, j.b bVar) {
        this.f3940b = jVar;
        this.f3939a = bVar;
    }

    public void a(int i, int i2, String str) {
        if (!this.f3940b.f3944b) {
            this.f3940b.e.a(n.CONSUME_POWER, new C0558e(i, i2, str));
            return;
        }
        throw new InterruptedException();
    }

    public void a(List<AbsModel> list, int i, int i2) {
        if (list != null) {
            try {
                this.f3940b.h.a(list);
            } catch (Exception e) {
                Log.e("FirstAidKitManager", "startScanConsumePower", e);
                return;
            }
        }
        int unused = this.f3940b.i = this.f3940b.i + i2;
        this.f3940b.e.a(n.CONSUME_POWER, new C0558e(O.f.FINISH, i2));
        if (this.f3939a != null) {
            this.f3939a.a(n.CONSUME_POWER);
        }
    }

    public void b() {
    }
}
