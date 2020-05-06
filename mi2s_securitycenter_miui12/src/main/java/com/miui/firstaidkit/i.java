package com.miui.firstaidkit;

import com.miui.firstaidkit.a.a;
import com.miui.firstaidkit.j;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0558e;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class i implements a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j.b f3941a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f3942b;

    i(j jVar, j.b bVar) {
        this.f3942b = jVar;
        this.f3941a = bVar;
    }

    public void a(int i, int i2, String str) {
        if (!this.f3942b.f3944b) {
            this.f3942b.e.a(n.OTHER, new C0558e(i, i2, str));
            return;
        }
        throw new InterruptedException();
    }

    public void a(List<AbsModel> list, int i, int i2) {
        if (list != null) {
            try {
                this.f3942b.h.d(list);
            } catch (Exception e) {
                Log.e("FirstAidKitManager", "startScanOther", e);
                return;
            }
        }
        int unused = this.f3942b.i = this.f3942b.i + i2;
        this.f3942b.e.a(n.OTHER, new C0558e(O.f.FINISH, i2));
        if (this.f3941a != null) {
            this.f3941a.a(n.OTHER);
        }
    }

    public void b() {
    }
}
