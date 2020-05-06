package com.miui.securityscan.scanner;

import com.miui.securityscan.b.g;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class A implements g {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7813a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ O.e f7814b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ O f7815c;

    A(O o, boolean z, O.e eVar) {
        this.f7815c = o;
        this.f7813a = z;
        this.f7814b = eVar;
    }

    public void a() {
        Log.d("SecurityManager", "startScanAutoItem onInterrupted()  ");
        O.e eVar = this.f7814b;
        if (eVar != null) {
            eVar.a();
        }
    }

    public void a(int i, int i2, Object obj) {
        if (this.f7815c.f7850b) {
            throw new InterruptedException();
        } else if (obj != null && (obj instanceof String)) {
            String str = (String) obj;
            if (this.f7813a) {
                this.f7815c.m.a(v.PREDICT_AUTO_ITEM, new C0558e(i, i2, str));
            } else {
                this.f7815c.n.a(C0568o.SYSTEM_CONFIG, new C0558e(i, i2, str));
            }
        }
    }

    public void a(List<GroupModel> list, int i) {
        Log.d("SecurityManager", "startScanAutoItem =============> onFinishScan");
        this.f7815c.l.post(new y(this, i, list));
    }

    public void b() {
        Log.d("SecurityManager", "startScanAutoItem -------------> onStartScan");
    }
}
