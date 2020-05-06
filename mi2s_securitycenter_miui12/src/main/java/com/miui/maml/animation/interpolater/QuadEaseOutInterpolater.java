package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class QuadEaseOutInterpolater implements Interpolator {
    public float getInterpolation(float f) {
        return (-f) * (f - 2.0f);
    }
}
