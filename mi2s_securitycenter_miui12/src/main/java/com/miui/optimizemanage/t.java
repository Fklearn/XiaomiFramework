package com.miui.optimizemanage;

import android.animation.ValueAnimator;

class t implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f6004a;

    t(v vVar) {
        this.f6004a = vVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.f6004a.f6008c.setAlpha(floatValue);
        this.f6004a.f6007b.setAlpha(floatValue);
    }
}
