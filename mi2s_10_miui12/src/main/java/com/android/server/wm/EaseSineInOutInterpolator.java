package com.android.server.wm;

import android.view.animation.Interpolator;

/* compiled from: ScreenRotationAnimationInjector */
class EaseSineInOutInterpolator implements Interpolator {
    EaseSineInOutInterpolator() {
    }

    public float getInterpolation(float input) {
        return (((float) Math.cos((((double) input) * 3.141592653589793d) / 1.0d)) - 1.0f) * -0.5f;
    }
}
