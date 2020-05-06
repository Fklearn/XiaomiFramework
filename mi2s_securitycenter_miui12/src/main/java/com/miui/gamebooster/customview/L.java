package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;
import android.widget.LinearLayout;

class L implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4142a;

    L(W w) {
        this.f4142a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f4142a.i.getLayoutParams();
        layoutParams.topMargin = intValue;
        this.f4142a.i.setLayoutParams(layoutParams);
    }
}
