package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;

class X implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VoiceModeView f4166a;

    X(VoiceModeView voiceModeView) {
        this.f4166a = voiceModeView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4166a.f4161d.b(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
