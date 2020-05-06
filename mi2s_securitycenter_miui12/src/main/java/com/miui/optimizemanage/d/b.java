package com.miui.optimizemanage.d;

import android.animation.ValueAnimator;
import com.miui.optimizemanage.d.c;

class b implements ValueAnimator.AnimatorUpdateListener {
    b() {
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        Float f = (Float) valueAnimator.getAnimatedValue();
        for (c.a a2 : c.f5929c) {
            a2.a(f.floatValue());
        }
    }
}
