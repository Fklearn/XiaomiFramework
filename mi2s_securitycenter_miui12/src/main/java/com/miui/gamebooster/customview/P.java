package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class P implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4146a;

    P(W w) {
        this.f4146a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4146a.q.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue() / 100.0f);
    }
}
