package androidx.lifecycle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

public class t {

    /* renamed from: a  reason: collision with root package name */
    private final a f1003a;

    /* renamed from: b  reason: collision with root package name */
    private final u f1004b;

    public interface a {
        @NonNull
        <T extends s> T a(@NonNull Class<T> cls);
    }

    static abstract class b implements a {
        b() {
        }

        @NonNull
        public <T extends s> T a(@NonNull Class<T> cls) {
            throw new UnsupportedOperationException("create(String, Class<?>) must be called on implementaions of KeyedFactory");
        }

        @NonNull
        public abstract <T extends s> T a(@NonNull String str, @NonNull Class<T> cls);
    }

    public t(@NonNull u uVar, @NonNull a aVar) {
        this.f1003a = aVar;
        this.f1004b = uVar;
    }

    @MainThread
    @NonNull
    public <T extends s> T a(@NonNull Class<T> cls) {
        String canonicalName = cls.getCanonicalName();
        if (canonicalName != null) {
            return a("androidx.lifecycle.ViewModelProvider.DefaultKey:" + canonicalName, cls);
        }
        throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
    }

    @MainThread
    @NonNull
    public <T extends s> T a(@NonNull String str, @NonNull Class<T> cls) {
        T a2 = this.f1004b.a(str);
        if (cls.isInstance(a2)) {
            return a2;
        }
        a aVar = this.f1003a;
        T a3 = aVar instanceof b ? ((b) aVar).a(str, cls) : aVar.a(cls);
        this.f1004b.a(str, a3);
        return a3;
    }
}
