package com.miui.gamebooster.a;

class r implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f4059a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ v f4060b;

    r(v vVar, long j) {
        this.f4060b = vVar;
        this.f4059a = j;
    }

    public void run() {
        this.f4060b.e.postDelayed(new q(this), this.f4059a >> 1);
    }
}
