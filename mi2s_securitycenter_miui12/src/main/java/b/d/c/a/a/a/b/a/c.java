package b.d.c.a.a.a.b.a;

import java.util.Random;

public class c extends d {
    private Float e = null;

    public Float a() {
        Float f;
        Float f2 = this.f2141b;
        if (f2 == null || (f = this.f2140a) == null) {
            throw new IllegalStateException("Epsilon and Delta must be set before calling _find_scale().");
        }
        Float valueOf = Float.valueOf(this.f2149d.f2143b.floatValue() - this.f2149d.f2142a.floatValue());
        Float f3 = this.f2144c;
        b bVar = new b(this, f3, valueOf, f2, f);
        Double valueOf2 = Double.valueOf(((double) f3.floatValue()) / (((double) f2.floatValue()) - Math.log((double) (1.0f - f.floatValue()))));
        Double b2 = bVar.b(Float.valueOf(valueOf2.floatValue()));
        Double valueOf3 = Double.valueOf((b2.doubleValue() - valueOf2.doubleValue()) * 2.0d);
        while (valueOf3.doubleValue() > b2.doubleValue() - valueOf2.doubleValue()) {
            valueOf3 = Double.valueOf(b2.doubleValue() - valueOf2.doubleValue());
            Double valueOf4 = Double.valueOf((b2.doubleValue() + valueOf2.doubleValue()) / 2.0d);
            if (bVar.b(Float.valueOf(valueOf4.floatValue())).doubleValue() >= valueOf4.doubleValue()) {
                valueOf2 = valueOf4;
            }
            if (bVar.b(Float.valueOf(valueOf4.floatValue())).doubleValue() <= valueOf4.doubleValue()) {
                b2 = valueOf4;
            }
        }
        return Float.valueOf(Double.valueOf((b2.doubleValue() + valueOf2.doubleValue()) / 2.0d).floatValue());
    }

    public Object b(Object obj) {
        super.a(obj);
        if (this.e == null) {
            this.e = Float.valueOf(a().floatValue());
        }
        float max = Math.max(Math.min(((Float) obj).floatValue(), this.f2149d.f2143b.floatValue()), this.f2149d.f2142a.floatValue());
        double min = Math.min(((((double) new Random().nextFloat()) * ((double) (c(Float.valueOf(this.f2149d.f2143b.floatValue() - max)).floatValue() - c(Float.valueOf(this.f2149d.f2142a.floatValue() - max)).floatValue()))) + ((double) c(Float.valueOf(this.f2149d.f2142a.floatValue() - max)).floatValue())) - 0.5d, 0.4999999999d);
        return Float.valueOf(Double.valueOf(((double) max) - ((((double) this.e.floatValue()) * Math.signum(min)) * Math.log(1.0d - (Math.abs(min) * 2.0d)))).floatValue());
    }

    public Float c(Float f) {
        float f2 = 0.0f;
        if (this.e.floatValue() == 0.0f) {
            if (f.floatValue() >= 0.0f) {
                f2 = 1.0f;
            }
            return Float.valueOf(f2);
        }
        return Float.valueOf(Double.valueOf(f.floatValue() < 0.0f ? Math.exp((double) (f.floatValue() / this.e.floatValue())) * 0.5d : 1.0d - (Math.exp((double) ((-f.floatValue()) / this.e.floatValue())) * 0.5d)).floatValue());
    }
}
