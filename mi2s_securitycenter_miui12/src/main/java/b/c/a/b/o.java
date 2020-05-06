package b.c.a.b;

import android.graphics.Bitmap;
import android.os.Handler;
import b.c.a.b.a.b;
import b.c.a.b.a.e;
import b.c.a.b.a.f;
import b.c.a.b.a.i;
import b.c.a.b.b.b;
import b.c.a.b.d;
import b.c.a.b.d.d;
import b.c.a.c.c;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

final class o implements Runnable, c.a {

    /* renamed from: a  reason: collision with root package name */
    private final j f2061a;

    /* renamed from: b  reason: collision with root package name */
    private final k f2062b;

    /* renamed from: c  reason: collision with root package name */
    private final Handler f2063c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public final h f2064d;
    private final d e;
    private final d f;
    private final d g;
    private final b h;
    final String i;
    private final String j;
    final b.c.a.b.e.a k;
    private final e l;
    final d m;
    final b.c.a.b.f.a n;
    final b.c.a.b.f.b o;
    private final boolean p;
    private f q = f.NETWORK;

    class a extends Exception {
        a() {
        }
    }

    public o(j jVar, k kVar, Handler handler) {
        this.f2061a = jVar;
        this.f2062b = kVar;
        this.f2063c = handler;
        this.f2064d = jVar.f2046a;
        h hVar = this.f2064d;
        this.e = hVar.p;
        this.f = hVar.s;
        this.g = hVar.t;
        this.h = hVar.q;
        this.i = kVar.f2050a;
        this.j = kVar.f2051b;
        this.k = kVar.f2052c;
        this.l = kVar.f2053d;
        this.m = kVar.e;
        this.n = kVar.f;
        this.o = kVar.g;
        this.p = this.m.n();
    }

    private Bitmap a(String str) {
        String str2 = str;
        return this.h.a(new b.c.a.b.b.c(this.j, str2, this.i, this.l, this.k.e(), i(), this.m));
    }

    private void a(b.a aVar, Throwable th) {
        if (!this.p && !j() && !k()) {
            a(new m(this, aVar, th), false, this.f2063c, this.f2061a);
        }
    }

    static void a(Runnable runnable, boolean z, Handler handler, j jVar) {
        if (z) {
            runnable.run();
        } else if (handler == null) {
            jVar.a(runnable);
        } else {
            handler.post(runnable);
        }
    }

    private void b() {
        if (j()) {
            throw new a();
        }
    }

    private boolean b(int i2, int i3) {
        if (j() || k()) {
            return false;
        }
        if (this.o == null) {
            return true;
        }
        a(new l(this, i2, i3), false, this.f2063c, this.f2061a);
        return true;
    }

    private void c() {
        d();
        e();
    }

    private boolean c(int i2, int i3) {
        File file = this.f2064d.o.get(this.i);
        if (file == null || !file.exists()) {
            return false;
        }
        e eVar = new e(i2, i3);
        d.a aVar = new d.a();
        aVar.a(this.m);
        aVar.a(b.c.a.b.a.d.IN_SAMPLE_INT);
        Bitmap a2 = this.h.a(new b.c.a.b.b.c(this.j, d.a.FILE.c(file.getAbsolutePath()), this.i, eVar, i.FIT_INSIDE, i(), aVar.a()));
        if (!(a2 == null || this.f2064d.f == null)) {
            b.c.a.c.d.a("Process image before cache on disk [%s]", this.j);
            a2 = this.f2064d.f.process(a2);
            if (a2 == null) {
                b.c.a.c.d.b("Bitmap processor for disk cache returned null [%s]", this.j);
            }
        }
        if (a2 == null) {
            return false;
        }
        boolean a3 = this.f2064d.o.a(this.i, a2);
        a2.recycle();
        return a3;
    }

    private void d() {
        if (l()) {
            throw new a();
        }
    }

    private void e() {
        if (m()) {
            throw new a();
        }
    }

