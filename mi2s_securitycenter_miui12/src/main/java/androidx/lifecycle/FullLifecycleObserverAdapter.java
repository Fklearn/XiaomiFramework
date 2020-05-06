package androidx.lifecycle;

import androidx.lifecycle.f;

class FullLifecycleObserverAdapter implements g {

    /* renamed from: a  reason: collision with root package name */
    private final b f962a;

    /* renamed from: b  reason: collision with root package name */
    private final g f963b;

    FullLifecycleObserverAdapter(b bVar, g gVar) {
        this.f962a = bVar;
        this.f963b = gVar;
    }

    public void a(i iVar, f.a aVar) {
        switch (c.f983a[aVar.ordinal()]) {
            case 1:
                this.f962a.a(iVar);
                break;
            case 2:
                this.f962a.f(iVar);
                break;
            case 3:
                this.f962a.b(iVar);
                break;
            case 4:
                this.f962a.c(iVar);
                break;
            case 5:
                this.f962a.d(iVar);
                break;
            case 6:
                this.f962a.e(iVar);
                break;
            case 7:
                throw new IllegalArgumentException("ON_ANY must not been send by anybody");
        }
        g gVar = this.f963b;
        if (gVar != null) {
            gVar.a(iVar, aVar);
        }
    }
}
