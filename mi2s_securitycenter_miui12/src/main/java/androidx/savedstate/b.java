package androidx.savedstate;

import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.f;

public final class b {

    /* renamed from: a  reason: collision with root package name */
    private final c f1277a;

    /* renamed from: b  reason: collision with root package name */
    private final a f1278b = new a();

    private b(c cVar) {
        this.f1277a = cVar;
    }

    @NonNull
    public static b a(@NonNull c cVar) {
        return new b(cVar);
    }

    @NonNull
    public a a() {
        return this.f1278b;
    }

    @MainThread
    public void a(@Nullable Bundle bundle) {
        f a2 = this.f1277a.a();
        if (a2.a() == f.b.INITIALIZED) {
            a2.a(new Recreator(this.f1277a));
            this.f1278b.a(a2, bundle);
            return;
        }
        throw new IllegalStateException("Restarter must be created only during owner's initialization stage");
    }

    @MainThread
    public void b(@NonNull Bundle bundle) {
        this.f1278b.a(bundle);
    }
}
