package d.k.a;

import android.view.animation.Interpolator;

public class q implements Interpolator {
    public float getInterpolation(float f) {
        return (float) Math.sin(((double) f) * 1.5707963267948966d);
    }
}
