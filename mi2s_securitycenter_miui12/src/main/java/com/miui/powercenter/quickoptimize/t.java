package com.miui.powercenter.quickoptimize;

import android.content.Context;
import com.miui.powercenter.quickoptimize.v;

class t implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7259a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ v.a f7260b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ v f7261c;

    t(v vVar, Context context, v.a aVar) {
        this.f7261c = vVar;
        this.f7259a = context;
        this.f7260b = aVar;
    }

    public void run() {
        boolean unused = this.f7261c.g = true;
        this.f7261c.l();
        this.f7261c.b(this.f7259a, this.f7260b);
        this.f7261c.a(this.f7260b, this.f7259a);
    }
}
