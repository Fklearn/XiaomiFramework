package a.g.a.a;

import android.view.animation.Interpolator;

abstract class d implements Interpolator {

    /* renamed from: a  reason: collision with root package name */
    private final float[] f152a;

    /* renamed from: b  reason: collision with root package name */
    private final float f153b = (1.0f / ((float) (this.f152a.length - 1)));

    protected d(float[] fArr) {
        this.f152a = fArr;
    }

    public float getInterpolation(float f) {
        if (f >= 1.0f) {
            return 1.0f;
        }
        if (f <= 0.0f) {
            return 0.0f;
        }
        float[] fArr = this.f152a;
        int min = Math.min((int) (((float) (fArr.length - 1)) * f), fArr.length - 2);
        float f2 = this.f153b;
        float f3 = (f - (((float) min) * f2)) / f2;
        float[] fArr2 = this.f152a;
        return fArr2[min] + (f3 * (fArr2[min + 1] - fArr2[min]));
    }
}
