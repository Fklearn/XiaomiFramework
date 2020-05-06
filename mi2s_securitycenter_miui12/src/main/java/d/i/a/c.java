package d.i.a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class c {

    /* renamed from: a  reason: collision with root package name */
    private a f8863a = new a(this, 0);

    /* renamed from: b  reason: collision with root package name */
    private a f8864b = new b(this, 1);

    private abstract class a {

        /* renamed from: a  reason: collision with root package name */
        float f8865a;

        /* renamed from: b  reason: collision with root package name */
        float f8866b;

        /* renamed from: c  reason: collision with root package name */
        int f8867c;

        a(int i) {
            this.f8867c = i;
        }

        private float a(float f) {
            float f2;
            float pow;
            int b2 = b();
            if (b2 == 0) {
                pow = Math.abs(f);
                f2 = 0.5f;
            } else {
                f2 = (float) b2;
                double min = (double) Math.min(Math.abs(f) / f2, 1.0f);
                pow = (float) (((Math.pow(min, 3.0d) / 3.0d) - Math.pow(min, 2.0d)) + min);
            }
            return pow * f2;
        }

        private void a(int i, int[] iArr, boolean z) {
            if (i != 0 && a()) {
                float f = (float) i;
                this.f8866b += f;
                if (z) {
                    this.f8865a = Math.signum(this.f8866b) * a(Math.abs(this.f8866b));
                } else {
                    this.f8865a += f;
                    this.f8866b = Math.signum(this.f8865a) * b(Math.abs(this.f8865a));
                }
                int i2 = this.f8867c;
                iArr[i2] = iArr[i2] + i;
            }
        }

        private float b(float f) {
            int b2 = b();
            if (b2 == 0) {
                return Math.abs(f) * 2.0f;
            }
            float f2 = (float) b2;
            if (Math.abs(f) / f2 > 0.33333334f) {
                return f * 3.0f;
            }
            double d2 = (double) b2;
            return (float) (d2 - (Math.pow(d2, 0.6666666865348816d) * Math.pow((double) (f2 - (Math.abs(f) * 3.0f)), 0.3333333432674408d)));
        }

        private int b(int i, int[] iArr, boolean z) {
            float f = this.f8865a;
            float f2 = this.f8866b;
            float signum = Math.signum(f);
            this.f8866b += (float) i;
            if (z) {
                this.f8865a = Math.signum(this.f8866b) * a(Math.abs(this.f8866b));
                int i2 = this.f8867c;
                iArr[i2] = iArr[i2] + (i - i);
            }
            int i3 = (int) (this.f8865a + (this.f8866b - f2));
            float f3 = (float) i3;
            if (signum * f3 >= 0.0f) {
                if (!z) {
                    this.f8865a = f3;
                }
                iArr[this.f8867c] = i;
            } else {
                this.f8865a = 0.0f;
                int i4 = this.f8867c;
                iArr[i4] = (int) (((float) iArr[i4]) + f);
            }
            if (this.f8865a == 0.0f) {
                this.f8866b = 0.0f;
            }
            if (!z) {
                this.f8866b = Math.signum(this.f8865a) * b(Math.abs(this.f8865a));
            }
            return i3;
        }

        /* access modifiers changed from: package-private */
        public void a(int i, @Nullable int[] iArr, int i2, @NonNull int[] iArr2) {
            if (c.this.g()) {
                a(i, iArr2, i2 == 0);
            }
        }

        /* access modifiers changed from: protected */
        public abstract boolean a();

        /* access modifiers changed from: package-private */
        public boolean a(@NonNull int[] iArr, @NonNull int[] iArr2, boolean z) {
            int i = iArr[this.f8867c];
            if (i != 0 && a()) {
                float f = this.f8865a;
                if (f == 0.0f || Integer.signum((int) f) * i > 0) {
                    return false;
                }
                iArr[this.f8867c] = b(i, iArr2, z);
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public abstract int b();
    }

    /* access modifiers changed from: protected */
    public abstract void a(int i, int i2, int i3, int i4, @Nullable int[] iArr, int i5, @Nullable int[] iArr2);

    /* access modifiers changed from: protected */
    public abstract boolean a();

    /* access modifiers changed from: protected */
    public abstract boolean a(int i, int i2, @Nullable int[] iArr, @Nullable int[] iArr2, int i3);

    public void b(int i, int i2, int i3, int i4, @Nullable int[] iArr, int i5, @Nullable int[] iArr2) {
        if (iArr2 == null) {
            iArr2 = new int[]{0, 0};
        }
        a(i, i2, i3, i4, iArr, i5, iArr2);
        int i6 = i3 - iArr2[0];
        int i7 = i4 - iArr2[1];
        if (i6 != 0 || i7 != 0) {
            this.f8863a.a(i6, iArr, i5, iArr2);
            this.f8864b.a(i7, iArr, i5, iArr2);
        }
    }

    /* access modifiers changed from: protected */
    public abstract boolean b();

    public boolean b(int i, int i2, @Nullable int[] iArr, @Nullable int[] iArr2, int i3) {
        boolean z;
        int i4;
        int i5;
        int[] iArr3 = {0, 0};
        if (g()) {
            boolean z2 = i3 == 0;
            int[] iArr4 = {i, i2};
            boolean a2 = this.f8864b.a(iArr4, iArr3, z2) | this.f8863a.a(iArr4, iArr3, z2);
            i4 = iArr4[0];
            i5 = iArr4[1];
            z = a2;
        } else {
            i4 = i;
            i5 = i2;
            z = false;
        }
        if (z) {
            i4 -= iArr3[0];
            i5 -= iArr3[1];
        }
        boolean a3 = a(i4, i5, iArr, iArr2, i3) | z;
        if (iArr != null) {
            iArr[0] = iArr[0] + iArr3[0];
            iArr[1] = iArr[1] + iArr3[1];
        }
        return a3;
    }

    /* access modifiers changed from: protected */
    public abstract int c();

    public int d() {
        return (int) this.f8863a.f8865a;
    }

    public int e() {
        return (int) this.f8864b.f8865a;
    }

    /* access modifiers changed from: protected */
    public abstract int f();

    /* access modifiers changed from: protected */
    public abstract boolean g();
}
