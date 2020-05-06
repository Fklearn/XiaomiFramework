package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;

class K implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ float f6838a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ float f6839b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ float f6840c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ float f6841d;
    final /* synthetic */ BatteryLevelHistogram e;

    K(BatteryLevelHistogram batteryLevelHistogram, float f, float f2, float f3, float f4) {
        this.e = batteryLevelHistogram;
        this.f6838a = f;
        this.f6839b = f2;
        this.f6840c = f3;
        this.f6841d = f4;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float unused = this.e.k = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.e.a(((this.f6838a - this.f6839b) * this.e.k) + this.f6839b, ((this.f6840c - this.f6841d) * this.e.k) + this.f6841d);
        this.e.f6813b.postInvalidate();
    }
}
