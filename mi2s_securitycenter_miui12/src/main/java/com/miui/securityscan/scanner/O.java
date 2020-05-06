package com.miui.securityscan.scanner;

import android.content.Context;
import android.os.Handler;
import b.b.c.j.s;
import com.miui.antivirus.model.k;
import com.miui.securityscan.b.g;
import com.miui.securityscan.b.n;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import miui.util.Log;

public class O {

    /* renamed from: a  reason: collision with root package name */
    private static O f7849a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public volatile boolean f7850b = false;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f7851c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public U f7852d;
    private CacheCheckManager e;
    private C0564k f;
    private C0561h g;
    /* access modifiers changed from: private */
    public ScoreManager h;
    /* access modifiers changed from: private */
    public b.b.c.b.b i;
    /* access modifiers changed from: private */
    public b.b.c.f.a j;
    private C0570q k;
    /* access modifiers changed from: private */
    public Handler l;
    /* access modifiers changed from: private */
    public C0559f m;
    /* access modifiers changed from: private */
    public C0567n n;
    private Queue<v> o = new ConcurrentLinkedQueue();
    private Queue<C0568o> p = new ConcurrentLinkedQueue();
    private b q;
    private b r;

    public interface a {
        void a(int i, boolean z);

        void a(AbsModel absModel);

        void b(AbsModel absModel);
    }

    private class b extends Thread {

        /* renamed from: a  reason: collision with root package name */
        private d f7853a;

        /* renamed from: b  reason: collision with root package name */
        private BlockingQueue<C0558e> f7854b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f7855c;

        /* renamed from: d  reason: collision with root package name */
        private C0568o f7856d;
        private String e;

        public b(O o, String str, d dVar, BlockingQueue<C0558e> blockingQueue) {
            this(str, dVar, blockingQueue, false);
        }

        public b(String str, d dVar, BlockingQueue<C0558e> blockingQueue, boolean z) {
            this.e = str;
            this.f7853a = dVar;
            this.f7854b = blockingQueue;
            this.f7855c = z;
            StringBuilder sb = new StringBuilder();
            sb.append("FetchEntryTask blockingQueue == null ? : ");
            sb.append(this.f7854b == null);
            Log.d("SecurityManager", sb.toString());
        }

