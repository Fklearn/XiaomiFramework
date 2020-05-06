package com.miui.permcenter.privacymanager.b;

import android.animation.ValueAnimator;

class i implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ValueAnimator f6363a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ m f6364b;

    i(m mVar, ValueAnimator valueAnimator) {
        this.f6364b = mVar;
        this.f6363a = valueAnimator;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float unused = this.f6364b.q = ((Float) this.f6363a.getAnimatedValue()).floatValue();
        this.f6364b.postInvalidate();
    }
}
