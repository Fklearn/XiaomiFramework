package com.miui.firstaidkit;

import android.content.Context;
import android.os.Handler;
import com.miui.securityscan.scanner.C0558e;
import com.miui.securityscan.scanner.O;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import miui.util.Log;

public class j {

    /* renamed from: a  reason: collision with root package name */
    private static j f3943a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public volatile boolean f3944b = false;

    /* renamed from: c  reason: collision with root package name */
    private Context f3945c;

    /* renamed from: d  reason: collision with root package name */
    private l f3946d;
    /* access modifiers changed from: private */
    public m e;
    private Queue<n> f = new ConcurrentLinkedQueue();
    private a g;
    /* access modifiers changed from: private */
    public o h;
    /* access modifiers changed from: private */
    public int i;

    private static class a extends Thread {

        /* renamed from: a  reason: collision with root package name */
        private com.miui.firstaidkit.a.b f3947a;

        /* renamed from: b  reason: collision with root package name */
        private BlockingQueue<C0558e> f3948b;

        /* renamed from: c  reason: collision with root package name */
        private Random f3949c = new Random();

        /* renamed from: d  reason: collision with root package name */
        private final WeakReference<j> f3950d;

        public a(j jVar, com.miui.firstaidkit.a.b bVar, BlockingQueue<C0558e> blockingQueue) {
            this.f3950d = new WeakReference<>(jVar);
            this.f3947a = bVar;
            this.f3948b = blockingQueue;
        }

        public void run() {
            j jVar = (j) this.f3950d.get();
            if (jVar != null) {
                while (true) {
                    try {
                        if (jVar.f3944b) {
                            break;
                        } else if (this.f3948b != null) {
                            C0558e poll = this.f3948b.poll(10, TimeUnit.SECONDS);
                            if (poll == null) {
                                Log.d("FetchEntryTask", "FetchEntryTask blockingQueue poll timeout");
                                if (this.f3947a != null) {
                                    this.f3947a.a(0);
                                }
                            } else if (poll.f7892d == O.f.FINISH) {
                                Thread.sleep(((long) this.f3949c.nextInt(500)) + 200);
                                if (this.f3947a != null) {
                                    this.f3947a.a(poll.a());
                                }
                            } else {
                                if (this.f3947a != null) {
                                    this.f3947a.a(poll);
                                }
                                Thread.sleep(200);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("FetchEntryTask", "FetchEntryTask InterruptedException", e);
                    }
                }
                this.f3948b = null;
                this.f3947a = null;
            }
        }
    }

    public interface b {
        void a(n nVar);
    }

    public j(Context context) {
        this.f3945c = context;
        this.f3946d = l.a(this.f3945c);
        this.e = new m();
        this.h = o.f();
        this.f3944b = false;
    }

    public static j a(Context context) {
        if (f3943a == null) {
            f3943a = new j(context.getApplicationContext());
        }
        return f3943a;
    }

    private void b(b bVar, Handler handler) {
        this.f3946d.a(handler, "ConsumePower", new h(this, bVar));
    }

    private void c(b bVar, Handler handler) {
        this.f3946d.a(handler, "Internet", new f(this, bVar));
    }

    private void d(b bVar, Handler handler) {
        this.f3946d.a(handler, "Operation", new g(this, bVar));
    }

    private void e(b bVar, Handler handler) {
        this.f3946d.a(handler, "Other", new i(this, bVar));
    }

    private void f(b bVar, Handler handler) {
        this.f3946d.a(handler, "Performance", new e(this, bVar));
    }

    public void a() {
        this.f3944b = true;
        a aVar = this.g;
        if (aVar != null && aVar.isAlive()) {
            this.g.interrupt();
            this.g = null;
        }
    }

    public void a(Handler handler) {
        a((b) null, handler);
    }

    public void a(b bVar, Handler handler) {
        this.i = 0;
        this.f3944b = false;
        this.f.clear();
        this.f.addAll(Arrays.asList(n.values()));
        this.e.a();
        f(bVar, handler);
        c(bVar, handler);
        d(bVar, handler);
        b(bVar, handler);
        e(bVar, handler);
    }

    public void a(n nVar, com.miui.firstaidkit.a.b bVar) {
        this.g = new a(this, bVar, this.e.a(nVar));
        this.g.start();
    }

    public int b() {
        return this.i;
    }

    public n c() {
        return this.f.poll();
    }
}
