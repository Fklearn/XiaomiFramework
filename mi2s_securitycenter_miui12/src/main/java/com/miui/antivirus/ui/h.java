package com.miui.antivirus.ui;

import android.animation.ValueAnimator;

class h implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivityView f2965a;

    h(MainActivityView mainActivityView) {
        this.f2965a = mainActivityView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f2965a.f2924c.a(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
