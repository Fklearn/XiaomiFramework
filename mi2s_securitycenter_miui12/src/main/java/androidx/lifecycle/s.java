package androidx.lifecycle;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class s {
    @Nullable

    /* renamed from: a  reason: collision with root package name */
    private final Map<String, Object> f1001a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    private volatile boolean f1002b = false;

    private static void a(Object obj) {
        if (obj instanceof Closeable) {
            try {
                ((Closeable) obj).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @MainThread
    public final void a() {
        this.f1002b = true;
        Map<String, Object> map = this.f1001a;
        if (map != null) {
            synchronized (map) {
                for (Object a2 : this.f1001a.values()) {
                    a(a2);
                }
            }
        }
        b();
    }

    /* access modifiers changed from: protected */
    public void b() {
    }
}
