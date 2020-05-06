package com.miui.securityscan.scanner;

import b.b.b.b;
import com.miui.antivirus.model.k;
import com.miui.securityscan.b.d;
import com.miui.securityscan.b.g;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class C implements g {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7818a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d f7819b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ O.c f7820c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ O.e f7821d;
    final /* synthetic */ O e;

    C(O o, boolean z, d dVar, O.c cVar, O.e eVar) {
        this.e = o;
        this.f7818a = z;
        this.f7819b = dVar;
        this.f7820c = cVar;
        this.f7821d = eVar;
    }

    public void a() {
        O.e eVar = this.f7821d;
        if (eVar != null) {
            eVar.a();
        }
    }

    public void a(int i, int i2, Object obj) {
        if (this.e.f7850b) {
            throw new InterruptedException();
        } else if (obj != null && (obj instanceof k)) {
            this.e.h.b(i2);
            k kVar = (k) obj;
            if (kVar.d() != b.c.SAFE) {
                this.e.h.a(kVar);
            }
            C0558e eVar = new C0558e(i, i2, kVar.a());
            if (this.f7818a) {
                this.e.m.a(v.PREDICT_SYSTEM_APP, eVar);
            } else {
                this.e.n.a(C0568o.SYSTEM_APP, eVar);
            }
        }
    }

    public void a(List<GroupModel> list, int i) {
        Log.d("SecurityManager", "startScanSystemApps =============> onFinishScan");
        this.e.l.post(new B(this, i));
    }

    public void b() {
        Log.d("SecurityManager", "startScanSystemApps -------------> onStartScan ");
    }
}
