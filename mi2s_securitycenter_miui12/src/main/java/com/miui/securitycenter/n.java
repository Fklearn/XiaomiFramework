package com.miui.securitycenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static n f7497a;

    /* renamed from: b  reason: collision with root package name */
    private ExecutorService f7498b = Executors.newFixedThreadPool(4, new k(this));

    /* renamed from: c  reason: collision with root package name */
    private ExecutorService f7499c = Executors.newFixedThreadPool(2, new m(this));

    private n() {
    }

    public static synchronized n a() {
        n nVar;
        synchronized (n.class) {
            if (f7497a == null) {
                f7497a = new n();
            }
            nVar = f7497a;
        }
        return nVar;
    }

    public void a(Runnable runnable) {
        this.f7498b.execute(runnable);
    }

    public void b(Runnable runnable) {
        this.f7499c.execute(runnable);
    }
}