        public void a(C0568o oVar) {
            this.f7856d = oVar;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
            r1.c();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r5 = this;
                java.lang.String r0 = "SecurityManager"
                com.miui.securityscan.scanner.O$d r1 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                if (r1 == 0) goto L_0x000b
                com.miui.securityscan.scanner.O$d r1 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                r1.b()     // Catch:{ InterruptedException -> 0x0085 }
            L_0x000b:
                com.miui.securityscan.scanner.O r1 = com.miui.securityscan.scanner.O.this     // Catch:{ InterruptedException -> 0x0085 }
                boolean r1 = r1.f7850b     // Catch:{ InterruptedException -> 0x0085 }
                if (r1 != 0) goto L_0x008b
                java.util.concurrent.BlockingQueue<com.miui.securityscan.scanner.e> r1 = r5.f7854b     // Catch:{ InterruptedException -> 0x0085 }
                if (r1 == 0) goto L_0x000b
                com.miui.securityscan.scanner.O r1 = com.miui.securityscan.scanner.O.this     // Catch:{ InterruptedException -> 0x0085 }
                b.b.c.f.a r1 = r1.j     // Catch:{ InterruptedException -> 0x0085 }
                java.lang.String r2 = r5.e     // Catch:{ InterruptedException -> 0x0085 }
                boolean r1 = r1.a((java.lang.String) r2)     // Catch:{ InterruptedException -> 0x0085 }
                if (r1 == 0) goto L_0x002f
                com.miui.securityscan.scanner.O$d r1 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                if (r1 == 0) goto L_0x002f
                com.miui.securityscan.scanner.O$d r1 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
            L_0x002b:
                r1.c()     // Catch:{ InterruptedException -> 0x0085 }
                goto L_0x008b
            L_0x002f:
                java.util.concurrent.BlockingQueue<com.miui.securityscan.scanner.e> r1 = r5.f7854b     // Catch:{ InterruptedException -> 0x0085 }
                r2 = 10
                java.util.concurrent.TimeUnit r4 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ InterruptedException -> 0x0085 }
                java.lang.Object r1 = r1.poll(r2, r4)     // Catch:{ InterruptedException -> 0x0085 }
                com.miui.securityscan.scanner.e r1 = (com.miui.securityscan.scanner.C0558e) r1     // Catch:{ InterruptedException -> 0x0085 }
                if (r1 == 0) goto L_0x0079
                com.miui.securityscan.scanner.O$f r2 = r1.f7892d     // Catch:{ InterruptedException -> 0x0085 }
                com.miui.securityscan.scanner.O$f r3 = com.miui.securityscan.scanner.O.f.FINISH     // Catch:{ InterruptedException -> 0x0085 }
                if (r2 != r3) goto L_0x004a
                com.miui.securityscan.scanner.O$d r1 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                if (r1 == 0) goto L_0x008b
                com.miui.securityscan.scanner.O$d r1 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                goto L_0x002b
            L_0x004a:
                com.miui.securityscan.scanner.O$d r2 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                if (r2 == 0) goto L_0x0053
                com.miui.securityscan.scanner.O$d r2 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                r2.a(r1)     // Catch:{ InterruptedException -> 0x0085 }
            L_0x0053:
                boolean r2 = r5.f7855c     // Catch:{ InterruptedException -> 0x0085 }
                if (r2 == 0) goto L_0x000b
                com.miui.securityscan.scanner.o r2 = r5.f7856d     // Catch:{ InterruptedException -> 0x0085 }
                com.miui.securityscan.scanner.o r3 = com.miui.securityscan.scanner.C0568o.SYSTEM_CONFIG     // Catch:{ InterruptedException -> 0x0085 }
                if (r2 != r3) goto L_0x0063
                r1 = 500(0x1f4, double:2.47E-321)
                java.lang.Thread.sleep(r1)     // Catch:{ InterruptedException -> 0x0085 }
                goto L_0x000b
            L_0x0063:
                com.miui.securityscan.scanner.o r2 = r5.f7856d     // Catch:{ InterruptedException -> 0x0085 }
                com.miui.securityscan.scanner.o r3 = com.miui.securityscan.scanner.C0568o.SYSTEM_APP     // Catch:{ InterruptedException -> 0x0085 }
                if (r2 != r3) goto L_0x000b
                int r2 = r1.f7890b     // Catch:{ InterruptedException -> 0x0085 }
                if (r2 == 0) goto L_0x000b
                r2 = 2000(0x7d0, float:2.803E-42)
                int r1 = r1.f7890b     // Catch:{ InterruptedException -> 0x0085 }
                int r2 = r2 / r1
                int r2 = r2 + 10
                long r1 = (long) r2     // Catch:{ InterruptedException -> 0x0085 }
                java.lang.Thread.sleep(r1)     // Catch:{ InterruptedException -> 0x0085 }
                goto L_0x000b
            L_0x0079:
                java.lang.String r1 = "FetchEntryTask blockingQueue poll timeout"
                miui.util.Log.d(r0, r1)     // Catch:{ InterruptedException -> 0x0085 }
                com.miui.securityscan.scanner.O$d r1 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                if (r1 == 0) goto L_0x008b
                com.miui.securityscan.scanner.O$d r1 = r5.f7853a     // Catch:{ InterruptedException -> 0x0085 }
                goto L_0x002b
            L_0x0085:
                r1 = move-exception
                java.lang.String r2 = "FetchEntryTask InterruptedException"
                miui.util.Log.e(r0, r2, r1)
            L_0x008b:
                r0 = 0
                r5.f7854b = r0
                r5.f7853a = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.scanner.O.b.run():void");
        }
    }

    public interface c {
        void a(GroupModel groupModel);
    }

    public interface d {
        void a(C0558e eVar);

        void b();

        void c();
    }

    public interface e {
        void a();
    }

    public enum f {
        NORMAL,
        FINISH
    }

    private O(Context context) {
        this.f7851c = context;
        this.l = new Handler(context.getMainLooper());
        this.m = new C0559f();
        this.n = new C0567n();
        this.h = ScoreManager.e();
        this.f = C0564k.b(context);
        this.f7852d = U.c(context);
        this.e = CacheCheckManager.a(context);
        this.g = C0561h.a(context);
        this.i = b.b.c.b.b.a(context);
        this.j = b.b.c.f.a.a(context);
        this.k = C0570q.b();
    }

    public static synchronized O a(Context context) {
        O o2;
        synchronized (O.class) {
            if (f7849a == null) {
                f7849a = new O(context.getApplicationContext());
            }
            o2 = f7849a;
        }
        return o2;
    }

    /* access modifiers changed from: private */
    public void a(com.miui.securityscan.b.d dVar, List<com.miui.securitycenter.memory.d> list) {
        Log.d("SecurityManager", "startOptimizeMemoryAfterScanMemory");
        this.f.a(list, (com.miui.securitycenter.memory.a) new M(this, dVar, list));
    }

    /* access modifiers changed from: private */
    public void a(com.miui.securityscan.b.d dVar, List<GroupModel> list, c cVar) {
        Log.d("SecurityManager", "startOptimizeSystemAppAfterScanSystem");
        this.f7852d.a(list, cVar, new K(this, dVar));
    }

    private void a(e eVar) {
        s.a("SecurityManager startScanCacheItem(5)");
        this.e.a(eVar, new G(this));
    }

    private void a(boolean z, com.miui.securityscan.b.d dVar, e eVar) {
        s.a("SecurityManager startScanMemoryItem(4)");
        this.f.a((com.miui.securitycenter.memory.b) new E(this, z, eVar, dVar));
    }

    private void a(boolean z, com.miui.securityscan.b.d dVar, e eVar, c cVar) {
        s.a("SecurityManager startScanSystemApps(3)");
        this.f7852d.a(z, new C(this, z, dVar, cVar, eVar));
    }

    private void a(boolean z, n nVar) {
        s.a("SecurityManager startScanManualItem(1)");
        this.g.a((g) new I(this, nVar, z));
    }

    private void a(boolean z, e eVar) {
        s.a("SecurityManager startScanAutoItem(2)");
        this.f7852d.a((g) new A(this, z, eVar));
    }

    public void a() {
        this.f7850b = true;
        b bVar = this.q;
        if (bVar != null && bVar.isAlive()) {
            this.q.interrupt();
            this.q = null;
        }
        b bVar2 = this.r;
        if (bVar2 != null && bVar2.isAlive()) {
            this.r.interrupt();
            this.r = null;
        }
        this.j.b("com.miui.cleanmaster.action.CHECK_GARBAGE_CHECK");
    }

    public void a(n nVar) {
        this.g.a((g) new H(this, nVar));
    }

    public void a(n nVar, e eVar) {
        s.a("SecurityManager startPredictScan:---------------------------------");
        this.f7850b = false;
        this.o.clear();
        this.o.addAll(Arrays.asList(v.values()));
        this.m.a();
        this.h.u();
        a(true, nVar);
        a(true, eVar);
        a(true, (com.miui.securityscan.b.d) null, eVar, (c) null);
        a(true, (com.miui.securityscan.b.d) null, eVar);
    }

    public void a(c cVar, n nVar, com.miui.securityscan.b.d dVar, e eVar) {
        s.a("SecurityManager startScanAndOptimize:---------------------------------");
        this.f7850b = false;
        this.k.a();
        this.p.clear();
        this.n.a();
        this.p.addAll(Arrays.asList(C0568o.values()));
        this.h.u();
        a(false, nVar);
        a(false, eVar);
        a(false, dVar, eVar, cVar);
        a(false, dVar, eVar);
        a(eVar);
    }

    public void a(C0568o oVar, d dVar) {
        Log.d("SecurityManager", "popOptimizeEntry : item = " + oVar);
        int i2 = z.f7940b[oVar.ordinal()];
        String str = "";
        if (i2 == 1 || i2 == 2) {
            str = "com.miui.guardprovider.action.antivirusservice";
        }
        this.r = new b(str, dVar, this.n.a(oVar), true);
        this.r.a(oVar);
        this.r.start();
    }

    public void a(v vVar, d dVar) {
        Log.d("SecurityManager", "popEntry : item = " + vVar);
        this.q = new b(this, z.f7939a[vVar.ordinal()] != 1 ? "" : "com.miui.guardprovider.action.antivirusservice", dVar, this.m.a(vVar));
        this.q.start();
    }

    public void a(List<k> list) {
        for (k e2 : list) {
            this.h.b(e2.e());
        }
        b.b.c.j.d.a(new N(this, list));
    }

    public C0568o b() {
        return this.p.poll();
    }

    public v c() {
        return this.o.poll();
    }
}
