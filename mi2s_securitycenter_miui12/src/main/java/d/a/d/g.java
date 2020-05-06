package d.a.d;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import d.a.d;
import d.a.f.b;
import d.a.g.C0575b;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class g implements b.C0073b {

    /* renamed from: a  reason: collision with root package name */
    private static final Handler f8701a = new b(Looper.getMainLooper());

    /* renamed from: b  reason: collision with root package name */
    private long f8702b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public volatile long f8703c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f8704d;
    private volatile float e = 1.0f;
    private long[] f = {0, 0, 0, 0, 0};
    private int g = 0;
    private List<d> h = new ArrayList();

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        static final g f8705a = new g();
    }

    private long a(long[] jArr) {
        int i = 0;
        long j = 0;
        for (long j2 : jArr) {
            j += j2;
            if (j2 > 0) {
                i++;
            }
        }
        if (i > 0) {
            return j / ((long) i);
        }
        return 0;
    }

    public static i a(d dVar, long j, d.a.b.a aVar, d.a.b.a aVar2, d.a.a.g gVar) {
        i animTask = dVar.getAnimTask();
        animTask.a(j, new k(dVar, aVar, aVar2, gVar));
        return animTask;
    }

    /* access modifiers changed from: private */
    public void a(i iVar, long j, long j2, long... jArr) {
        iVar.a(j, j2, jArr);
    }

    private long b(long j) {
        long j2 = this.f8702b;
        long j3 = 0;
        if (j2 != 0) {
            j3 = j - j2;
        }
        this.f8702b = j;
        int i = this.g;
        this.f[i % 5] = j3;
        this.g = i + 1;
        long a2 = a(j3);
        this.f8703c += a2;
        return a2;
    }

    public static g b() {
        return a.f8705a;
    }

    /* access modifiers changed from: private */
    public void b(d dVar) {
        i animTask = dVar.getAnimTask();
        if (!dVar.hasFlags(1)) {
            return;
        }
        if (!animTask.b() || animTask.a()) {
            d.a.b.a((T[]) new d[]{dVar});
        }
    }

    private boolean c() {
        if (d()) {
            return false;
        }
        Log.d("miuix_anim", "AnimRunner.endAnimation");
        this.f8704d = false;
        this.f8703c = 0;
        this.f8702b = 0;
        b.a().a((b.C0073b) this);
        return true;
    }

    private boolean d() {
        boolean z;
        Iterator<d> it = this.h.iterator();
        while (true) {
            if (it.hasNext()) {
                if (a(it.next())) {
                    z = true;
                    break;
                }
            } else {
                z = false;
                break;
            }
        }
        this.h.clear();
        return z;
    }

    /* access modifiers changed from: private */
    public void e() {
        if (!this.f8704d) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                f();
            } else {
                f8701a.sendEmptyMessage(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void f() {
        g b2 = b();
        if (!b2.f8704d) {
            Log.d("miuix_anim", "AnimRunner.start");
            b2.f8704d = true;
            b.a().a((b.C0073b) b2, 0);
        }
    }

    public long a(long j) {
        long a2 = a(this.f);
        if (a2 > 0) {
            j = a2;
        }
        if (j > 16) {
            j = 16;
        }
        return (long) Math.ceil((double) (((float) j) / this.e));
    }

    public void a(d dVar, d.a.b.a aVar, d.a.b.a aVar2, d.a.a.g gVar) {
        dVar.executeOnInitialized(new f(this, dVar, aVar, aVar2, gVar));
    }

    public void a(d dVar, C0575b... bVarArr) {
        dVar.post(new d(this, dVar, bVarArr));
    }

    /* access modifiers changed from: package-private */
    public boolean a(d dVar) {
        return !dVar.getAnimTask().a();
    }

    public void b(d dVar, C0575b... bVarArr) {
        dVar.post(new e(this, dVar, bVarArr));
    }

    public boolean doAnimationFrame(long j) {
        long b2 = b(j);
        long j2 = this.f8703c;
        d.a.b.a((Collection<d>) this.h);
        for (d next : this.h) {
            if (next.allowAnimRun() && next.getAnimTask().b()) {
                next.post(new c(this, next, j2, b2));
            }
            b(next);
        }
        return c();
    }
}
