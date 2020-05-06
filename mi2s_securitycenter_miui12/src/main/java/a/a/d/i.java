package a.a.d;

import android.view.animation.Interpolator;
import androidx.annotation.RestrictTo;
import androidx.core.view.D;
import androidx.core.view.E;
import androidx.core.view.F;
import java.util.ArrayList;
import java.util.Iterator;

@RestrictTo({RestrictTo.a.f224c})
public class i {

    /* renamed from: a  reason: collision with root package name */
    final ArrayList<D> f35a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    private long f36b = -1;

    /* renamed from: c  reason: collision with root package name */
    private Interpolator f37c;

    /* renamed from: d  reason: collision with root package name */
    E f38d;
    private boolean e;
    private final F f = new h(this);

    public i a(long j) {
        if (!this.e) {
            this.f36b = j;
        }
        return this;
    }

    public i a(Interpolator interpolator) {
        if (!this.e) {
            this.f37c = interpolator;
        }
        return this;
    }

    public i a(D d2) {
        if (!this.e) {
            this.f35a.add(d2);
        }
        return this;
    }

    public i a(D d2, D d3) {
        this.f35a.add(d2);
        d3.b(d2.b());
        this.f35a.add(d3);
        return this;
    }

    public i a(E e2) {
        if (!this.e) {
            this.f38d = e2;
        }
        return this;
    }

    public void a() {
        if (this.e) {
            Iterator<D> it = this.f35a.iterator();
            while (it.hasNext()) {
                it.next().a();
            }
            this.e = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void b() {
        this.e = false;
    }

    public void c() {
        if (!this.e) {
            Iterator<D> it = this.f35a.iterator();
            while (it.hasNext()) {
                D next = it.next();
                long j = this.f36b;
                if (j >= 0) {
                    next.a(j);
                }
                Interpolator interpolator = this.f37c;
                if (interpolator != null) {
                    next.a(interpolator);
                }
                if (this.f38d != null) {
                    next.a((E) this.f);
                }
                next.c();
            }
            this.e = true;
        }
    }
}
