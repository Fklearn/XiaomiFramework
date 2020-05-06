package com.miui.monthreport;

import java.util.List;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f5615a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ b f5616b;

    a(b bVar, List list) {
        this.f5616b = bVar;
        this.f5615a = list;
    }

    public void run() {
        this.f5616b.b(this.f5615a);
    }
}
