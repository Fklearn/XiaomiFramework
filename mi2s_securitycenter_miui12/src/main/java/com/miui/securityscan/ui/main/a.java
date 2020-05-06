package com.miui.securityscan.ui.main;

import android.animation.ValueAnimator;

class a implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainVideoView f8010a;

    a(MainVideoView mainVideoView) {
        this.f8010a = mainVideoView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f8010a.f7995a.setRenderState(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
