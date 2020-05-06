package b.c.a.b;

import b.c.a.b.e.a;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

class j {

    /* renamed from: a  reason: collision with root package name */
    final h f2046a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Executor f2047b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Executor f2048c;

    /* renamed from: d  reason: collision with root package name */
    private Executor f2049d;
    private final Map<Integer, String> e = Collections.synchronizedMap(new HashMap());
    private final Map<String, ReentrantLock> f = new WeakHashMap();
    private final AtomicBoolean g = new AtomicBoolean(false);
    private final AtomicBoolean h = new AtomicBoolean(false);
    private final AtomicBoolean i = new AtomicBoolean(false);
    private final Object j = new Object();

    j(h hVar) {
        this.f2046a = hVar;
        this.f2047b = hVar.g;
        this.f2048c = hVar.h;
        this.f2049d = a.c();
    }

    private Executor g() {
        h hVar = this.f2046a;
        return a.a(hVar.k, hVar.l, hVar.m);
    }

    /* access modifiers changed from: private */
    public void h() {
        if (!this.f2046a.i && ((ExecutorService) this.f2047b).isShutdown()) {
            this.f2047b = g();
        }
        if (!this.f2046a.j && ((ExecutorService) this.f2048c).isShutdown()) {
            this.f2048c = g();
        }
    }

    /* access modifiers changed from: package-private */
    public AtomicBoolean a() {
        return this.g;
    }

    /* access modifiers changed from: package-private */
    public ReentrantLock a(String str) {
        ReentrantLock reentrantLock = this.f.get(str);
        if (reentrantLock != null) {
            return reentrantLock;
        }
        ReentrantLock reentrantLock2 = new ReentrantLock();
        this.f.put(str, reentrantLock2);
        return reentrantLock2;
    }

    /* access modifiers changed from: package-private */
    public void a(a aVar) {
        this.e.remove(Integer.valueOf(aVar.getId()));
    }

    /* access modifiers changed from: package-private */
    public void a(a aVar, String str) {
        this.e.put(Integer.valueOf(aVar.getId()), str);
    }

    /* access modifiers changed from: package-private */
    public void a(o oVar) {
        this.f2049d.execute(new i(this, oVar));
    }

    /* access modifiers changed from: package-private */
    public void a(p pVar) {
        h();
        this.f2048c.execute(pVar);
    }

    /* access modifiers changed from: package-private */
    public void a(Runnable runnable) {
        this.f2049d.execute(runnable);
    }

    /* access modifiers changed from: package-private */
    public Object b() {
        return this.j;
    }

    /* access modifiers changed from: package-private */
    public String b(a aVar) {
        return this.e.get(Integer.valueOf(aVar.getId()));
    }

    /* access modifiers changed from: package-private */
    public boolean c() {
        return this.h.get();
    }

    /* access modifiers changed from: package-private */
    public boolean d() {
        return this.i.get();
    }

    /* access modifiers changed from: package-private */
    public void e() {
        this.g.set(true);
    }

    /* access modifiers changed from: package-private */
    public void f() {
        this.g.set(false);
        synchronized (this.j) {
            this.j.notifyAll();
        }
    }
}
