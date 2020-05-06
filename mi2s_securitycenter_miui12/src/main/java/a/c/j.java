package a.c;

import androidx.annotation.Nullable;

public class j<E> implements Cloneable {

    /* renamed from: a  reason: collision with root package name */
    private static final Object f97a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private boolean f98b;

    /* renamed from: c  reason: collision with root package name */
    private int[] f99c;

    /* renamed from: d  reason: collision with root package name */
    private Object[] f100d;
    private int e;

    public j() {
        this(10);
    }

    public j(int i) {
        Object[] objArr;
        this.f98b = false;
        if (i == 0) {
            this.f99c = e.f68a;
            objArr = e.f70c;
        } else {
            int b2 = e.b(i);
            this.f99c = new int[b2];
            objArr = new Object[b2];
        }
        this.f100d = objArr;
    }

    private void b() {
        int i = this.e;
        int[] iArr = this.f99c;
        Object[] objArr = this.f100d;
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            Object obj = objArr[i3];
            if (obj != f97a) {
                if (i3 != i2) {
                    iArr[i2] = iArr[i3];
                    objArr[i2] = obj;
                    objArr[i3] = null;
                }
                i2++;
            }
        }
        this.f98b = false;
        this.e = i2;
    }

    public int a() {
        if (this.f98b) {
            b();
        }
        return this.e;
    }

    public int a(E e2) {
        if (this.f98b) {
            b();
        }
        for (int i = 0; i < this.e; i++) {
            if (this.f100d[i] == e2) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public E a(int i) {
        return b(i, (Object) null);
    }

    public void a(int i, E e2) {
        int i2 = this.e;
        if (i2 == 0 || i > this.f99c[i2 - 1]) {
            if (this.f98b && this.e >= this.f99c.length) {
                b();
            }
            int i3 = this.e;
            if (i3 >= this.f99c.length) {
                int b2 = e.b(i3 + 1);
                int[] iArr = new int[b2];
                Object[] objArr = new Object[b2];
                int[] iArr2 = this.f99c;
                System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
                Object[] objArr2 = this.f100d;
                System.arraycopy(objArr2, 0, objArr, 0, objArr2.length);
                this.f99c = iArr;
                this.f100d = objArr;
            }
            this.f99c[i3] = i;
            this.f100d[i3] = e2;
            this.e = i3 + 1;
            return;
        }
        c(i, e2);
    }

    public int b(int i) {
        if (this.f98b) {
            b();
        }
        return this.f99c[i];
    }

    public E b(int i, E e2) {
        int a2 = e.a(this.f99c, this.e, i);
        if (a2 >= 0) {
            E[] eArr = this.f100d;
            if (eArr[a2] != f97a) {
                return eArr[a2];
            }
        }
        return e2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r0 = r3.f100d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void c(int r4) {
        /*
            r3 = this;
            int[] r0 = r3.f99c
            int r1 = r3.e
            int r4 = a.c.e.a((int[]) r0, (int) r1, (int) r4)
            if (r4 < 0) goto L_0x0017
            java.lang.Object[] r0 = r3.f100d
            r1 = r0[r4]
            java.lang.Object r2 = f97a
            if (r1 == r2) goto L_0x0017
            r0[r4] = r2
            r4 = 1
            r3.f98b = r4
        L_0x0017:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: a.c.j.c(int):void");
    }

    public void c(int i, E e2) {
        int a2 = e.a(this.f99c, this.e, i);
        if (a2 >= 0) {
            this.f100d[a2] = e2;
            return;
        }
        int i2 = ~a2;
        if (i2 < this.e) {
            Object[] objArr = this.f100d;
            if (objArr[i2] == f97a) {
                this.f99c[i2] = i;
                objArr[i2] = e2;
                return;
            }
        }
        if (this.f98b && this.e >= this.f99c.length) {
            b();
            i2 = ~e.a(this.f99c, this.e, i);
        }
        int i3 = this.e;
        if (i3 >= this.f99c.length) {
            int b2 = e.b(i3 + 1);
            int[] iArr = new int[b2];
            Object[] objArr2 = new Object[b2];
            int[] iArr2 = this.f99c;
            System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
            Object[] objArr3 = this.f100d;
            System.arraycopy(objArr3, 0, objArr2, 0, objArr3.length);
            this.f99c = iArr;
            this.f100d = objArr2;
        }
        int i4 = this.e;
        if (i4 - i2 != 0) {
            int[] iArr3 = this.f99c;
            int i5 = i2 + 1;
            System.arraycopy(iArr3, i2, iArr3, i5, i4 - i2);
            Object[] objArr4 = this.f100d;
            System.arraycopy(objArr4, i2, objArr4, i5, this.e - i2);
        }
        this.f99c[i2] = i;
        this.f100d[i2] = e2;
        this.e++;
    }

    public void clear() {
        int i = this.e;
        Object[] objArr = this.f100d;
        for (int i2 = 0; i2 < i; i2++) {
            objArr[i2] = null;
        }
        this.e = 0;
        this.f98b = false;
    }

    public j<E> clone() {
        try {
            j<E> jVar = (j) super.clone();
            jVar.f99c = (int[]) this.f99c.clone();
            jVar.f100d = (Object[]) this.f100d.clone();
            return jVar;
        } catch (CloneNotSupportedException e2) {
            throw new AssertionError(e2);
        }
    }

    public E d(int i) {
        if (this.f98b) {
            b();
        }
        return this.f100d[i];
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
            sb.append(b(i));
            sb.append('=');
            Object d2 = d(i);
            if (d2 != this) {
                sb.append(d2);
            } else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
