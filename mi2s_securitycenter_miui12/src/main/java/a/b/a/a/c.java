package a.b.a.a;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.util.concurrent.Executor;

@RestrictTo({RestrictTo.a.f223b})
public class c extends f {

    /* renamed from: a  reason: collision with root package name */
    private static volatile c f40a;
    @NonNull

    /* renamed from: b  reason: collision with root package name */
    private static final Executor f41b = new a();
    @NonNull

    /* renamed from: c  reason: collision with root package name */
    private static final Executor f42c = new b();
    @NonNull

    /* renamed from: d  reason: collision with root package name */
    private f f43d = this.e;
    @NonNull
    private f e = new e();

    private c() {
    }

    @NonNull
    public static c b() {
        if (f40a != null) {
            return f40a;
        }
        synchronized (c.class) {
            if (f40a == null) {
                f40a = new c();
            }
        }
        return f40a;
    }

    public void a(Runnable runnable) {
        this.f43d.a(runnable);
    }

    public boolean a() {
        return this.f43d.a();
    }

    public void b(Runnable runnable) {
        this.f43d.b(runnable);
    }
}
