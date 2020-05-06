package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class U implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4156a;

    U(W w) {
        this.f4156a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4156a.r.setAlpha((float) ((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
