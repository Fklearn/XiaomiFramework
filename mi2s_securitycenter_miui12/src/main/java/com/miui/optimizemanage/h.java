package com.miui.optimizemanage;

import android.animation.Animator;

class h implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f5936a;

    h(m mVar) {
        this.f5936a = mVar;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f5936a.e.a();
        if (this.f5936a.r <= 0) {
            this.f5936a.f.a();
        }
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
