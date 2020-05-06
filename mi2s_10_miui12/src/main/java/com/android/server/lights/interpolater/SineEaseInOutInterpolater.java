package com.android.server.lights.interpolater;

import android.view.animation.Interpolator;

public class SineEaseInOutInterpolater implements Interpolator {
    public float getInterpolation(float input) {
        return ((float) (Math.cos(((double) input) * 3.141592653589793d) - 1.0d)) * -0.5f;
    }
}
