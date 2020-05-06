package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class E implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4121a;

    E(W w) {
        this.f4121a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4121a.e.setAlpha((float) ((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
