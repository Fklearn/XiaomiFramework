package com.miui.antivirus.result;

import b.b.b.a.b;
import com.miui.antivirus.activity.MainActivity;

class p implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2847a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ q f2848b;

    p(q qVar, MainActivity mainActivity) {
        this.f2848b = qVar;
        this.f2847a = mainActivity;
    }

    public void run() {
        this.f2847a.a((C0238a) this.f2848b);
        b.a.d();
    }
}
