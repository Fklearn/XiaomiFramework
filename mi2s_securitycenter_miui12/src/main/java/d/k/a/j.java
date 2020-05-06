package d.k.a;

import android.view.animation.Interpolator;

public class j implements Interpolator {
    public float getInterpolation(float f) {
        return f * f * f * f;
    }
}
