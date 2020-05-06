package com.miui.powercenter;

import android.animation.ValueAnimator;
import android.content.Context;
import com.miui.powercenter.PowerMainActivity;
import com.miui.powercenter.utils.s;

class l implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerMainActivity f7075a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PowerMainActivity.a f7076b;

    l(PowerMainActivity.a aVar, PowerMainActivity powerMainActivity) {
        this.f7076b = aVar;
        this.f7075a = powerMainActivity;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.powercenter.PowerMainActivity] */
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        String unused = this.f7076b.f = s.b((Context) this.f7075a, (long) ((Float) valueAnimator.getAnimatedValue()).floatValue());
        this.f7075a.j.setText(this.f7076b.f);
    }
}
