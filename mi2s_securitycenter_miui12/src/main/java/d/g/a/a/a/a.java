package d.g.a.a.a;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.view.Choreographer;
import java.util.ArrayList;

class a {

    /* renamed from: a  reason: collision with root package name */
    public static final ThreadLocal<a> f8811a = new ThreadLocal<>();

    /* renamed from: b  reason: collision with root package name */
    private final ArrayMap<b, Long> f8812b = new ArrayMap<>();

    /* renamed from: c  reason: collision with root package name */
    final ArrayList<b> f8813c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    private final C0076a f8814d = new C0076a();
    private c e;
    long f = 0;
    private boolean g = false;

    /* renamed from: d.g.a.a.a.a$a  reason: collision with other inner class name */
    class C0076a {
        C0076a() {
        }

        /* access modifiers changed from: package-private */
        public void a() {
            a.this.f = SystemClock.uptimeMillis();
            a aVar = a.this;
            aVar.a(aVar.f);
            if (a.this.f8813c.size() > 0) {
                a.this.b().a();
            }
        }
    }

    interface b {
        boolean doAnimationFrame(long j);
    }

    static abstract class c {

        /* renamed from: a  reason: collision with root package name */
        final C0076a f8816a;

        c(C0076a aVar) {
            this.f8816a = aVar;
        }

        /* access modifiers changed from: package-private */
        public abstract void a();
    }

    private static class d extends c {

        /* renamed from: b  reason: collision with root package name */
        private final Runnable f8817b = new b(this);

        /* renamed from: c  reason: collision with root package name */
        private final Handler f8818c = new Handler(Looper.myLooper());

        /* renamed from: d  reason: collision with root package name */
        long f8819d = -1;

        d(C0076a aVar) {
            super(aVar);
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.f8818c.postDelayed(this.f8817b, Math.max(10 - (SystemClock.uptimeMillis() - this.f8819d), 0));
        }
    }

    private static class e extends c {

        /* renamed from: b  reason: collision with root package name */
        private final Choreographer f8820b = Choreographer.getInstance();

        /* renamed from: c  reason: collision with root package name */
        private final Choreographer.FrameCallback f8821c = new c(this);

        e(C0076a aVar) {
            super(aVar);
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.f8820b.postFrameCallback(this.f8821c);
        }
    }

    a() {
    }

    public static a a() {
        if (f8811a.get() == null) {
            f8811a.set(new a());
        }
        return f8811a.get();
    }

    private boolean b(b bVar, long j) {
        Long l = this.f8812b.get(bVar);
        if (l == null) {
            return true;
        }
        if (l.longValue() >= j) {
            return false;
        }
        this.f8812b.remove(bVar);
        return true;
    }

    private void c() {
        if (this.g) {
            for (int size = this.f8813c.size() - 1; size >= 0; size--) {
                if (this.f8813c.get(size) == null) {
                    this.f8813c.remove(size);
                }
            }
            this.g = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void a(long j) {
        long uptimeMillis = SystemClock.uptimeMillis();
        for (int i = 0; i < this.f8813c.size(); i++) {
            b bVar = this.f8813c.get(i);
            if (bVar != null && b(bVar, uptimeMillis)) {
                bVar.doAnimationFrame(j);
            }
        }
        c();
    }

    public void a(b bVar) {
        this.f8812b.remove(bVar);
        int indexOf = this.f8813c.indexOf(bVar);
        if (indexOf >= 0) {
            this.f8813c.set(indexOf, (Object) null);
            this.g = true;
        }
    }

    public void a(b bVar, long j) {
        if (this.f8813c.size() == 0) {
            b().a();
        }
        if (!this.f8813c.contains(bVar)) {
            this.f8813c.add(bVar);
        }
        if (j > 0) {
            this.f8812b.put(bVar, Long.valueOf(SystemClock.uptimeMillis() + j));
        }
    }

    /* access modifiers changed from: package-private */
    public c b() {
        if (this.e == null) {
            this.e = Build.VERSION.SDK_INT >= 16 ? new e(this.f8814d) : new d(this.f8814d);
        }
        return this.e;
    }
}
