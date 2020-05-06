package com.miui.antivirus.ui;

import android.animation.Animator;

class g implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivityView f2964a;

    g(MainActivityView mainActivityView) {
        this.f2964a = mainActivityView;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f2964a.f2925d.setVisibility(4);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
        this.f2964a.f.sendEmptyMessage(1024);
    }
}
