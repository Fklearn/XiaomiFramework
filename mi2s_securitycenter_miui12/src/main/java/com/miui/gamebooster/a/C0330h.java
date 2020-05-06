package com.miui.gamebooster.a;

/* renamed from: com.miui.gamebooster.a.h  reason: case insensitive filesystem */
class C0330h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f4044a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ v f4045b;

    C0330h(v vVar, long j) {
        this.f4045b = vVar;
        this.f4044a = j;
    }

    public void run() {
        this.f4045b.e.postDelayed(new C0329g(this), this.f4044a >> 1);
        this.f4045b.g.animate().alpha(0.0f).setDuration(this.f4044a).start();
    }
}
