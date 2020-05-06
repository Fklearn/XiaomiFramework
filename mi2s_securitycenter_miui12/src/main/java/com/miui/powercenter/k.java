package com.miui.powercenter;

import android.animation.ValueAnimator;
import android.content.Context;
import com.miui.powercenter.PowerMainActivity;
import com.miui.powercenter.utils.s;

class k implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerMainActivity f7073a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PowerMainActivity.a f7074b;

    k(PowerMainActivity.a aVar, PowerMainActivity powerMainActivity) {
        this.f7074b = aVar;
        this.f7073a = powerMainActivity;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.powercenter.PowerMainActivity] */
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        String unused = this.f7074b.f = s.a((Context) this.f7073a, (long) ((Float) valueAnimator.getAnimatedValue()).floatValue());
        this.f7073a.j.setText(this.f7074b.f);
    }
}
