package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class S implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4154a;

    S(W w) {
        this.f4154a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4154a.e.setAlpha((float) ((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
