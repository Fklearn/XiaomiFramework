package com.miui.securityscan.i;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import com.miui.maml.folme.AnimatedProperty;

class u implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f7749a;

    u(View view) {
        this.f7749a = view;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f7749a.setVisibility(8);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.f7749a, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.f7749a, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f});
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(0);
        animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
        animatorSet.start();
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
