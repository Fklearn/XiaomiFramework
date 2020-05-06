package com.miui.powercenter.quickoptimize;

import android.animation.Animator;

/* renamed from: com.miui.powercenter.quickoptimize.b  reason: case insensitive filesystem */
class C0523b implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7212a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f7213b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ MainContentFrame f7214c;

    C0523b(MainContentFrame mainContentFrame, boolean z, int i) {
        this.f7214c = mainContentFrame;
        this.f7212a = z;
        this.f7213b = i;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f7214c.g.setVisibility(4);
        this.f7214c.h.setVisibility(8);
        this.f7214c.l.setVisibility(8);
        this.f7214c.b(this.f7212a, this.f7213b);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
