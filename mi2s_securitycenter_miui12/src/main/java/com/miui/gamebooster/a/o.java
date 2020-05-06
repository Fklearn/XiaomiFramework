package com.miui.gamebooster.a;

import android.view.View;

class o implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ float f4053a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f4054b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ v f4055c;

    o(v vVar, float f, View view) {
        this.f4055c = vVar;
        this.f4053a = f;
        this.f4054b = view;
    }

    public void run() {
        long abs = Math.abs((long) ((this.f4053a / this.f4055c.o) * 1500.0f));
        v vVar = this.f4055c;
        vVar.a(vVar.o, abs, this.f4054b);
        if (this.f4053a < 0.0f) {
            this.f4055c.g.animate().alpha(0.0f).setDuration(abs).withEndAction(new n(this)).start();
        }
    }
}
