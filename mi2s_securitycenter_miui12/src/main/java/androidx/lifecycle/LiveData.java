package androidx.lifecycle;

import a.b.a.a.c;
import a.b.a.b.b;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.f;
import java.util.Map;

public abstract class LiveData<T> {

    /* renamed from: a  reason: collision with root package name */
    static final Object f965a = new Object();

    /* renamed from: b  reason: collision with root package name */
    final Object f966b = new Object();

    /* renamed from: c  reason: collision with root package name */
    private b<q<? super T>, LiveData<T>.a> f967c = new b<>();

    /* renamed from: d  reason: collision with root package name */
    int f968d = 0;
    private volatile Object e;
    volatile Object f;
    private int g;
    private boolean h;
    private boolean i;
    private final Runnable j;

    class LifecycleBoundObserver extends LiveData<T>.a implements e {
        @NonNull
        final i e;

        LifecycleBoundObserver(@NonNull i iVar, q<? super T> qVar) {
            super(qVar);
            this.e = iVar;
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.e.a().b(this);
        }

        public void a(i iVar, f.a aVar) {
            if (this.e.a().a() == f.b.DESTROYED) {
                LiveData.this.a(this.f969a);
            } else {
                a(b());
            }
        }

        /* access modifiers changed from: package-private */
        public boolean a(i iVar) {
            return this.e == iVar;
        }

        /* access modifiers changed from: package-private */
        public boolean b() {
            return this.e.a().a().a(f.b.STARTED);
        }
    }

    private abstract class a {

        /* renamed from: a  reason: collision with root package name */
        final q<? super T> f969a;

        /* renamed from: b  reason: collision with root package name */
        boolean f970b;

        /* renamed from: c  reason: collision with root package name */
        int f971c = -1;

        a(q<? super T> qVar) {
            this.f969a = qVar;
        }

        /* access modifiers changed from: package-private */
        public void a() {
        }

        /* access modifiers changed from: package-private */
        public void a(boolean z) {
            if (z != this.f970b) {
                this.f970b = z;
                int i = 1;
                boolean z2 = LiveData.this.f968d == 0;
                LiveData liveData = LiveData.this;
                int i2 = liveData.f968d;
                if (!this.f970b) {
                    i = -1;
                }
                liveData.f968d = i2 + i;
                if (z2 && this.f970b) {
                    LiveData.this.a();
                }
                LiveData liveData2 = LiveData.this;
                if (liveData2.f968d == 0 && !this.f970b) {
                    liveData2.b();
                }
                if (this.f970b) {
                    LiveData.this.a((LiveData<T>.a) this);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean a(i iVar) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public abstract boolean b();
    }

    public LiveData() {
        Object obj = f965a;
        this.e = obj;
        this.f = obj;
        this.g = -1;
        this.j = new n(this);
    }

    private static void a(String str) {
        if (!c.b().a()) {
            throw new IllegalStateException("Cannot invoke " + str + " on a background" + " thread");
        }
    }

    private void b(LiveData<T>.a aVar) {
        if (aVar.f970b) {
            if (!aVar.b()) {
                aVar.a(false);
                return;
            }
            int i2 = aVar.f971c;
            int i3 = this.g;
            if (i2 < i3) {
                aVar.f971c = i3;
                aVar.f969a.a(this.e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void a() {
    }

    /* access modifiers changed from: package-private */
    public void a(@Nullable LiveData<T>.a aVar) {
        if (this.h) {
            this.i = true;
            return;
        }
        this.h = true;
        do {
            this.i = false;
            if (aVar == null) {
                b<K, V>.d b2 = this.f967c.b();
                while (b2.hasNext()) {
                    b((a) ((Map.Entry) b2.next()).getValue());
                    if (this.i) {
                        break;
                    }
                }
            } else {
                b(aVar);
                aVar = null;
            }
        } while (this.i);
        this.h = false;
    }

    @MainThread
    public void a(@NonNull i iVar, @NonNull q<? super T> qVar) {
        a("observe");
        if (iVar.a().a() != f.b.DESTROYED) {
            LifecycleBoundObserver lifecycleBoundObserver = new LifecycleBoundObserver(iVar, qVar);
            a b2 = this.f967c.b(qVar, lifecycleBoundObserver);
            if (b2 != null && !b2.a(iVar)) {
                throw new IllegalArgumentException("Cannot add the same observer with different lifecycles");
            } else if (b2 == null) {
                iVar.a().a(lifecycleBoundObserver);
            }
        }
    }

    @MainThread
    public void a(@NonNull q<? super T> qVar) {
        a("removeObserver");
        a remove = this.f967c.remove(qVar);
        if (remove != null) {
            remove.a();
            remove.a(false);
        }
    }

    /* access modifiers changed from: protected */
    @MainThread
    public void a(T t) {
        a("setValue");
        this.g++;
        this.e = t;
        a((LiveData<T>.a) null);
    }

    /* access modifiers changed from: protected */
    public void b() {
    }
}
