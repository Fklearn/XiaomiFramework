package d.a.b;

import d.a.d;
import d.a.g.B;
import d.a.i.b;
import d.a.k;

public class c extends b implements k {

    /* renamed from: b  reason: collision with root package name */
    private d.a.c.a f8646b;

    /* renamed from: c  reason: collision with root package name */
    private int f8647c;

    /* renamed from: d  reason: collision with root package name */
    private d.a.a.a f8648d = new d.a.a.a();
    private boolean e;

    public enum a {
        INIT,
        TARGET
    }

    public c() {
        super(new d[0]);
        this.f8648d.f8626c = b.a(0, 350.0f, 0.9f, 0.86f);
    }

    public k a(int i, d.a.a.a... aVarArr) {
        l lVar;
        a aVar;
        l lVar2 = this.f8645a;
        if (lVar2 != null) {
            if (!this.e) {
                this.e = true;
                lVar2.setTo((Object) a.INIT);
            }
            d.a.a.a[] aVarArr2 = (d.a.a.a[]) d.a.i.a.a((T[]) aVarArr, (T[]) new d.a.a.a[]{this.f8648d});
            if (this.f8647c == i) {
                lVar = this.f8645a;
                aVar = a.INIT;
            } else {
                this.f8645a.getState(a.TARGET).a((B) this.f8646b, i, new long[0]);
                lVar = this.f8645a;
                aVar = a.TARGET;
            }
            lVar.a(aVar, aVarArr2);
        }
        return this;
    }

    public void clean() {
        super.clean();
        this.f8645a = null;
        this.f8646b = null;
        this.f8647c = 0;
    }
}
