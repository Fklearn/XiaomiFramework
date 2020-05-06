package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

/* renamed from: com.miui.gamebooster.customview.b  reason: case insensitive filesystem */
class C0333b implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AuditionView f4176a;

    C0333b(AuditionView auditionView) {
        this.f4176a = auditionView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4176a.f4105c.setVoice((double) (((Float) valueAnimator.getAnimatedValue()).floatValue() / 9.0f));
    }
}
