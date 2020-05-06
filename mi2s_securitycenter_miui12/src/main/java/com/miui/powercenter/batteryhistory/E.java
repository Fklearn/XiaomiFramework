package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;

class E implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ float f6826a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ float f6827b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ float f6828c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ float f6829d;
    final /* synthetic */ BatteryLevelChart.a e;

    E(BatteryLevelChart.a aVar, float f, float f2, float f3, float f4) {
        this.e = aVar;
        this.f6826a = f;
        this.f6827b = f2;
        this.f6828c = f3;
        this.f6829d = f4;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        BatteryLevelChart.a aVar = this.e;
        float f = this.f6826a;
        float f2 = f + ((this.f6827b - f) * floatValue);
        float f3 = this.f6828c;
        aVar.a(f2, f3 - ((f3 - this.f6829d) * floatValue));
        this.e.postInvalidate();
    }
}
