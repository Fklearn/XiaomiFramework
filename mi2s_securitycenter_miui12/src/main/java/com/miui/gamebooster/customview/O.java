package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class O implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4145a;

    O(W w) {
        this.f4145a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (!this.f4145a.w) {
            this.f4145a.r.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }
}
