package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class QuartEaseInInterpolater implements Interpolator {
    public float getInterpolation(float f) {
        return f * f * f * f;
    }
}
