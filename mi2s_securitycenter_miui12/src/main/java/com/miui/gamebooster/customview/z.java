package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class z implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4245a;

    z(W w) {
        this.f4245a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4245a.s.setAlpha((float) ((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
