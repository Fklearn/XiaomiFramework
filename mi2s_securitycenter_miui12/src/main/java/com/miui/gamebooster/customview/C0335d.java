package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

/* renamed from: com.miui.gamebooster.customview.d  reason: case insensitive filesystem */
class C0335d implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AuditionView f4190a;

    C0335d(AuditionView auditionView) {
        this.f4190a = auditionView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4190a.m.a(((Float) valueAnimator.getAnimatedValue()).floatValue());
        this.f4190a.f4106d.setImageDrawable(this.f4190a.m);
    }
}
