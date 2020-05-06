package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;
import android.widget.LinearLayout;

class C implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4116a;

    C(W w) {
        this.f4116a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f4116a.i.getLayoutParams();
        layoutParams.bottomMargin = intValue;
        this.f4116a.i.setLayoutParams(layoutParams);
    }
}
