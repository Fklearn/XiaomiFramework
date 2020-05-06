package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;
import android.widget.LinearLayout;

class B implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4115a;

    B(W w) {
        this.f4115a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f4115a.i.getLayoutParams();
        layoutParams.topMargin = intValue;
        this.f4115a.i.setLayoutParams(layoutParams);
    }
}
