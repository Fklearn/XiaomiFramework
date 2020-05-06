package com.miui.firstaidkit;

import com.miui.firstaidkit.a.a;
import com.miui.firstaidkit.j;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0558e;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class f implements a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j.b f3935a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f3936b;

    f(j jVar, j.b bVar) {
        this.f3936b = jVar;
        this.f3935a = bVar;
    }

    public void a(int i, int i2, String str) {
        if (!this.f3936b.f3944b) {
            this.f3936b.e.a(n.INTERNET, new C0558e(i, i2, str));
            return;
        }
        throw new InterruptedException();
    }

    public void a(List<AbsModel> list, int i, int i2) {
        if (list != null) {
            try {
                this.f3936b.h.b(list);
            } catch (Exception e) {
                Log.e("FirstAidKitManager", "startScanInternet", e);
                return;
            }
        }
        int unused = this.f3936b.i = this.f3936b.i + i2;
        this.f3936b.e.a(n.INTERNET, new C0558e(O.f.FINISH, i2));
        if (this.f3935a != null) {
            this.f3935a.a(n.INTERNET);
        }
    }

    public void b() {
    }
}
