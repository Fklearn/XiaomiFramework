package com.miui.superpower.statusbar.panel;

import android.view.animation.Interpolator;

class b implements Interpolator {
    b() {
    }

    public float getInterpolation(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }
}
