package com.miui.securityscan.scanner;

import com.miui.antivirus.model.k;
import java.util.List;

class N implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f7847a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ O f7848b;

    N(O o, List list) {
        this.f7848b = o;
        this.f7847a = list;
    }

    public void run() {
        this.f7848b.f7852d.a((List<k>) this.f7847a);
    }
}
