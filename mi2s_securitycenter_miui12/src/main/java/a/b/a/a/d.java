package a.b.a.a;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class d implements ThreadFactory {

    /* renamed from: a  reason: collision with root package name */
    private final AtomicInteger f44a = new AtomicInteger(0);

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e f45b;

    d(e eVar) {
        this.f45b = eVar;
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(String.format("arch_disk_io_%d", new Object[]{Integer.valueOf(this.f44a.getAndIncrement())}));
        return thread;
    }
}
