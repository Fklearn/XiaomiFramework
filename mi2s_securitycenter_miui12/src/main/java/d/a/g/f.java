package d.a.g;

import java.util.Objects;

public class f extends C0575b {
    public f(String str) {
        super(str);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !f.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        return Objects.equals(getName(), ((f) obj).getName());
    }

    public float getValue(Object obj) {
        Float f;
        if (!(obj instanceof h) || (f = (Float) ((h) obj).a(getName(), Float.TYPE)) == null) {
            return 0.0f;
        }
        return f.floatValue();
    }

    public int hashCode() {
        return Objects.hash(new Object[]{getName()});
    }

    public void setValue(Object obj, float f) {
        if (obj instanceof h) {
            ((h) obj).a(getName(), Float.TYPE, Float.valueOf(f));
        }
    }

    public String toString() {
        return "ValueProperty{name=" + getName() + '}';
    }
}
