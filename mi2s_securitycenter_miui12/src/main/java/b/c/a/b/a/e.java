package b.c.a.b.a;

import com.miui.maml.folme.AnimatedProperty;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private final int f1974a;

    /* renamed from: b  reason: collision with root package name */
    private final int f1975b;

    public e(int i, int i2) {
        this.f1974a = i;
        this.f1975b = i2;
    }

    public e(int i, int i2, int i3) {
        if (i3 % 180 == 0) {
            this.f1974a = i;
            this.f1975b = i2;
            return;
        }
        this.f1974a = i2;
        this.f1975b = i;
    }

    public int a() {
        return this.f1975b;
    }

    public e a(float f) {
        return new e((int) (((float) this.f1974a) * f), (int) (((float) this.f1975b) * f));
    }

    public e a(int i) {
        return new e(this.f1974a / i, this.f1975b / i);
    }

    public int b() {
        return this.f1974a;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(9);
        sb.append(this.f1974a);
        sb.append(AnimatedProperty.PROPERTY_NAME_X);
        sb.append(this.f1975b);
        return sb.toString();
    }
}
