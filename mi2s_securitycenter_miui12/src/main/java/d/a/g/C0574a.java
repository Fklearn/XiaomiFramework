package d.a.g;

import java.util.Objects;

/* renamed from: d.a.g.a  reason: case insensitive filesystem */
public class C0574a<T> extends C0575b<T> implements C0576c<T> {
    private int mColorValue;

    public C0574a(String str) {
        super(str);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.mPropertyName.equals(((C0574a) obj).mPropertyName);
    }

    public int getIntValue(T t) {
        if (t instanceof h) {
            this.mColorValue = ((Integer) ((h) t).a(getName(), Integer.TYPE)).intValue();
        }
        return this.mColorValue;
    }

    public float getValue(T t) {
        return 0.0f;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.mPropertyName});
    }

    public void setIntValue(T t, int i) {
        this.mColorValue = i;
        if (t instanceof h) {
            ((h) t).a(getName(), Integer.TYPE, Integer.valueOf(i));
        }
    }

    public void setValue(T t, float f) {
    }
}
