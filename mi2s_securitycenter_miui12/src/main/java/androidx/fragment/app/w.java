package androidx.fragment.app;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.s;
import androidx.lifecycle.t;
import androidx.lifecycle.u;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

class w extends s {

    /* renamed from: c  reason: collision with root package name */
    private static final t.a f951c = new v();

    /* renamed from: d  reason: collision with root package name */
    private final HashSet<Fragment> f952d = new HashSet<>();
    private final HashMap<String, w> e = new HashMap<>();
    private final HashMap<String, u> f = new HashMap<>();
    private final boolean g;
    private boolean h = false;
    private boolean i = false;

    w(boolean z) {
        this.g = z;
    }

    @NonNull
    static w a(u uVar) {
        return (w) new t(uVar, f951c).a(w.class);
    }

    /* access modifiers changed from: package-private */
    public boolean a(@NonNull Fragment fragment) {
        return this.f952d.add(fragment);
    }

    /* access modifiers changed from: protected */
    public void b() {
        if (t.f937c) {
            Log.d("FragmentManager", "onCleared called for " + this);
        }
        this.h = true;
    }

    /* access modifiers changed from: package-private */
    public void b(@NonNull Fragment fragment) {
        if (t.f937c) {
            Log.d("FragmentManager", "Clearing non-config state for " + fragment);
        }
        w wVar = this.e.get(fragment.f);
        if (wVar != null) {
            wVar.b();
            this.e.remove(fragment.f);
        }
        u uVar = this.f.get(fragment.f);
        if (uVar != null) {
            uVar.a();
            this.f.remove(fragment.f);
        }
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public w c(@NonNull Fragment fragment) {
        w wVar = this.e.get(fragment.f);
        if (wVar != null) {
            return wVar;
        }
        w wVar2 = new w(this.g);
        this.e.put(fragment.f, wVar2);
        return wVar2;
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public Collection<Fragment> c() {
        return this.f952d;
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public u d(@NonNull Fragment fragment) {
        u uVar = this.f.get(fragment.f);
        if (uVar != null) {
            return uVar;
        }
        u uVar2 = new u();
        this.f.put(fragment.f, uVar2);
        return uVar2;
    }

    /* access modifiers changed from: package-private */
    public boolean d() {
        return this.h;
    }

    /* access modifiers changed from: package-private */
    public boolean e(@NonNull Fragment fragment) {
        return this.f952d.remove(fragment);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || w.class != obj.getClass()) {
            return false;
        }
        w wVar = (w) obj;
        return this.f952d.equals(wVar.f952d) && this.e.equals(wVar.e) && this.f.equals(wVar.f);
    }

    /* access modifiers changed from: package-private */
    public boolean f(@NonNull Fragment fragment) {
        if (!this.f952d.contains(fragment)) {
            return true;
        }
        return this.g ? this.h : !this.i;
    }

    public int hashCode() {
        return (((this.f952d.hashCode() * 31) + this.e.hashCode()) * 31) + this.f.hashCode();
    }

    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder("FragmentManagerViewModel{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append("} Fragments (");
        Iterator<Fragment> it = this.f952d.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") Child Non Config (");
        Iterator<String> it2 = this.e.keySet().iterator();
        while (it2.hasNext()) {
            sb.append(it2.next());
            if (it2.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") ViewModelStores (");
        Iterator<String> it3 = this.f.keySet().iterator();
        while (it3.hasNext()) {
            sb.append(it3.next());
            if (it3.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
