package com.miui.powercenter;

import android.animation.ValueAnimator;
import com.miui.powercenter.PowerMainActivity;
import com.miui.powercenter.utils.u;

class m implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerMainActivity.b f7102a;

    m(PowerMainActivity.b bVar) {
        this.f7102a = bVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        PowerMainActivity.this.k.setText(u.a(((Integer) valueAnimator.getAnimatedValue()).intValue()));
    }
}
