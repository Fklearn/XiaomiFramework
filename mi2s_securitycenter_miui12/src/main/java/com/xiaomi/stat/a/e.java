package com.xiaomi.stat.a;

import java.util.concurrent.Callable;

class e implements Callable<k> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b[] f8373a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f8374b;

    e(c cVar, b[] bVarArr) {
        this.f8374b = cVar;
        this.f8373a = bVarArr;
    }

    /* renamed from: a */
    public k call() {
        return this.f8374b.b(this.f8373a);
    }
}
