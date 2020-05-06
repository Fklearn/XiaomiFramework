package miui.animation;

import android.animation.TimeInterpolator;

public class DampingInterpolator implements TimeInterpolator {
    private final double mAtanValue = Math.atan((double) this.mFactor);
    private final float mFactor;

    public DampingInterpolator(float factor) {
        this.mFactor = factor;
    }

    public float getInterpolation(float input) {
        return (float) (Math.atan((double) (this.mFactor * input)) / this.mAtanValue);
    }
}
