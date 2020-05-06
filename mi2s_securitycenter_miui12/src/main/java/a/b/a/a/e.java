package a.b.a.a;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestrictTo({RestrictTo.a.f223b})
public class e extends f {

    /* renamed from: a  reason: collision with root package name */
    private final Object f46a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private final ExecutorService f47b = Executors.newFixedThreadPool(2, new d(this));
    @Nullable

    /* renamed from: c  reason: collision with root package name */
    private volatile Handler f48c;

    public void a(Runnable runnable) {
        this.f47b.execute(runnable);
    }

    public boolean a() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public void b(Runnable runnable) {
        if (this.f48c == null) {
            synchronized (this.f46a) {
                if (this.f48c == null) {
                    this.f48c = new Handler(Looper.getMainLooper());
                }
            }
        }
        this.f48c.post(runnable);
    }
}
