package com.miui.securityscan.b;

import com.miui.securityscan.L;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7613a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f7614b;

    i(j jVar, L l) {
        this.f7614b = jVar;
        this.f7613a = l;
    }

    public void run() {
        L l = this.f7613a;
        l.f7559d = true;
        if (!l.va || l.ua) {
            L l2 = this.f7613a;
            l2.Ja.add(Integer.valueOf(l2.l.j()));
            return;
        }
        l.g();
    }
}