    private boolean f() {
        if (!this.m.o()) {
            return false;
        }
        b.c.a.c.d.a("Delay %d ms before loading...  [%s]", Integer.valueOf(this.m.c()), this.j);
        try {
            Thread.sleep((long) this.m.c());
            return k();
        } catch (InterruptedException unused) {
            b.c.a.c.d.b("Task was interrupted [%s]", this.j);
            return true;
        }
    }

    private boolean g() {
        InputStream a2 = i().a(this.i, this.m.e());
        if (a2 == null) {
            b.c.a.c.d.b("No stream for image [%s]", this.j);
            return false;
        }
        try {
            return this.f2064d.o.a(this.i, a2, this);
        } finally {
            c.a((Closeable) a2);
        }
    }

    private void h() {
        if (!this.p && !j()) {
            a(new n(this), false, this.f2063c, this.f2061a);
        }
    }

    private b.c.a.b.d.d i() {
        return this.f2061a.c() ? this.f : this.f2061a.d() ? this.g : this.e;
    }

    private boolean j() {
        if (!Thread.interrupted()) {
            return false;
        }
        b.c.a.c.d.a("Task was interrupted [%s]", this.j);
        return true;
    }

    private boolean k() {
        return l() || m();
    }

    private boolean l() {
        if (!this.k.d()) {
            return false;
        }
        b.c.a.c.d.a("ImageAware was collected by GC. Task is cancelled. [%s]", this.j);
        return true;
    }

    private boolean m() {
        if (!(!this.j.equals(this.f2061a.b(this.k)))) {
            return false;
        }
        b.c.a.c.d.a("ImageAware is reused for another image. Task is cancelled. [%s]", this.j);
        return true;
    }

