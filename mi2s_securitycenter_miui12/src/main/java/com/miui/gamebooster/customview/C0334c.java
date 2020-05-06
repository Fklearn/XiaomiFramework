package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

/* renamed from: com.miui.gamebooster.customview.c  reason: case insensitive filesystem */
class C0334c implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AuditionView f4189a;

    C0334c(AuditionView auditionView) {
        this.f4189a = auditionView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4189a.m.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
        this.f4189a.f4106d.setImageDrawable(this.f4189a.m);
    }
}
