package com.android.server.lights.interpolater;

import android.view.animation.Interpolator;

public class SineEaseInInterpolater implements Interpolator {
    public float getInterpolation(float input) {
        return (-((float) Math.cos(((double) input) * 1.5707963267948966d))) + 1.0f;
    }
}
