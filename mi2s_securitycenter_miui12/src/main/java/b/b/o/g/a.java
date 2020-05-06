package b.b.o.g;

import android.util.Property;

public abstract class a<T> extends Property<T, Integer> {
    public a(String str) {
        super(Integer.class, str);
    }

    public abstract void a(T t, int i);

    /* renamed from: a */
    public final void set(T t, Integer num) {
        a(t, num.intValue());
    }
}
