package com.android.server.wm;

import android.view.animation.Interpolator;

/* compiled from: ScreenRotationAnimationInjector */
class EaseQuartOutInterpolator implements Interpolator {
    EaseQuartOutInterpolator() {
    }

    public float getInterpolation(float input) {
        return 1.0f - ((((input - 1.0f) * (input - 1.0f)) * (input - 1.0f)) * (input - 1.0f));
    }
}
