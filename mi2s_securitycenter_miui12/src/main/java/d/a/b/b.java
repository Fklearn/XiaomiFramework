package d.a.b;

import d.a.d;
import d.a.g;
import d.a.g.C0575b;

public abstract class b implements g {

    /* renamed from: a  reason: collision with root package name */
    l f8645a;

    b(d... dVarArr) {
        this.f8645a = f.a(dVarArr);
    }

    /* access modifiers changed from: package-private */
    public C0575b a(int i) {
        l lVar = this.f8645a;
        if (lVar != null) {
            return lVar.getTarget().getProperty(i);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, Object obj2) {
        l lVar = this.f8645a;
        if (lVar != null) {
            a.a(lVar.getTarget(), this.f8645a.getState(obj), this.f8645a.getState(obj2));
        }
    }

    public void a(Object... objArr) {
        l lVar = this.f8645a;
        if (lVar != null) {
            lVar.a(objArr);
        }
    }

    public void clean() {
        l lVar = this.f8645a;
        if (lVar != null) {
            lVar.clean();
        }
    }
}
