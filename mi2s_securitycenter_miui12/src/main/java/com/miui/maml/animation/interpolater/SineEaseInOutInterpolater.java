package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class SineEaseInOutInterpolater implements Interpolator {
    public float getInterpolation(float f) {
        return ((float) (Math.cos(((double) f) * 3.141592653589793d) - 1.0d)) * -0.5f;
    }
}
