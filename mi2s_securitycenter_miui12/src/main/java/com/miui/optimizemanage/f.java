package com.miui.optimizemanage;

import android.animation.Animator;
import android.os.Handler;

class f implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f5934a;

    f(m mVar) {
        this.f5934a = mVar;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        new Handler().postDelayed(new e(this), 100);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
