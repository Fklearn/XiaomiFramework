package d.k.a;

import android.view.animation.Interpolator;

public class a implements Interpolator {
    public float getInterpolation(float f) {
        return f * f * f;
    }
}
