package com.miui.securityscan.b;

import com.miui.securityscan.L;
import com.miui.securityscan.M;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7601a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d f7602b;

    a(d dVar, L l) {
        this.f7602b = dVar;
        this.f7601a = l;
    }

    public void run() {
        M.d(System.currentTimeMillis());
        this.f7601a.q();
    }
}
