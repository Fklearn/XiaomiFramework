package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class BounceEaseInInterpolater implements Interpolator {
    public static float getInterpolationImp(float f) {
        return 1.0f - BounceEaseOutInterpolater.getInterpolationImp(1.0f - f);
    }

    public float getInterpolation(float f) {
        return getInterpolationImp(f);
    }
}
