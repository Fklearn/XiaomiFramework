package com.miui.firstaidkit;

import com.miui.firstaidkit.a.a;
import com.miui.firstaidkit.j;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0558e;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class g implements a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j.b f3937a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f3938b;

    g(j jVar, j.b bVar) {
        this.f3938b = jVar;
        this.f3937a = bVar;
    }

    public void a(int i, int i2, String str) {
        if (!this.f3938b.f3944b) {
            this.f3938b.e.a(n.OPERATION, new C0558e(i, i2, str));
            return;
        }
        throw new InterruptedException();
    }

    public void a(List<AbsModel> list, int i, int i2) {
        if (list != null) {
            try {
                this.f3938b.h.c(list);
            } catch (Exception e) {
                Log.e("FirstAidKitManager", "startScanOperation", e);
                return;
            }
        }
        int unused = this.f3938b.i = this.f3938b.i + i2;
        this.f3938b.e.a(n.OPERATION, new C0558e(O.f.FINISH, i2));
        if (this.f3937a != null) {
            this.f3937a.a(n.OPERATION);
        }
    }

    public void b() {
    }
}
