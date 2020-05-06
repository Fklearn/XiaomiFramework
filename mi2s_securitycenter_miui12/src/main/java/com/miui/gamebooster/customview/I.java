package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class I implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4135a;

    I(W w) {
        this.f4135a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4135a.s.setAlpha((float) ((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
