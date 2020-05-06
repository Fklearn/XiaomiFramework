package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import com.miui.powercenter.batteryhistory.BatteryLevelHistogram;

class Q implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram.b f6848a;

    Q(BatteryLevelHistogram.b bVar) {
        this.f6848a = bVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float unused = this.f6848a.H = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        BatteryLevelHistogram.this.f6813b.postInvalidate();
    }
}
