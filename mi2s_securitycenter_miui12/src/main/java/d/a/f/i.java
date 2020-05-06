package d.a.f;

public class i implements g {

    /* renamed from: a  reason: collision with root package name */
    private final double f8752a;

    /* renamed from: b  reason: collision with root package name */
    private final double f8753b;

    public i(float f, float f2) {
        double d2 = (double) f2;
        this.f8753b = Math.pow(6.283185307179586d / d2, 2.0d);
        this.f8752a = (((double) f) * 12.566370614359172d) / d2;
    }

    public double a(double d2, float f, double... dArr) {
        double d3 = (double) f;
        return (d2 * (1.0d - (this.f8752a * d3))) + ((double) ((float) (this.f8753b * (dArr[0] - dArr[1]) * d3)));
    }
}
