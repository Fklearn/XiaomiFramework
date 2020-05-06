package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;

class m implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7135a;

    m(MainBatteryView mainBatteryView) {
        this.f7135a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float unused = this.f7135a.u = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.f7135a.invalidate();
    }
}
