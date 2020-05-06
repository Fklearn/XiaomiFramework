package androidx.lifecycle;

import androidx.lifecycle.a;
import androidx.lifecycle.f;

class ReflectiveGenericLifecycleObserver implements g {

    /* renamed from: a  reason: collision with root package name */
    private final Object f973a;

    /* renamed from: b  reason: collision with root package name */
    private final a.C0017a f974b = a.f976a.a(this.f973a.getClass());

    ReflectiveGenericLifecycleObserver(Object obj) {
        this.f973a = obj;
    }

    public void a(i iVar, f.a aVar) {
        this.f974b.a(iVar, aVar, this.f973a);
    }
}
