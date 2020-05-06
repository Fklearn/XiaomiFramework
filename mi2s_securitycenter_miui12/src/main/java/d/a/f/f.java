package d.a.f;

public class f implements g {

    /* renamed from: a  reason: collision with root package name */
    private final double f8751a;

    public f(float f) {
        this.f8751a = 1.0d - Math.pow(2.718281828459045d, (double) (f * -4.2f));
    }

    public double a(double d2, float f, double... dArr) {
        return d2 * Math.pow(1.0d - this.f8751a, (double) f);
    }
}
