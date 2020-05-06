package d.k.a;

import android.view.animation.Interpolator;

public class i implements Interpolator {
    public float getInterpolation(float f) {
        return (-f) * (f - 2.0f);
    }
}
