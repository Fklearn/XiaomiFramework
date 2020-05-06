package d.a.f;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.view.Choreographer;
import java.util.ArrayList;

public class b {

    /* renamed from: a  reason: collision with root package name */
    public static final ThreadLocal<b> f8735a = new ThreadLocal<>();

    /* renamed from: b  reason: collision with root package name */
    private final ArrayMap<C0073b, Long> f8736b = new ArrayMap<>();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public final ArrayList<C0073b> f8737c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    private final a f8738d = new a();
    private c e;
    /* access modifiers changed from: private */
    public long f = 0;
    private boolean g = false;

    class a {
        a() {
        }

        /* access modifiers changed from: package-private */
        public void a() {
            long unused = b.this.f = SystemClock.uptimeMillis();
            b bVar = b.this;
            bVar.a(bVar.f);
            if (b.this.f8737c.size() > 0) {
                b.this.c().a();
            }
        }
    }

    /* renamed from: d.a.f.b$b  reason: collision with other inner class name */
    public interface C0073b {
        boolean doAnimationFrame(long j);
    }

    static abstract class c {

        /* renamed from: a  reason: collision with root package name */
        final a f8740a;

        c(a aVar) {
            this.f8740a = aVar;
        }

        /* access modifiers changed from: package-private */
        public abstract void a();
    }

    private static class d extends c {

        /* renamed from: b  reason: collision with root package name */
        private final Runnable f8741b = new c(this);

        /* renamed from: c  reason: collision with root package name */
        private final Handler f8742c = new Handler(Looper.myLooper());
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public long f8743d = -1;

        d(a aVar) {
            super(aVar);
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.f8742c.postDelayed(this.f8741b, Math.max(10 - (SystemClock.uptimeMillis() - this.f8743d), 0));
        }
    }

    private static class e extends c {

        /* renamed from: b  reason: collision with root package name */
        private final Choreographer f8744b = Choreographer.getInstance();

        /* renamed from: c  reason: collision with root package name */
        private final Choreographer.FrameCallback f8745c = new d(this);

        e(a aVar) {
            super(aVar);
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.f8744b.postFrameCallback(this.f8745c);
        }
    }

    public static b a() {
        if (f8735a.get() == null) {
            f8735a.set(new b());
        }
        return f8735a.get();
    }

    /* access modifiers changed from: private */
    public void a(long j) {
        long uptimeMillis = SystemClock.uptimeMillis();
        for (int i = 0; i < this.f8737c.size(); i++) {
            C0073b bVar = this.f8737c.get(i);
            if (bVar != null && b(bVar, uptimeMillis)) {
                bVar.doAnimationFrame(j);
            }
        }
        b();
    }

    private void b() {
        if (this.g) {
            for (int size = this.f8737c.size() - 1; size >= 0; size--) {
                if (this.f8737c.get(size) == null) {
                    this.f8737c.remove(size);
                }
            }
            this.g = false;
        }
    }

    private boolean b(C0073b bVar, long j) {
        Long l = this.f8736b.get(bVar);
        if (l == null) {
            return true;
        }
        if (l.longValue() >= j) {
            return false;
        }
        this.f8736b.remove(bVar);
        return true;
    }

    /* access modifiers changed from: private */
    public c c() {
        if (this.e == null) {
            this.e = Build.VERSION.SDK_INT >= 16 ? new e(this.f8738d) : new d(this.f8738d);
        }
        return this.e;
    }

    public void a(C0073b bVar) {
        this.f8736b.remove(bVar);
        int indexOf = this.f8737c.indexOf(bVar);
        if (indexOf >= 0) {
            this.f8737c.set(indexOf, (Object) null);
            this.g = true;
        }
    }

    public void a(C0073b bVar, long j) {
        if (this.f8737c.size() == 0) {
            c().a();
        }
        if (!this.f8737c.contains(bVar)) {
            this.f8737c.add(bVar);
        }
        if (j > 0) {
            this.f8736b.put(bVar, Long.valueOf(SystemClock.uptimeMillis() + j));
        }
    }
}
