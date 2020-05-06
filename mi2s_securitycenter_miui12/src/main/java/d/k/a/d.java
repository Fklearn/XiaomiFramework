package d.k.a;

import android.view.animation.Interpolator;

public class d implements Interpolator {
    public float getInterpolation(float f) {
        if (f == 0.0f) {
            return 0.0f;
        }
        return (float) Math.pow(2.0d, (double) ((f - 1.0f) * 10.0f));
    }
}
