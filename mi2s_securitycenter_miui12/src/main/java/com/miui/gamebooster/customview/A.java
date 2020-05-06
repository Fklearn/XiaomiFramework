package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;
import android.widget.LinearLayout;

class A implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4101a;

    A(W w) {
        this.f4101a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f4101a.j.getLayoutParams();
        layoutParams.topMargin = intValue;
        this.f4101a.j.setLayoutParams(layoutParams);
    }
}
