package com.xiaomi.stat.a;

import java.util.ArrayList;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f8375a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f8376b;

    f(c cVar, ArrayList arrayList) {
        this.f8376b = cVar;
        this.f8375a = arrayList;
    }

    public void run() {
        this.f8376b.b((ArrayList<Long>) this.f8375a);
    }
}
