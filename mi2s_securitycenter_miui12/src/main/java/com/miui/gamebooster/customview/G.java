package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class G implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4124a;

    G(W w) {
        this.f4124a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4124a.r.setAlpha((float) ((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
