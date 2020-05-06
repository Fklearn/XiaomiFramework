package com.miui.securityscan.scanner;

import com.miui.securityscan.b.g;
import com.miui.securityscan.b.n;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class I implements g {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n f7837a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f7838b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ O f7839c;

    I(O o, n nVar, boolean z) {
        this.f7839c = o;
        this.f7837a = nVar;
        this.f7838b = z;
    }

    public void a() {
        Log.d("SecurityManager", "startScanManualItem =============> onInterrupted");
        n nVar = this.f7837a;
        if (nVar != null) {
            nVar.b();
        }
    }

    public void a(int i, int i2, Object obj) {
        if (this.f7839c.f7850b) {
            throw new InterruptedException();
        } else if (obj != null && (obj instanceof String)) {
            String str = (String) obj;
            if (this.f7838b) {
                this.f7839c.m.a(v.PREDICT_MANUAL_ITEM, new C0558e(i, i2, str));
            }
        }
    }

    public void a(List<GroupModel> list, int i) {
        Log.d("SecurityManager", "startScanManualItem =============> onFinishScan");
        if (list != null) {
            this.f7839c.h.a(list);
        }
        try {
            if (this.f7838b) {
                this.f7839c.m.a(v.PREDICT_MANUAL_ITEM, new C0558e(O.f.FINISH));
            }
        } catch (InterruptedException unused) {
            n nVar = this.f7837a;
            if (nVar != null) {
                nVar.b();
            }
        }
        n nVar2 = this.f7837a;
        if (nVar2 != null) {
            nVar2.c();
        }
    }

    public void b() {
        Log.d("SecurityManager", "startScanManualItem -------------> onStartScan");
        n nVar = this.f7837a;
        if (nVar != null) {
            nVar.a();
        }
    }
}
