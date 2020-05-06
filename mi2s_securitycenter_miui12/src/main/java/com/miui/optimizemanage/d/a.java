package com.miui.optimizemanage.d;

import android.animation.ValueAnimator;
import com.miui.optimizemanage.d.c;

class a implements ValueAnimator.AnimatorUpdateListener {
    a() {
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        Float f = (Float) valueAnimator.getAnimatedValue();
        for (c.b a2 : c.f5930d) {
            a2.a(f.floatValue());
        }
    }
}
