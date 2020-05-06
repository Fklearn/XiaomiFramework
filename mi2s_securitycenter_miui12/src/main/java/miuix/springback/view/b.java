package miuix.springback.view;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private final double f8947a;

    /* renamed from: b  reason: collision with root package name */
    private final double f8948b;

    public b(float f, float f2) {
        double d2 = (double) f2;
        this.f8948b = Math.pow(6.283185307179586d / d2, 2.0d);
        this.f8947a = (((double) f) * 12.566370614359172d) / d2;
    }

    public double a(double d2, float f, double d3, double d4) {
        double d5 = (double) f;
        return (d2 * (1.0d - (this.f8947a * d5))) + ((double) ((float) (this.f8948b * (d3 - d4) * d5)));
    }
}
