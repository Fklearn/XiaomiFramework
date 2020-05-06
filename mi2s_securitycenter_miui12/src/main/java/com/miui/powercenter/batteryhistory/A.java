package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;

class A implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelChart.a f6781a;

    A(BatteryLevelChart.a aVar) {
        this.f6781a = aVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float unused = this.f6781a.aa = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.f6781a.postInvalidate();
    }
}
