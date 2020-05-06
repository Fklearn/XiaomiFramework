package com.xiaomi.stat.a;

import com.xiaomi.stat.d.k;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l f8371a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f8372b;

    d(c cVar, l lVar) {
        this.f8372b = cVar;
        this.f8371a = lVar;
    }

    public void run() {
        try {
            this.f8372b.b(this.f8371a);
        } catch (Exception e) {
            k.e("EventManager", "addEvent exception: " + e.toString());
        }
    }
}
