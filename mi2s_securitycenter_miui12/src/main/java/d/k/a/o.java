package d.k.a;

import android.view.animation.Interpolator;

public class o implements Interpolator {
    public float getInterpolation(float f) {
        return (-((float) Math.cos(((double) f) * 1.5707963267948966d))) + 1.0f;
    }
}
