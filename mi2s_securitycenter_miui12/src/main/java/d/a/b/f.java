package d.a.b;

import android.util.ArrayMap;
import d.a.a.a;
import d.a.a.g;
import d.a.d.i;
import d.a.e.k;
import d.a.g.C0575b;
import d.a.g.C0576c;
import d.a.g.e;
import d.a.h;
import d.a.i.b;
import d.a.i.d;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class f implements l {

    /* renamed from: a  reason: collision with root package name */
    private static final d.a.g.f f8655a = new d.a.g.f("defaultProperty");

    /* renamed from: b  reason: collision with root package name */
    private static final e f8656b = new e("defaultIntProperty");

    /* renamed from: c  reason: collision with root package name */
    private static final d.a<l> f8657c = new d();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public d.a.d f8658d;
    private a e = new a("defaultTo");
    private a f = new a("defaultSetTo");
    private a g = new a("autoSetTo");
    private d.a.d h;
    private a i;
    private a j;
    private List<C0575b> k = new ArrayList();
    private Map<Object, a> l = new ArrayMap();
    private Object m = this.e;
    private boolean n = true;

    f(d.a.d dVar) {
        this.f8658d = dVar;
    }

    private int a(a aVar, g gVar, Object obj, Object obj2, int i2, Object... objArr) {
        int i3;
        C0575b a2;
        if (b(gVar, obj) || (a2 = a(obj, obj2)) == null) {
            i3 = 0;
        } else {
            if (!a(a2)) {
                i2++;
            }
            i3 = a(aVar, a2, i2, objArr);
        }
        return i3 > 0 ? i2 + i3 : i2 + 1;
    }

    private int a(a aVar, C0575b bVar, int i2, Object... objArr) {
        Object a2;
        if (bVar == null || (a2 = a(i2, objArr)) == null || !a(aVar, bVar, a2)) {
            return 0;
        }
        return a(bVar, i2 + 1, objArr) ? 2 : 1;
    }

    private g a(a aVar, Object... objArr) {
        g gVar = new g();
        gVar.a(new a());
        b(aVar);
        a(aVar, gVar, objArr);
        return gVar;
    }

    private a a(Object obj, boolean z) {
        if (obj instanceof a) {
            return (a) obj;
        }
        a aVar = this.l.get(obj);
        if (aVar != null || !z) {
            return aVar;
        }
        a aVar2 = new a(obj);
        a(aVar2);
        return aVar2;
    }

    private a a(Object obj, Object... objArr) {
        a aVar;
        if (objArr.length > 0) {
            aVar = a(objArr[0], false);
            if (aVar == null) {
                aVar = c(objArr);
            }
        } else {
            aVar = null;
        }
        return aVar == null ? getState(obj) : aVar;
    }

    public static l a(d.a.d... dVarArr) {
        if (dVarArr == null || dVarArr.length == 0) {
            return null;
        }
        if (dVarArr.length == 1) {
            return new f(dVarArr[0]);
        }
        f[] fVarArr = new f[dVarArr.length];
        for (int i2 = 0; i2 < dVarArr.length; i2++) {
            fVarArr[i2] = new f(dVarArr[i2]);
        }
        return (l) d.a(l.class, f8657c, fVarArr);
    }

    private C0575b a(Object obj, Object obj2) {
        Class<?> cls = null;
        if (obj instanceof C0575b) {
            return (C0575b) obj;
        }
        if (obj instanceof String) {
            if (obj2 != null) {
                cls = obj2.getClass();
            }
            return getTarget().createProperty((String) obj, cls);
        } else if (obj instanceof Float) {
            return f8655a;
        } else {
            if (!(obj instanceof Integer)) {
                return null;
            }
            C0575b property = getTarget().getProperty(((Integer) obj).intValue());
            return property == null ? f8656b : property;
        }
    }

    private h a(Object obj, g gVar) {
        d.a.d dVar = this.f8658d;
        if (dVar == null) {
            return this;
        }
        if ((obj instanceof Integer) || (obj instanceof Float)) {
            setTo(obj, gVar);
            return this;
        }
        dVar.executeOnInitialized(new e(this, obj, gVar));
        return this;
    }

    private h a(Object obj, Object obj2, g gVar) {
        if (this.n) {
            this.m = obj2;
            a state = getState(obj2);
            a aVar = this.e;
            if (state != aVar) {
                aVar.a(gVar);
            }
            d.a.d.g.b().a(this.f8658d, obj != null ? getState(obj) : null, getState(obj2), gVar);
        }
        return this;
    }

    private Object a(int i2, Object... objArr) {
        if (i2 < objArr.length) {
            return objArr[i2];
        }
        return null;
    }

    private void a(a aVar, Object obj) {
        if (obj instanceof k) {
            aVar.a((k) obj);
        } else if (obj instanceof b.a) {
            aVar.a((b.a) obj);
        }
    }

    private void a(a aVar, g gVar, Object... objArr) {
        if (objArr.length != 0) {
            int equals = aVar.c().equals(objArr[0]);
            while (equals < objArr.length) {
                int i2 = equals + 1;
                equals = a(aVar, gVar, objArr[equals], i2 < objArr.length ? objArr[i2] : null, equals, objArr);
            }
        }
    }

    private boolean a(g gVar, Object obj) {
        if (obj instanceof a) {
            gVar.a((a) obj);
            return true;
        } else if (!(obj instanceof g)) {
            return false;
        } else {
            gVar.a((g) obj);
            return false;
        }
    }

    private boolean a(a aVar, C0575b bVar, Object obj) {
        boolean z = obj instanceof Integer;
        if (!z && !(obj instanceof Float) && !(obj instanceof Double)) {
            return false;
        }
        if (bVar instanceof C0576c) {
            aVar.a(bVar, c(obj, z), new long[0]);
            return true;
        }
        aVar.a(bVar, b(obj, z), new long[0]);
        return true;
    }

    private boolean a(C0575b bVar) {
        return bVar == f8655a || bVar == f8656b;
    }

    private boolean a(C0575b bVar, int i2, Object... objArr) {
        if (i2 >= objArr.length) {
            return false;
        }
        Float f2 = objArr[i2];
        if (!(f2 instanceof Float)) {
            return false;
        }
        getTarget().setVelocity(bVar, (double) f2.floatValue());
        return true;
    }

    private float b(Object obj, boolean z) {
        return z ? (float) ((Integer) obj).intValue() : ((Float) obj).floatValue();
    }

    private void b() {
        if (this.h == null) {
            this.h = d.a.b.a("predictTarget");
            this.i = new a("predictFrom");
            this.j = new a("predictTo");
        } else {
            this.i.a();
            this.j.a();
        }
        d.a.d target = getTarget();
        for (C0575b next : this.j.d()) {
            this.h.setMinVisibleChange((Object) next, target.getMinVisibleChange(next));
        }
    }

    private void b(a aVar) {
        if (aVar == this.f || aVar == this.e || aVar == this.g) {
            aVar.a();
        }
    }

    private boolean b(g gVar, Object obj) {
        if ((obj instanceof k) || (obj instanceof b.a)) {
            a(gVar.a(), obj);
            return true;
        } else if (!obj.getClass().isArray()) {
            return a(gVar, obj);
        } else {
            int length = Array.getLength(obj);
            boolean z = false;
            for (int i2 = 0; i2 < length; i2++) {
                z = a(gVar, Array.get(obj, i2)) || z;
            }
            return z;
        }
    }

    private int c(Object obj, boolean z) {
        return z ? ((Integer) obj).intValue() : (int) ((Float) obj).floatValue();
    }

    private a c(Object... objArr) {
        Object obj = objArr[0];
        Object obj2 = objArr.length > 1 ? objArr[1] : null;
        if (!(obj instanceof String) || !(obj2 instanceof String)) {
            return null;
        }
        return a(obj, true);
    }

    public a a() {
        if (this.m == null) {
            this.m = this.e;
        }
        return getState(this.m);
    }

    public h a(Object obj, Object obj2, a... aVarArr) {
        a(obj, obj2, g.a(aVarArr));
        return this;
    }

    public h a(Object obj, a... aVarArr) {
        if ((obj instanceof a) || this.l.get(obj) != null) {
            a((Object) null, (Object) getState(obj), aVarArr);
            return this;
        } else if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            Object[] objArr = new Object[(aVarArr.length + length)];
            System.arraycopy(obj, 0, objArr, 0, length);
            System.arraycopy(aVarArr, 0, objArr, length, aVarArr.length);
            to(objArr);
            return this;
        } else {
            to(obj, aVarArr);
            return this;
        }
    }

    public void a(a aVar) {
        this.l.put(aVar.c(), aVar);
    }

    public void a(C0575b... bVarArr) {
        d.a.d.g.b().a(this.f8658d, bVarArr);
    }

    public void a(Object... objArr) {
        ArrayList arrayList = new ArrayList();
        for (C0575b bVar : objArr) {
            if (bVar instanceof C0575b) {
                arrayList.add(bVar);
            } else if (bVar instanceof String) {
                arrayList.add(new d.a.g.f((String) bVar));
            }
        }
        d.a.d.g.b().b(this.f8658d, (C0575b[]) arrayList.toArray(new C0575b[0]));
    }

    public long b(Object... objArr) {
        b();
        g a2 = a(this.j, objArr);
        a.a(getTarget(), this.i, this.j);
        long j2 = 0;
        i a3 = d.a.d.g.a(this.h, 0, this.i, this.j, a2);
        long a4 = d.a.d.g.b().a(16);
        while (a3.b() && !a3.a()) {
            a3.a(j2, a4, new long[0]);
            j2 += a4;
        }
        return j2;
    }

    public h b(Object obj, a... aVarArr) {
        a(obj, g.a(aVarArr));
        return this;
    }

    public void cancel() {
        d.a.d.g.b().a(this.f8658d, new C0575b[0]);
    }

    public void clean() {
        cancel();
    }

    public a getState(Object obj) {
        return a(obj, true);
    }

    public d.a.d getTarget() {
        return this.f8658d;
    }

    public h setTo(Object obj) {
        b(obj, new a[0]);
        return this;
    }

    public h setTo(Object... objArr) {
        a a2 = a((Object) this.f, objArr);
        a((Object) a2, a(a2, objArr));
        return this;
    }

    public h to(Object... objArr) {
        a a2 = a((Object) a(), objArr);
        a((Object) null, (Object) a2, a(a2, objArr));
        return this;
    }
}
