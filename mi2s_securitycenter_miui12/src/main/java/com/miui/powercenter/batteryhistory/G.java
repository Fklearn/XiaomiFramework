package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;

class G implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ float f6831a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ float f6832b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ float f6833c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ float f6834d;
    final /* synthetic */ BatteryLevelChart.a e;

    G(BatteryLevelChart.a aVar, float f, float f2, float f3, float f4) {
        this.e = aVar;
        this.f6831a = f;
        this.f6832b = f2;
        this.f6833c = f3;
        this.f6834d = f4;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        BatteryLevelChart.a aVar = this.e;
        float f = this.f6831a;
        float f2 = f - ((f - this.f6832b) * floatValue);
        float f3 = this.f6833c;
        aVar.a(f2, f3 + ((this.f6834d - f3) * floatValue));
        this.e.postInvalidate();
    }
}
