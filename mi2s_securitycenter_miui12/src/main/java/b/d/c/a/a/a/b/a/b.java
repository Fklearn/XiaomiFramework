package b.d.c.a.a.a.b.a;

class b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Float f2145a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Float f2146b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Float f2147c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ Float f2148d;
    final /* synthetic */ c e;

    b(c cVar, Float f, Float f2, Float f3, Float f4) {
        this.e = cVar;
        this.f2145a = f;
        this.f2146b = f2;
        this.f2147c = f3;
        this.f2148d = f4;
    }

    public Double a(Float f) {
        return f.floatValue() == 0.0f ? Double.valueOf(2.0d) : Double.valueOf(((2.0d - Math.exp((double) ((-this.f2145a.floatValue()) / f.floatValue()))) - Math.exp((double) ((-(this.f2146b.floatValue() - this.f2145a.floatValue())) / f.floatValue()))) / (1.0d - Math.exp((double) ((-this.f2146b.floatValue()) / f.floatValue()))));
    }

    public Double b(Float f) {
        return Double.valueOf(((double) this.f2145a.floatValue()) / ((((double) this.f2147c.floatValue()) - Math.log(a(f).doubleValue())) - Math.log((double) (1.0f - this.f2148d.floatValue()))));
    }
}
