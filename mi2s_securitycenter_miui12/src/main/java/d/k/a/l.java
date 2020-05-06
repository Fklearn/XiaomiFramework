package d.k.a;

import android.view.animation.Interpolator;

public class l implements Interpolator {
    public float getInterpolation(float f) {
        return f * f * f * f * f;
    }
}
