package d.a.g;

import android.util.Property;

/* renamed from: d.a.g.b  reason: case insensitive filesystem */
public abstract class C0575b<T> extends Property<T, Float> {
    final String mPropertyName;

    public C0575b(String str) {
        super(Float.class, str);
        this.mPropertyName = str;
    }

    public Float get(T t) {
        return Float.valueOf(t == null ? 0.0f : getValue(t));
    }

    public abstract float getValue(T t);

    public final void set(T t, Float f) {
        if (t != null) {
            setValue(t, f.floatValue());
        }
    }

    public abstract void setValue(T t, float f);

    public String toString() {
        return getClass().getSimpleName() + "{" + "mPropertyName='" + this.mPropertyName + '\'' + '}';
    }
}