    private boolean n() {
        b.c.a.c.d.a("Cache image on disk [%s]", this.j);
        try {
            boolean g2 = g();
            if (!g2) {
                return g2;
            }
            int i2 = this.f2064d.f2037d;
            int i3 = this.f2064d.e;
            if (i2 <= 0 && i3 <= 0) {
                return g2;
            }
            b.c.a.c.d.a("Resize image in disk cache [%s]", this.j);
            c(i2, i3);
            return g2;
        } catch (IOException unused) {
            b.c.a.c.d.b("Socket time out : tryCacheImageOnDisk", new Object[0]);
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004d, code lost:
        if (r2.getHeight() > 0) goto L_0x00d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x009f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00a1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00a3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a5, code lost:
        r1 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00d0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d1, code lost:
        throw r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00d0 A[ExcHandler: a (r0v3 'e' b.c.a.b.o$a A[CUSTOM_DECLARE]), Splitter:B:1:0x0002] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Bitmap o() {
        /*
            r9 = this;
            r0 = 0
            r1 = 0
            b.c.a.b.h r2 = r9.f2064d     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            b.c.a.a.a.a r2 = r2.o     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            java.lang.String r3 = r9.i     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            java.io.File r2 = r2.get(r3)     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            r3 = 1
            if (r2 == 0) goto L_0x0040
            boolean r4 = r2.exists()     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            if (r4 == 0) goto L_0x0040
            long r4 = r2.length()     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            r6 = 0
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x0040
            java.lang.String r4 = "Load image from disk cache [%s]"
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            java.lang.String r6 = r9.j     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            r5[r0] = r6     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            b.c.a.c.d.a(r4, r5)     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            b.c.a.b.a.f r4 = b.c.a.b.a.f.DISC_CACHE     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            r9.q = r4     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            r9.c()     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            b.c.a.b.d.d$a r4 = b.c.a.b.d.d.a.FILE     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            java.lang.String r2 = r4.c(r2)     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            android.graphics.Bitmap r2 = r9.a((java.lang.String) r2)     // Catch:{ IllegalStateException -> 0x00d2, a -> 0x00d0, SocketTimeoutException -> 0x00c2, IOException -> 0x00b7, OutOfMemoryError -> 0x00af, Throwable -> 0x00a7 }
            goto L_0x0041
        L_0x0040:
            r2 = r1
        L_0x0041:
            if (r2 == 0) goto L_0x004f
            int r4 = r2.getWidth()     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            if (r4 <= 0) goto L_0x004f
            int r4 = r2.getHeight()     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            if (r4 > 0) goto L_0x00d8
        L_0x004f:
            java.lang.String r4 = "Load image from network [%s]"
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            java.lang.String r5 = r9.j     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            r3[r0] = r5     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            b.c.a.c.d.a(r4, r3)     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            b.c.a.b.a.f r3 = b.c.a.b.a.f.NETWORK     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            r9.q = r3     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            java.lang.String r3 = r9.i     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            b.c.a.b.d r4 = r9.m     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            boolean r4 = r4.k()     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            if (r4 == 0) goto L_0x0084
            boolean r4 = r9.n()     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            if (r4 == 0) goto L_0x0084
            b.c.a.b.h r4 = r9.f2064d     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            b.c.a.a.a.a r4 = r4.o     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            java.lang.String r5 = r9.i     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            java.io.File r4 = r4.get(r5)     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            if (r4 == 0) goto L_0x0084
            b.c.a.b.d.d$a r3 = b.c.a.b.d.d.a.FILE     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            java.lang.String r4 = r4.getAbsolutePath()     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            java.lang.String r3 = r3.c(r4)     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
        L_0x0084:
            r9.c()     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            android.graphics.Bitmap r2 = r9.a((java.lang.String) r3)     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            if (r2 == 0) goto L_0x0099
            int r3 = r2.getWidth()     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            if (r3 <= 0) goto L_0x0099
            int r3 = r2.getHeight()     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            if (r3 > 0) goto L_0x00d8
        L_0x0099:
            b.c.a.b.a.b$a r3 = b.c.a.b.a.b.a.DECODING_ERROR     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            r9.a((b.c.a.b.a.b.a) r3, (java.lang.Throwable) r1)     // Catch:{ IllegalStateException -> 0x00d3, a -> 0x00d0, SocketTimeoutException -> 0x00a5, IOException -> 0x00a3, OutOfMemoryError -> 0x00a1, Throwable -> 0x009f }
            goto L_0x00d8
        L_0x009f:
            r0 = move-exception
            goto L_0x00a9
        L_0x00a1:
            r0 = move-exception
            goto L_0x00b1
        L_0x00a3:
            r0 = move-exception
            goto L_0x00b9
        L_0x00a5:
            r1 = move-exception
            goto L_0x00c6
        L_0x00a7:
            r0 = move-exception
            r2 = r1
        L_0x00a9:
            b.c.a.c.d.a((java.lang.Throwable) r0)
            b.c.a.b.a.b$a r1 = b.c.a.b.a.b.a.UNKNOWN
            goto L_0x00be
        L_0x00af:
            r0 = move-exception
            r2 = r1
        L_0x00b1:
            b.c.a.c.d.a((java.lang.Throwable) r0)
            b.c.a.b.a.b$a r1 = b.c.a.b.a.b.a.OUT_OF_MEMORY
            goto L_0x00be
        L_0x00b7:
            r0 = move-exception
            r2 = r1
        L_0x00b9:
            b.c.a.c.d.a((java.lang.Throwable) r0)
            b.c.a.b.a.b$a r1 = b.c.a.b.a.b.a.IO_ERROR
        L_0x00be:
            r9.a((b.c.a.b.a.b.a) r1, (java.lang.Throwable) r0)
            goto L_0x00d8
        L_0x00c2:
            r2 = move-exception
            r8 = r2
            r2 = r1
            r1 = r8
        L_0x00c6:
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r3 = "Socket Time out"
            b.c.a.c.d.b(r3, r0)
            b.c.a.b.a.b$a r0 = b.c.a.b.a.b.a.IO_ERROR
            goto L_0x00d5
        L_0x00d0:
            r0 = move-exception
            throw r0
        L_0x00d2:
            r2 = r1
        L_0x00d3:
            b.c.a.b.a.b$a r0 = b.c.a.b.a.b.a.NETWORK_DENIED
        L_0x00d5:
            r9.a((b.c.a.b.a.b.a) r0, (java.lang.Throwable) r1)
        L_0x00d8:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.b.o.o():android.graphics.Bitmap");
    }

    private boolean p() {
        AtomicBoolean a2 = this.f2061a.a();
        if (a2.get()) {
            synchronized (this.f2061a.b()) {
                if (a2.get()) {
                    b.c.a.c.d.a("ImageLoader is paused. Waiting...  [%s]", this.j);
                    try {
                        this.f2061a.b().wait();
                        b.c.a.c.d.a(".. Resume loading [%s]", this.j);
                    } catch (InterruptedException unused) {
                        b.c.a.c.d.b("Task was interrupted [%s]", this.j);
                        return true;
                    }
                }
            }
        }
        return k();
    }

    /* access modifiers changed from: package-private */
    public String a() {
        return this.i;
    }

    public boolean a(int i2, int i3) {
        return this.p || b(i2, i3);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00fb, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        h();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0103, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0104, code lost:
        r0.unlock();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0107, code lost:
        throw r1;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x00fd */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00d2 A[Catch:{ a -> 0x00fd }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r7 = this;
            boolean r0 = r7.p()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            boolean r0 = r7.f()
            if (r0 == 0) goto L_0x000e
            return
        L_0x000e:
            b.c.a.b.k r0 = r7.f2062b
            java.util.concurrent.locks.ReentrantLock r0 = r0.h
            r1 = 1
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r3 = r7.j
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = "Start display image task [%s]"
            b.c.a.c.d.a(r3, r2)
            boolean r2 = r0.isLocked()
            if (r2 == 0) goto L_0x0030
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r3 = r7.j
            r2[r4] = r3
            java.lang.String r3 = "Image already is loading. Waiting... [%s]"
            b.c.a.c.d.a(r3, r2)
        L_0x0030:
            r0.lock()
            r7.c()     // Catch:{ a -> 0x00fd }
            b.c.a.b.h r2 = r7.f2064d     // Catch:{ a -> 0x00fd }
            b.c.a.a.b.a r2 = r2.n     // Catch:{ a -> 0x00fd }
            java.lang.String r3 = r7.j     // Catch:{ a -> 0x00fd }
            android.graphics.Bitmap r2 = r2.get(r3)     // Catch:{ a -> 0x00fd }
            if (r2 == 0) goto L_0x0059
            boolean r3 = r2.isRecycled()     // Catch:{ a -> 0x00fd }
            if (r3 == 0) goto L_0x0049
            goto L_0x0059
        L_0x0049:
            b.c.a.b.a.f r3 = b.c.a.b.a.f.MEMORY_CACHE     // Catch:{ a -> 0x00fd }
            r7.q = r3     // Catch:{ a -> 0x00fd }
            java.lang.String r3 = "...Get cached bitmap from memory after waiting. [%s]"
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ a -> 0x00fd }
            java.lang.String r6 = r7.j     // Catch:{ a -> 0x00fd }
            r5[r4] = r6     // Catch:{ a -> 0x00fd }
            b.c.a.c.d.a(r3, r5)     // Catch:{ a -> 0x00fd }
            goto L_0x00b1
        L_0x0059:
            android.graphics.Bitmap r2 = r7.o()     // Catch:{ a -> 0x00fd }
            if (r2 != 0) goto L_0x0063
            r0.unlock()
            return
        L_0x0063:
            r7.c()     // Catch:{ a -> 0x00fd }
            r7.b()     // Catch:{ a -> 0x00fd }
            b.c.a.b.d r3 = r7.m     // Catch:{ a -> 0x00fd }
            boolean r3 = r3.q()     // Catch:{ a -> 0x00fd }
            if (r3 == 0) goto L_0x0093
            java.lang.String r3 = "PreProcess image before caching in memory [%s]"
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ a -> 0x00fd }
            java.lang.String r6 = r7.j     // Catch:{ a -> 0x00fd }
            r5[r4] = r6     // Catch:{ a -> 0x00fd }
            b.c.a.c.d.a(r3, r5)     // Catch:{ a -> 0x00fd }
            b.c.a.b.d r3 = r7.m     // Catch:{ a -> 0x00fd }
            b.c.a.b.g.a r3 = r3.i()     // Catch:{ a -> 0x00fd }
            android.graphics.Bitmap r2 = r3.process(r2)     // Catch:{ a -> 0x00fd }
            if (r2 != 0) goto L_0x0093
            java.lang.String r3 = "Pre-processor returned null [%s]"
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ a -> 0x00fd }
            java.lang.String r6 = r7.j     // Catch:{ a -> 0x00fd }
            r5[r4] = r6     // Catch:{ a -> 0x00fd }
            b.c.a.c.d.b(r3, r5)     // Catch:{ a -> 0x00fd }
        L_0x0093:
            if (r2 == 0) goto L_0x00b1
            b.c.a.b.d r3 = r7.m     // Catch:{ a -> 0x00fd }
            boolean r3 = r3.j()     // Catch:{ a -> 0x00fd }
            if (r3 == 0) goto L_0x00b1
            java.lang.String r3 = "Cache image in memory [%s]"
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ a -> 0x00fd }
            java.lang.String r6 = r7.j     // Catch:{ a -> 0x00fd }
            r5[r4] = r6     // Catch:{ a -> 0x00fd }
            b.c.a.c.d.a(r3, r5)     // Catch:{ a -> 0x00fd }
            b.c.a.b.h r3 = r7.f2064d     // Catch:{ a -> 0x00fd }
            b.c.a.a.b.a r3 = r3.n     // Catch:{ a -> 0x00fd }
            java.lang.String r5 = r7.j     // Catch:{ a -> 0x00fd }
            r3.a(r5, r2)     // Catch:{ a -> 0x00fd }
        L_0x00b1:
            if (r2 == 0) goto L_0x00dd
            b.c.a.b.d r3 = r7.m     // Catch:{ a -> 0x00fd }
            boolean r3 = r3.p()     // Catch:{ a -> 0x00fd }
            if (r3 == 0) goto L_0x00dd
            java.lang.String r3 = "PostProcess image before displaying [%s]"
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ a -> 0x00fd }
            java.lang.String r6 = r7.j     // Catch:{ a -> 0x00fd }
            r5[r4] = r6     // Catch:{ a -> 0x00fd }
            b.c.a.c.d.a(r3, r5)     // Catch:{ a -> 0x00fd }
            b.c.a.b.d r3 = r7.m     // Catch:{ a -> 0x00fd }
            b.c.a.b.g.a r3 = r3.h()     // Catch:{ a -> 0x00fd }
            android.graphics.Bitmap r2 = r3.process(r2)     // Catch:{ a -> 0x00fd }
            if (r2 != 0) goto L_0x00dd
            java.lang.String r3 = "Post-processor returned null [%s]"
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ a -> 0x00fd }
            java.lang.String r5 = r7.j     // Catch:{ a -> 0x00fd }
            r1[r4] = r5     // Catch:{ a -> 0x00fd }
            b.c.a.c.d.b(r3, r1)     // Catch:{ a -> 0x00fd }
        L_0x00dd:
            r7.c()     // Catch:{ a -> 0x00fd }
            r7.b()     // Catch:{ a -> 0x00fd }
            r0.unlock()
            b.c.a.b.b r0 = new b.c.a.b.b
            b.c.a.b.k r1 = r7.f2062b
            b.c.a.b.j r3 = r7.f2061a
            b.c.a.b.a.f r4 = r7.q
            r0.<init>(r2, r1, r3, r4)
            boolean r1 = r7.p
            android.os.Handler r2 = r7.f2063c
            b.c.a.b.j r3 = r7.f2061a
            a(r0, r1, r2, r3)
            return
        L_0x00fb:
            r1 = move-exception
            goto L_0x0104
        L_0x00fd:
            r7.h()     // Catch:{ all -> 0x00fb }
            r0.unlock()
            return
        L_0x0104:
            r0.unlock()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.b.o.run():void");
    }
}
