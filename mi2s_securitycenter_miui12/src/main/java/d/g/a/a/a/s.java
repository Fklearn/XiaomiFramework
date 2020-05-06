package d.g.a.a.a;

import android.os.Looper;
import android.util.AndroidRuntimeException;
import android.view.View;
import com.miui.maml.folme.AnimatedProperty;
import d.g.a.a.a.a;
import d.g.a.a.a.s;
import java.util.ArrayList;

public abstract class s<T extends s<T>> implements a.b {

    /* renamed from: a  reason: collision with root package name */
    public static final d f8826a = new j("translationX");

    /* renamed from: b  reason: collision with root package name */
    public static final d f8827b = new k("translationY");

    /* renamed from: c  reason: collision with root package name */
    public static final d f8828c = new l("translationZ");

    /* renamed from: d  reason: collision with root package name */
    public static final d f8829d = new m(AnimatedProperty.PROPERTY_NAME_SCALE_X);
    public static final d e = new n(AnimatedProperty.PROPERTY_NAME_SCALE_Y);
    public static final d f = new o(AnimatedProperty.PROPERTY_NAME_ROTATION);
    public static final d g = new p(AnimatedProperty.PROPERTY_NAME_ROTATION_X);
    public static final d h = new q(AnimatedProperty.PROPERTY_NAME_ROTATION_Y);
    public static final d i = new r(AnimatedProperty.PROPERTY_NAME_X);
    public static final d j = new d(AnimatedProperty.PROPERTY_NAME_Y);
    public static final d k = new e("z");
    public static final d l = new f(AnimatedProperty.PROPERTY_NAME_ALPHA);
    public static final d m = new g("scrollX");
    public static final d n = new h("scrollY");
    private boolean A;
    float o = 0.0f;
    float p = Float.MAX_VALUE;
    boolean q = false;
    final Object r = null;
    final u s;
    boolean t = false;
    float u = Float.MAX_VALUE;
    float v = (-this.u);
    private long w = 0;
    private float x;
    private final ArrayList<b> y = new ArrayList<>();
    private final ArrayList<c> z = new ArrayList<>();

    static class a {

        /* renamed from: a  reason: collision with root package name */
        float f8830a;

        /* renamed from: b  reason: collision with root package name */
        float f8831b;

        a() {
        }
    }

    public interface b {
        void a(s sVar, boolean z, float f, float f2);
    }

    public interface c {
        void a(s sVar, float f, float f2);
    }

    public static abstract class d extends u<View> {
        private d(String str) {
            super(str);
        }

        /* synthetic */ d(String str, j jVar) {
            this(str);
        }
    }

    s(v vVar) {
        this.s = new i(this, "FloatValueHolder", vVar);
        this.x = 1.0f;
    }

    private static <T> void a(ArrayList<T> arrayList) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            if (arrayList.get(size) == null) {
                arrayList.remove(size);
            }
        }
    }

    private static <T> void a(ArrayList<T> arrayList, T t2) {
        int indexOf = arrayList.indexOf(t2);
        if (indexOf >= 0) {
            arrayList.set(indexOf, (Object) null);
        }
    }

    private void b(boolean z2) {
        this.t = false;
        if (!this.A) {
            a.a().a((a.b) this);
        }
        this.A = false;
        this.w = 0;
        this.q = false;
        for (int i2 = 0; i2 < this.y.size(); i2++) {
            if (this.y.get(i2) != null) {
                this.y.get(i2).a(this, z2, this.p, this.o);
            }
        }
        a(this.y);
    }

    private void c(boolean z2) {
        if (!this.t) {
            this.A = z2;
            this.t = true;
            if (!this.q) {
                this.p = d();
            }
            float f2 = this.p;
            if (f2 > this.u || f2 < this.v) {
                throw new IllegalArgumentException("Starting value(" + this.p + ") need to be in " + "between min value(" + this.v + ") and max value(" + this.u + ")");
            } else if (!z2) {
                a.a().a(this, 0);
            }
        }
    }

    private float d() {
        return this.s.a(this.r);
    }

    public T a(float f2) {
        this.u = f2;
        return this;
    }

    public T a(c cVar) {
        if (!c()) {
            if (!this.z.contains(cVar)) {
                this.z.add(cVar);
            }
            return this;
        }
        throw new UnsupportedOperationException("Error: Update listeners must be added beforethe animation.");
    }

    public void a() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new AndroidRuntimeException("Animations may only be canceled on the main thread");
        } else if (this.t) {
            b(true);
        }
    }

    public void a(boolean z2) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new AndroidRuntimeException("Animations may only be started on the main thread");
        } else if (!this.t) {
            c(z2);
        }
    }

    /* access modifiers changed from: package-private */
    public abstract boolean a(long j2);

    /* access modifiers changed from: package-private */
    public float b() {
        return this.x * 0.75f;
    }

    public T b(float f2) {
        this.v = f2;
        return this;
    }

    public void b(c cVar) {
        a(this.z, cVar);
    }

    public T c(float f2) {
        if (f2 > 0.0f) {
            this.x = f2;
            g(f2 * 0.75f);
            return this;
        }
        throw new IllegalArgumentException("Minimum visible change must be positive.");
    }

    public boolean c() {
        return this.t;
    }

    /* access modifiers changed from: package-private */
    public void d(float f2) {
        this.s.a(this.r, f2);
        for (int i2 = 0; i2 < this.z.size(); i2++) {
            if (this.z.get(i2) != null) {
                this.z.get(i2).a(this, this.p, this.o);
            }
        }
        a(this.z);
    }

    public boolean doAnimationFrame(long j2) {
        long j3 = this.w;
        if (j3 == 0) {
            this.w = j2;
            d(this.p);
            return false;
        }
        this.w = j2;
        boolean a2 = a(j2 - j3);
        this.p = Math.min(this.p, this.u);
        this.p = Math.max(this.p, this.v);
        d(this.p);
        if (a2) {
            b(false);
        }
        return a2;
    }

    public T e(float f2) {
        this.p = f2;
        this.q = true;
        return this;
    }

    public T f(float f2) {
        this.o = f2;
        return this;
    }

    /* access modifiers changed from: package-private */
    public abstract void g(float f2);
}
