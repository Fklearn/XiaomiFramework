package d.a.a;

import d.a.e.k;
import d.a.g.C0575b;
import d.a.i.b;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public long f8624a;

    /* renamed from: b  reason: collision with root package name */
    public long f8625b;

    /* renamed from: c  reason: collision with root package name */
    public b.a f8626c;

    /* renamed from: d  reason: collision with root package name */
    public float f8627d = Float.MAX_VALUE;
    public double e;
    public int f;
    public Object g;
    public long h;
    public C0575b[] i;
    public HashSet<k> j = new HashSet<>();

    public a() {
    }

    public a(a aVar) {
        if (aVar != null) {
            this.f8624a = aVar.f8624a;
            this.f8626c = aVar.f8626c;
            this.i = aVar.i;
            this.j.addAll(aVar.j);
            this.g = aVar.g;
            this.h = aVar.h;
            this.f8627d = aVar.f8627d;
            this.f8625b = aVar.f8625b;
            this.f = aVar.f;
            this.e = aVar.e;
        }
    }

    public a(C0575b bVar) {
        a(bVar);
    }

    public a(C0575b... bVarArr) {
        this.i = bVarArr;
    }

    public a a(float f2) {
        this.f8627d = f2;
        return this;
    }

    public a a(int i2, float... fArr) {
        this.f8626c = b.a(i2, fArr);
        return this;
    }

    public a a(long j2) {
        this.f8624a = j2;
        return this;
    }

    public a a(b.a aVar) {
        this.f8626c = aVar;
        return this;
    }

    public a a(k... kVarArr) {
        Collections.addAll(this.j, kVarArr);
        return this;
    }

    public final void a(C0575b bVar) {
        this.i = new C0575b[]{bVar};
    }

    public String toString() {
        return "AnimConfig{, delay=" + this.f8624a + ", minDuration = " + this.f8625b + ", fromSpeed = " + this.f8627d + ", ease=" + this.f8626c + ", relatedProperty=" + Arrays.toString(this.i) + ", tag = " + this.g + ", listeners = " + Arrays.toString(this.j.toArray()) + '}';
    }
}
