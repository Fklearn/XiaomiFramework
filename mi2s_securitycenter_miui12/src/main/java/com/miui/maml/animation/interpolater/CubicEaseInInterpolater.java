package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class CubicEaseInInterpolater implements Interpolator {
    public float getInterpolation(float f) {
        return f * f * f;
    }
}
