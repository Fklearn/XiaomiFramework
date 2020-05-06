package com.miui.securityscan.scanner;

import com.miui.securityscan.b.d;
import com.miui.securityscan.b.f;
import miui.util.Log;

class K implements f {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f7841a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ O f7842b;

    K(O o, d dVar) {
        this.f7842b = o;
        this.f7841a = dVar;
    }

    public void a() {
        Log.d("SecurityManager", "startOptimizeSystemAppAfterScanSystem onFinishOptimize() callback");
        this.f7842b.l.post(new J(this));
    }
}
