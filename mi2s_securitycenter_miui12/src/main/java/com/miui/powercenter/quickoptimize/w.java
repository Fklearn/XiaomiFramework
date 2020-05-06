package com.miui.powercenter.quickoptimize;

import java.util.List;

class w implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f7269a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ x f7270b;

    w(x xVar, List list) {
        this.f7270b = xVar;
        this.f7269a = list;
    }

    public void run() {
        this.f7270b.f7271a.a(this.f7269a);
    }
}
