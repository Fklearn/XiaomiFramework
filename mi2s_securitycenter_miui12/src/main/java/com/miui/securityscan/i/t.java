package com.miui.securityscan.i;

import android.animation.Animator;
import android.view.View;

class t implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f7748a;

    t(View view) {
        this.f7748a = view;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f7748a.setVisibility(8);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
