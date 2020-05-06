package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;

class C implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelChart.a f6824a;

    C(BatteryLevelChart.a aVar) {
        this.f6824a = aVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        BatteryLevelChart.a aVar = this.f6824a;
        aVar.a(floatValue, (aVar.J[1] + floatValue) - this.f6824a.J[0]);
        this.f6824a.postInvalidate();
    }
}
