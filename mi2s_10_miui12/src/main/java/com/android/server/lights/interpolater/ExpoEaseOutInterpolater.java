package com.android.server.lights.interpolater;

import android.view.animation.Interpolator;

public class ExpoEaseOutInterpolater implements Interpolator {
    public float getInterpolation(float input) {
        if (input == 1.0f) {
            return 1.0f;
        }
        return (float) ((-Math.pow(2.0d, (double) (-10.0f * input))) + 1.0d);
    }
}
