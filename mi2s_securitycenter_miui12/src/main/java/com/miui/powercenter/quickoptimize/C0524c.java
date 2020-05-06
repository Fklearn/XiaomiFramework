package com.miui.powercenter.quickoptimize;

import android.animation.Animator;

/* renamed from: com.miui.powercenter.quickoptimize.c  reason: case insensitive filesystem */
class C0524c implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainContentFrame f7215a;

    C0524c(MainContentFrame mainContentFrame) {
        this.f7215a = mainContentFrame;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f7215a.n.setVisibility(8);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
