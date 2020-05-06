package androidx.lifecycle;

import a.b.a.b.b;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.f;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class k extends f {

    /* renamed from: b  reason: collision with root package name */
    private a.b.a.b.a<h, a> f991b = new a.b.a.b.a<>();

    /* renamed from: c  reason: collision with root package name */
    private f.b f992c;

    /* renamed from: d  reason: collision with root package name */
    private final WeakReference<i> f993d;
    private int e = 0;
    private boolean f = false;
    private boolean g = false;
    private ArrayList<f.b> h = new ArrayList<>();

    static class a {

        /* renamed from: a  reason: collision with root package name */
        f.b f994a;

        /* renamed from: b  reason: collision with root package name */
        g f995b;

        a(h hVar, f.b bVar) {
            this.f995b = m.a((Object) hVar);
            this.f994a = bVar;
        }

        /* access modifiers changed from: package-private */
        public void a(i iVar, f.a aVar) {
            f.b a2 = k.a(aVar);
            this.f994a = k.a(this.f994a, a2);
            this.f995b.a(iVar, aVar);
            this.f994a = a2;
        }
    }

    public k(@NonNull i iVar) {
        this.f993d = new WeakReference<>(iVar);
        this.f992c = f.b.INITIALIZED;
    }

    static f.b a(f.a aVar) {
        switch (j.f989a[aVar.ordinal()]) {
            case 1:
            case 2:
                return f.b.CREATED;
            case 3:
            case 4:
                return f.b.STARTED;
            case 5:
                return f.b.RESUMED;
            case 6:
                return f.b.DESTROYED;
            default:
                throw new IllegalArgumentException("Unexpected event value " + aVar);
        }
    }

    static f.b a(@NonNull f.b bVar, @Nullable f.b bVar2) {
        return (bVar2 == null || bVar2.compareTo(bVar) >= 0) ? bVar : bVar2;
    }

    private void a(i iVar) {
        Iterator<Map.Entry<h, a>> descendingIterator = this.f991b.descendingIterator();
        while (descendingIterator.hasNext() && !this.g) {
            Map.Entry next = descendingIterator.next();
            a aVar = (a) next.getValue();
            while (aVar.f994a.compareTo(this.f992c) > 0 && !this.g && this.f991b.contains(next.getKey())) {
                f.a c2 = c(aVar.f994a);
                e(a(c2));
                aVar.a(iVar, c2);
                c();
            }
        }
    }

    private void b(i iVar) {
        b<K, V>.d b2 = this.f991b.b();
        while (b2.hasNext() && !this.g) {
            Map.Entry entry = (Map.Entry) b2.next();
            a aVar = (a) entry.getValue();
            while (aVar.f994a.compareTo(this.f992c) < 0 && !this.g && this.f991b.contains(entry.getKey())) {
                e(aVar.f994a);
                aVar.a(iVar, f(aVar.f994a));
                c();
            }
        }
    }

    private boolean b() {
        if (this.f991b.size() == 0) {
            return true;
        }
        f.b bVar = this.f991b.a().getValue().f994a;
        f.b bVar2 = this.f991b.c().getValue().f994a;
        return bVar == bVar2 && this.f992c == bVar2;
    }

    private static f.a c(f.b bVar) {
        int i = j.f990b[bVar.ordinal()];
        if (i == 1) {
            throw new IllegalArgumentException();
        } else if (i == 2) {
            return f.a.ON_DESTROY;
        } else {
            if (i == 3) {
                return f.a.ON_STOP;
            }
            if (i == 4) {
                return f.a.ON_PAUSE;
            }
            if (i != 5) {
                throw new IllegalArgumentException("Unexpected state value " + bVar);
            }
            throw new IllegalArgumentException();
        }
    }

    private f.b c(h hVar) {
        Map.Entry<h, a> b2 = this.f991b.b(hVar);
        f.b bVar = null;
        f.b bVar2 = b2 != null ? b2.getValue().f994a : null;
        if (!this.h.isEmpty()) {
            ArrayList<f.b> arrayList = this.h;
            bVar = arrayList.get(arrayList.size() - 1);
        }
        return a(a(this.f992c, bVar2), bVar);
    }

    private void c() {
        ArrayList<f.b> arrayList = this.h;
        arrayList.remove(arrayList.size() - 1);
    }

    private void d() {
        i iVar = (i) this.f993d.get();
        if (iVar != null) {
            while (!b()) {
                this.g = false;
                if (this.f992c.compareTo(this.f991b.a().getValue().f994a) < 0) {
                    a(iVar);
                }
                Map.Entry<h, a> c2 = this.f991b.c();
                if (!this.g && c2 != null && this.f992c.compareTo(c2.getValue().f994a) > 0) {
                    b(iVar);
                }
            }
            this.g = false;
            return;
        }
        throw new IllegalStateException("LifecycleOwner of this LifecycleRegistry is alreadygarbage collected. It is too late to change lifecycle state.");
    }

    private void d(f.b bVar) {
        if (this.f992c != bVar) {
            this.f992c = bVar;
            if (this.f || this.e != 0) {
                this.g = true;
                return;
            }
            this.f = true;
            d();
            this.f = false;
        }
    }

    private void e(f.b bVar) {
        this.h.add(bVar);
    }

    private static f.a f(f.b bVar) {
        int i = j.f990b[bVar.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return f.a.ON_START;
            }
            if (i == 3) {
                return f.a.ON_RESUME;
            }
            if (i == 4) {
                throw new IllegalArgumentException();
            } else if (i != 5) {
                throw new IllegalArgumentException("Unexpected state value " + bVar);
            }
        }
        return f.a.ON_CREATE;
    }

    @NonNull
    public f.b a() {
        return this.f992c;
    }

    @MainThread
    @Deprecated
    public void a(@NonNull f.b bVar) {
        b(bVar);
    }

    public void a(@NonNull h hVar) {
        i iVar;
        f.b bVar = this.f992c;
        f.b bVar2 = f.b.DESTROYED;
        if (bVar != bVar2) {
            bVar2 = f.b.INITIALIZED;
        }
        a aVar = new a(hVar, bVar2);
        if (this.f991b.b(hVar, aVar) == null && (iVar = (i) this.f993d.get()) != null) {
            boolean z = this.e != 0 || this.f;
            f.b c2 = c(hVar);
            this.e++;
            while (aVar.f994a.compareTo(c2) < 0 && this.f991b.contains(hVar)) {
                e(aVar.f994a);
                aVar.a(iVar, f(aVar.f994a));
                c();
                c2 = c(hVar);
            }
            if (!z) {
                d();
            }
            this.e--;
        }
    }

    public void b(@NonNull f.a aVar) {
        d(a(aVar));
    }

    @MainThread
    public void b(@NonNull f.b bVar) {
        d(bVar);
    }

    public void b(@NonNull h hVar) {
        this.f991b.remove(hVar);
    }
}
