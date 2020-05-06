package androidx.activity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class d {

    /* renamed from: a  reason: collision with root package name */
    private boolean f216a;

    /* renamed from: b  reason: collision with root package name */
    private CopyOnWriteArrayList<a> f217b = new CopyOnWriteArrayList<>();

    public d(boolean z) {
        this.f216a = z;
    }

    @MainThread
    public abstract void a();

    /* access modifiers changed from: package-private */
    public void a(@NonNull a aVar) {
        this.f217b.add(aVar);
    }

    @MainThread
    public final void a(boolean z) {
        this.f216a = z;
    }

    /* access modifiers changed from: package-private */
    public void b(@NonNull a aVar) {
        this.f217b.remove(aVar);
    }

    @MainThread
    public final boolean b() {
        return this.f216a;
    }

    @MainThread
    public final void c() {
        Iterator<a> it = this.f217b.iterator();
        while (it.hasNext()) {
            it.next().cancel();
        }
    }
}
