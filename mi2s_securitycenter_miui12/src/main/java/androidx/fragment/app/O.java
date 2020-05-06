package androidx.fragment.app;

import androidx.annotation.NonNull;
import androidx.lifecycle.f;
import androidx.lifecycle.i;
import androidx.lifecycle.k;

class O implements i {

    /* renamed from: a  reason: collision with root package name */
    private k f910a = null;

    O() {
    }

    @NonNull
    public f a() {
        d();
        return this.f910a;
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull f.a aVar) {
        this.f910a.b(aVar);
    }

    /* access modifiers changed from: package-private */
    public void d() {
        if (this.f910a == null) {
            this.f910a = new k(this);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean e() {
        return this.f910a != null;
    }
}
