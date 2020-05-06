package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;
import android.widget.LinearLayout;

class V implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4157a;

    V(W w) {
        this.f4157a = w;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f4157a.s.getLayoutParams();
        layoutParams.height = intValue;
        this.f4157a.s.setLayoutParams(layoutParams);
    }
}
