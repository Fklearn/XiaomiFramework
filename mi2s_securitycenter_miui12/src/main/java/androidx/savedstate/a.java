package androidx.savedstate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.f;
import java.util.Map;

@SuppressLint({"RestrictedApi"})
public final class a {

    /* renamed from: a  reason: collision with root package name */
    private a.b.a.b.b<String, b> f1273a = new a.b.a.b.b<>();
    @Nullable

    /* renamed from: b  reason: collision with root package name */
    private Bundle f1274b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f1275c;

    /* renamed from: d  reason: collision with root package name */
    boolean f1276d = true;

    /* renamed from: androidx.savedstate.a$a  reason: collision with other inner class name */
    public interface C0019a {
        void a(@NonNull c cVar);
    }

    public interface b {
        @NonNull
        Bundle a();
    }

    a() {
    }

    @MainThread
    @Nullable
    public Bundle a(@NonNull String str) {
        if (this.f1275c) {
            Bundle bundle = this.f1274b;
            if (bundle == null) {
                return null;
            }
            Bundle bundle2 = bundle.getBundle(str);
            this.f1274b.remove(str);
            if (this.f1274b.isEmpty()) {
                this.f1274b = null;
            }
            return bundle2;
        }
        throw new IllegalStateException("You can consumeRestoredStateForKey only after super.onCreate of corresponding component");
    }

    /* access modifiers changed from: package-private */
    @MainThread
    public void a(@NonNull Bundle bundle) {
        Bundle bundle2 = new Bundle();
        Bundle bundle3 = this.f1274b;
        if (bundle3 != null) {
            bundle2.putAll(bundle3);
        }
        a.b.a.b.b<K, V>.d b2 = this.f1273a.b();
        while (b2.hasNext()) {
            Map.Entry entry = (Map.Entry) b2.next();
            bundle2.putBundle((String) entry.getKey(), ((b) entry.getValue()).a());
        }
        bundle.putBundle("androidx.lifecycle.BundlableSavedStateRegistry.key", bundle2);
    }

    /* access modifiers changed from: package-private */
    @MainThread
    public void a(@NonNull f fVar, @Nullable Bundle bundle) {
        if (!this.f1275c) {
            if (bundle != null) {
                this.f1274b = bundle.getBundle("androidx.lifecycle.BundlableSavedStateRegistry.key");
            }
            fVar.a(new SavedStateRegistry$1(this));
            this.f1275c = true;
            return;
        }
        throw new IllegalStateException("SavedStateRegistry was already restored.");
    }
}
