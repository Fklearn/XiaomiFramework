package com.miui.securityscan.i;

import android.animation.Animator;
import android.view.View;

class s implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f7747a;

    s(View view) {
        this.f7747a = view;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f7747a.setClickable(true);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
