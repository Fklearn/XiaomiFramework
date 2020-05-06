package com.miui.optimizemanage;

import android.animation.Animator;

class i implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f5937a;

    i(m mVar) {
        this.f5937a = mVar;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f5937a.f.a();
        if (this.f5937a.s <= 0) {
            this.f5937a.g.a();
        }
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
