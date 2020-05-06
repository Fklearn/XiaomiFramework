package androidx.lifecycle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.util.concurrent.atomic.AtomicReference;

public abstract class f {
    @NonNull
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP})

    /* renamed from: a  reason: collision with root package name */
    AtomicReference<Object> f984a = new AtomicReference<>();

    public enum a {
        ON_CREATE,
        ON_START,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY,
        ON_ANY
    }

    public enum b {
        DESTROYED,
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED;

        public boolean a(@NonNull b bVar) {
            return compareTo(bVar) >= 0;
        }
    }

    @MainThread
    @NonNull
    public abstract b a();

    @MainThread
    public abstract void a(@NonNull h hVar);

    @MainThread
    public abstract void b(@NonNull h hVar);
}
