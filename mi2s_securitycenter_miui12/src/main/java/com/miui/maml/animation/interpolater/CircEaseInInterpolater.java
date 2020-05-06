package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class CircEaseInInterpolater implements Interpolator {
    public float getInterpolation(float f) {
        return -((float) (Math.sqrt((double) (1.0f - (f * f))) - 1.0d));
    }
}
