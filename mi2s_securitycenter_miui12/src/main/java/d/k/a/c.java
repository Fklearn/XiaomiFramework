package d.k.a;

import android.view.animation.Interpolator;

public class c implements Interpolator {
    public float getInterpolation(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2) + 1.0f;
    }
}
