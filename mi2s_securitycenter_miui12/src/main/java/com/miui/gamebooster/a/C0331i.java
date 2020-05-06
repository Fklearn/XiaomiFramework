package com.miui.gamebooster.a;

/* renamed from: com.miui.gamebooster.a.i  reason: case insensitive filesystem */
class C0331i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f4046a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f4047b;

    C0331i(j jVar, int i) {
        this.f4047b = jVar;
        this.f4046a = i;
    }

    public void run() {
        this.f4047b.f4048a.e.setAlpha(0.0f);
        this.f4047b.f4048a.e.setVisibility(0);
        this.f4047b.f4048a.e.animate().alpha(1.0f).translationYBy(this.f4047b.f4048a.m - ((float) this.f4046a)).setDuration(300).start();
    }
}
