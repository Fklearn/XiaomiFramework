package d.a.a;

import d.a.a.g;
import d.a.g.C0575b;

class e implements g.a<Float> {
    e() {
    }

    public Float a(a aVar, C0575b bVar, Float f) {
        if (aVar.f8627d == Float.MAX_VALUE) {
            return f;
        }
        return Float.valueOf(f.floatValue() == Float.MAX_VALUE ? aVar.f8627d : Math.max(f.floatValue(), aVar.f8627d));
    }
}
