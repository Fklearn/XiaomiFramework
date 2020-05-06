package com.android.server.lights.interpolater;

import android.view.animation.Interpolator;

public class LinearInterpolater implements Interpolator {
    public float getInterpolation(float input) {
        return input;
    }
}
