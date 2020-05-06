package a.c;

import androidx.annotation.Nullable;

public class f<E> implements Cloneable {

    /* renamed from: a  reason: collision with root package name */
    private static final Object f71a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private boolean f72b;

    /* renamed from: c  reason: collision with root package name */
    private long[] f73c;

    /* renamed from: d  reason: collision with root package name */
    private Object[] f74d;
    private int e;

    public f() {
        this(10);
    }

    public f(int i) {
        Object[] objArr;
        this.f72b = false;
        if (i == 0) {
            this.f73c = e.f69b;
            objArr = e.f70c;
        } else {
            int c2 = e.c(i);
            this.f73c = new long[c2];
            objArr = new Object[c2];
        }
        this.f74d = objArr;
    }

    private void b() {
        int i = this.e;
        long[] jArr = this.f73c;
        Object[] objArr = this.f74d;
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            Object obj = objArr[i3];
            if (obj != f71a) {
                if (i3 != i2) {
                    jArr[i2] = jArr[i3];
                    objArr[i2] = obj;
                    objArr[i3] = null;
                }
                i2++;
            }
        }
        this.f72b = false;
        this.e = i2;
    }

    public int a() {
        if (this.f72b) {
            b();
        }
        return this.e;
    }

    public long a(int i) {
        if (this.f72b) {
            b();
        }
        return this.f73c[i];
    }

    @Nullable
    public E a(long j) {
        return b(j, (Object) null);
    }

    public void a(long j, E e2) {
        int i = this.e;
        if (i == 0 || j > this.f73c[i - 1]) {
            if (this.f72b && this.e >= this.f73c.length) {
                b();
            }
            int i2 = this.e;
            if (i2 >= this.f73c.length) {
                int c2 = e.c(i2 + 1);
                long[] jArr = new long[c2];
                Object[] objArr = new Object[c2];
                long[] jArr2 = this.f73c;
                System.arraycopy(jArr2, 0, jArr, 0, jArr2.length);
                Object[] objArr2 = this.f74d;
                System.arraycopy(objArr2, 0, objArr, 0, objArr2.length);
                this.f73c = jArr;
                this.f74d = objArr;
            }
            this.f73c[i2] = j;
            this.f74d[i2] = e2;
            this.e = i2 + 1;
            return;
        }
        c(j, e2);
    }

    public E b(long j, E e2) {
        int a2 = e.a(this.f73c, this.e, j);
        if (a2 >= 0) {
            E[] eArr = this.f74d;
            if (eArr[a2] != f71a) {
                return eArr[a2];
            }
        }
        return e2;
    }

    public void b(int i) {
        Object[] objArr = this.f74d;
        Object obj = objArr[i];
        Object obj2 = f71a;
        if (obj != obj2) {
            objArr[i] = obj2;
            this.f72b = true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r4 = r2.f74d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(long r3) {
        /*
            r2 = this;
            long[] r0 = r2.f73c
            int r1 = r2.e
            int r3 = a.c.e.a((long[]) r0, (int) r1, (long) r3)
            if (r3 < 0) goto L_0x0017
            java.lang.Object[] r4 = r2.f74d
            r0 = r4[r3]
            java.lang.Object r1 = f71a
            if (r0 == r1) goto L_0x0017
            r4[r3] = r1
            r3 = 1
            r2.f72b = r3
        L_0x0017:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: a.c.f.b(long):void");
    }

    public E c(int i) {
        if (this.f72b) {
            b();
        }
        return this.f74d[i];
    }

    public void c(long j, E e2) {
        int a2 = e.a(this.f73c, this.e, j);
        if (a2 >= 0) {
            this.f74d[a2] = e2;
            return;
        }
        int i = ~a2;
        if (i < this.e) {
            Object[] objArr = this.f74d;
            if (objArr[i] == f71a) {
                this.f73c[i] = j;
                objArr[i] = e2;
                return;
            }
        }
        if (this.f72b && this.e >= this.f73c.length) {
            b();
            i = ~e.a(this.f73c, this.e, j);
        }
        int i2 = this.e;
        if (i2 >= this.f73c.length) {
            int c2 = e.c(i2 + 1);
            long[] jArr = new long[c2];
            Object[] objArr2 = new Object[c2];
            long[] jArr2 = this.f73c;
            System.arraycopy(jArr2, 0, jArr, 0, jArr2.length);
            Object[] objArr3 = this.f74d;
            System.arraycopy(objArr3, 0, objArr2, 0, objArr3.length);
            this.f73c = jArr;
            this.f74d = objArr2;
        }
        int i3 = this.e;
        if (i3 - i != 0) {
            long[] jArr3 = this.f73c;
            int i4 = i + 1;
            System.arraycopy(jArr3, i, jArr3, i4, i3 - i);
            Object[] objArr4 = this.f74d;
            System.arraycopy(objArr4, i, objArr4, i4, this.e - i);
        }
        this.f73c[i] = j;
        this.f74d[i] = e2;
        this.e++;
    }

    public void clear() {
        int i = this.e;
        Object[] objArr = this.f74d;
        for (int i2 = 0; i2 < i; i2++) {
            objArr[i2] = null;
        }
        this.e = 0;
        this.f72b = false;
    }

    public f<E> clone() {
        try {
            f<E> fVar = (f) super.clone();
            fVar.f73c = (long[]) this.f73c.clone();
            fVar.f74d = (Object[]) this.f74d.clone();
            return fVar;
        } catch (CloneNotSupportedException e2) {
            throw new AssertionError(e2);
        }
    }

    public String toString() {
        if (a() <= 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.e * 28);
        sb.append('{');
        for (int i = 0; i < this.e; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(a(i));
            sb.append('=');
            Object c2 = c(i);
            if (c2 != this) {
                sb.append(c2);
            } else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
