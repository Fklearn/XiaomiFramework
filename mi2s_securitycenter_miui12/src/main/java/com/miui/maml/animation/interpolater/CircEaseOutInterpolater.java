package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class CircEaseOutInterpolater implements Interpolator {
    public float getInterpolation(float f) {
        float f2 = f - 1.0f;
        return (float) Math.sqrt((double) (1.0f - (f2 * f2)));
    }
}
