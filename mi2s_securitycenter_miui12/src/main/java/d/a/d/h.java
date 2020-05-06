package d.a.d;

import android.util.Log;
import d.a.a.g;
import d.a.d;
import d.a.e.l;
import d.a.g.C0575b;
import d.a.g.C0576c;
import d.a.h.c;
import d.a.i.b;

public class h {

    /* renamed from: a  reason: collision with root package name */
    public d f8706a;

    /* renamed from: b  reason: collision with root package name */
    public C0575b f8707b;

    /* renamed from: c  reason: collision with root package name */
    public g f8708c;

    /* renamed from: d  reason: collision with root package name */
    public b.a f8709d;
    public int e = 0;
    public c f;
    public Object g;
    public long h;
    Number i;
    long j;
    h k;
    private long l = -1;
    private Number m;
    private long n;
    private l o = new l();

    private boolean a(d dVar, c cVar) {
        if (this.f8707b instanceof C0576c) {
            return j.b(dVar, cVar, this, this.i, this.n);
        }
        return j.a(dVar, cVar, this, this.i, this.n);
    }

    private void c(long j2) {
        this.l = j2;
        c cVar = this.f;
        if (cVar != null) {
            cVar.o();
        }
    }

    private void g() {
        Number number = this.m;
        if (number != null) {
            C0575b bVar = this.f8707b;
            if (bVar instanceof C0576c) {
                this.f8706a.setIntValue((C0576c) bVar, number.intValue());
            } else {
                this.f8706a.setValue(bVar, number.floatValue());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public long a() {
        c cVar = this.f;
        if (cVar == null) {
            return 0;
        }
        return cVar.g();
    }

    /* access modifiers changed from: package-private */
    public void a(g gVar) {
        this.f8708c = gVar;
        this.f8709d = gVar.a(this.f8707b);
        this.h = gVar.b(this.g, this.f8707b);
    }

    /* access modifiers changed from: package-private */
    public void a(h hVar) {
        this.e = 2;
        this.k = hVar;
        hVar.e = -1;
    }

    /* access modifiers changed from: package-private */
    public void a(k kVar) {
        this.m = kVar.f8717d.get(this.f8707b);
    }

    /* access modifiers changed from: package-private */
    public void a(k kVar, long j2) {
        boolean a2 = b.a(this.f8709d.f8781a);
        this.f8709d = kVar.f8715b.a(this.f8707b);
        boolean a3 = b.a(this.f8709d.f8781a);
        b(kVar);
        Log.d("miuix_anim", "update anim for " + this.f8707b.getName() + ", to = " + this.g + ", value " + this.i + ", newEase = " + this.f8709d);
        if (this.f == null || a2 != a3 || !a3) {
            Log.d("miuix_anim", "update anim, clear old and begin new");
            c cVar = this.f;
            if (cVar != null) {
                cVar.b();
                this.m = null;
                this.f.b(kVar.f8715b);
            } else {
                this.f = d.a.h.d.a(this.f8706a, this.g, this.f8707b, kVar.f8715b);
            }
            a(this.f8706a, j2);
            return;
        }
        Log.d("miuix_anim", "update anim values");
        this.f.b(kVar.f8715b);
        a(this.f8706a, this.f);
    }

    /* access modifiers changed from: package-private */
    public void a(d dVar, long j2) {
        this.e = 1;
        this.o.c();
        if (this.f == null) {
            this.f = d.a.h.d.a(dVar, this.g, this.f8707b, this.f8708c);
        }
        c(j2);
        g();
        float c2 = this.f8708c.c(this.g, this.f8707b);
        if (c2 != Float.MAX_VALUE) {
            Log.d("miuix_anim", "AnimRunningInfo, begin " + this.f8707b.getName() + ", fromSpeed = " + c2);
            dVar.setVelocity(this.f8707b, (double) c2);
        }
        if (!a(dVar, this.f)) {
            a(true);
        } else if (!this.f.k()) {
            Log.d("miuix_anim", "AnimRunningInfo, begin " + this.f8707b.getName() + ", toTag = " + this.g + ", target object = " + dVar.getTargetObject());
            this.f.p();
        }
    }

    /* access modifiers changed from: package-private */
    public void a(boolean z) {
        if (d()) {
            StringBuilder sb = new StringBuilder();
            sb.append("AnimRunningInfo, stop ");
            sb.append(this.f8707b.getName());
            sb.append(", toTag = ");
            sb.append(this.g);
            sb.append(", property = ");
            sb.append(this.f8707b.getName());
            sb.append(", anim.getCurrentValue = ");
            sb.append(this.f8707b instanceof C0576c ? (float) this.f.d() : this.f.e());
            Log.d("miuix_anim", sb.toString());
            this.e = 3;
            if (z) {
                this.f.c();
            } else {
                this.o.f8733d = true;
                this.f.a();
            }
            h hVar = this.k;
            if (hVar != null) {
                hVar.e = 0;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean a(long j2) {
        return this.l < j2;
    }

    /* access modifiers changed from: package-private */
    public void b(long j2) {
        c cVar;
        if (d() && (cVar = this.f) != null) {
            cVar.a(j2);
        }
    }

    /* access modifiers changed from: package-private */
    public void b(k kVar) {
        this.i = kVar.e.get(this.f8707b);
        this.g = kVar.f8716c;
        Long l2 = kVar.f.get(this.f8707b);
        if (l2 != null) {
            this.n = l2.longValue();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean b() {
        return this.e == 3;
    }

    /* access modifiers changed from: package-private */
    public boolean c() {
        return b.a(this.f8709d.f8781a) && this.f8706a.getVelocity(this.f8707b) != 0.0d;
    }

    /* access modifiers changed from: package-private */
    public boolean d() {
        int i2 = this.e;
        return i2 == 1 || i2 == 2;
    }

    /* access modifiers changed from: package-private */
    public void e() {
        a(false);
    }

    /* access modifiers changed from: package-private */
    public l f() {
        l lVar;
        Number number;
        float f2;
        l lVar2;
        C0575b bVar = this.f8707b;
        if (bVar instanceof C0576c) {
            lVar = this.o;
            number = Integer.valueOf(this.f8706a.getIntValue((C0576c) bVar));
        } else {
            lVar = this.o;
            number = Float.valueOf(this.f8706a.getValue(bVar));
        }
        lVar.a(number);
        this.o.f8730a = this.f8707b;
        if (b.a(this.f8709d.f8781a)) {
            lVar2 = this.o;
            f2 = (float) this.f8706a.getVelocity(this.f8707b);
        } else {
            lVar2 = this.o;
            f2 = 0.0f;
        }
        lVar2.f8731b = f2;
        l lVar3 = this.o;
        lVar3.f = this.f;
        lVar3.f8732c = b();
        return this.o;
    }
}
