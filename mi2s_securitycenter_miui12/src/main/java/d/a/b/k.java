package d.a.b;

import d.a.a.a;
import d.a.d;
import d.a.g.C0575b;
import d.a.i.b;
import d.a.l;

public class k extends b implements l {

    /* renamed from: b  reason: collision with root package name */
    private boolean f8675b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f8676c;

    /* renamed from: d  reason: collision with root package name */
    private a f8677d = new a();

    public k(d... dVarArr) {
        super(dVarArr);
        a(true);
    }

    private l.a a(l.a... aVarArr) {
        return aVarArr.length > 0 ? aVarArr[0] : l.a.HIDE;
    }

    private a[] a(l.a aVar, a... aVarArr) {
        a aVar2;
        float[] fArr;
        b.a aVar3;
        if (!this.f8676c && !this.f8675b) {
            aVar2 = this.f8677d;
            if (aVar == l.a.SHOW) {
                aVar3 = b.a(16, 300.0f);
                aVar2.f8626c = aVar3;
                return (a[]) d.a.i.a.a((T[]) aVarArr, (T[]) new a[]{this.f8677d});
            }
            fArr = new float[]{1.0f, 0.15f};
        } else if (this.f8676c && !this.f8675b) {
            aVar2 = this.f8677d;
            fArr = aVar == l.a.SHOW ? new float[]{0.6f, 0.35f} : new float[]{0.75f, 0.2f};
        } else if (!this.f8676c) {
            aVar2 = this.f8677d;
            fArr = aVar == l.a.SHOW ? new float[]{0.75f, 0.35f} : new float[]{0.75f, 0.25f};
        } else {
            aVar2 = this.f8677d;
            fArr = aVar == l.a.SHOW ? new float[]{0.65f, 0.35f} : new float[]{0.75f, 0.25f};
        }
        aVar3 = b.a(-2, fArr);
        aVar2.f8626c = aVar3;
        return (a[]) d.a.i.a.a((T[]) aVarArr, (T[]) new a[]{this.f8677d});
    }

    public l a(float f, l.a... aVarArr) {
        this.f8645a.getState(a(aVarArr)).a(a(14), f, new long[0]);
        return this;
    }

    public l a(boolean z) {
        C0575b a2 = a(14);
        C0575b a3 = a(4);
        if (z) {
            a state = this.f8645a.getState(l.a.SHOW);
            state.f(a3);
            state.a(a2, 1.0f, new long[0]);
            a state2 = this.f8645a.getState(l.a.HIDE);
            state2.f(a3);
            state2.a(a2, 0.0f, new long[0]);
        } else {
            a state3 = this.f8645a.getState(l.a.SHOW);
            state3.f(a2);
            state3.a(a3, 1.0f, new long[0]);
            a state4 = this.f8645a.getState(l.a.HIDE);
            state4.f(a2);
            state4.a(a3, 0.0f, new long[0]);
        }
        return this;
    }

    public void b(a... aVarArr) {
        a(l.a.HIDE, l.a.SHOW);
        l lVar = this.f8645a;
        l.a aVar = l.a.SHOW;
        lVar.a(aVar, a(aVar, aVarArr));
    }

    public void clean() {
        super.clean();
        this.f8676c = false;
        this.f8675b = false;
    }

    public l setHide() {
        a(l.a.SHOW, l.a.HIDE);
        this.f8645a.setTo((Object) l.a.HIDE);
        return this;
    }

    public l setShowDelay(long j) {
        this.f8645a.getState(l.a.SHOW).b().f8624a = j;
        return this;
    }
}
