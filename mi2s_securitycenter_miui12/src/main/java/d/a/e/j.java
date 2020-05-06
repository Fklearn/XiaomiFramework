package d.a.e;

import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import d.a.a.g;
import d.a.g.C0575b;
import d.a.g.C0576c;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class j {

    /* renamed from: a  reason: collision with root package name */
    private static a[] f8718a = {new a(), new b(), new c(), new d(), new e(), new f(), new g(), new h(), new i()};

    /* renamed from: b  reason: collision with root package name */
    private ArrayMap<Object, SparseArray<c>> f8719b = new ArrayMap<>();

    /* renamed from: c  reason: collision with root package name */
    private b f8720c = new b((a) null);

    /* renamed from: d  reason: collision with root package name */
    private final d.a.d f8721d;
    private d e;
    private List<k> f = new ArrayList();
    private List<d> g = new ArrayList();

    private static abstract class a {

        /* renamed from: a  reason: collision with root package name */
        private List<l> f8722a;

        private a() {
            this.f8722a = new ArrayList();
        }

        /* synthetic */ a(a aVar) {
            this();
        }

        /* access modifiers changed from: package-private */
        public abstract void a(b bVar, Object obj, List<l> list);

        /* access modifiers changed from: package-private */
        public void b(b bVar, Object obj, List<l> list) {
            if (d.a.i.a.a((T[]) bVar.f8726d)) {
                a(bVar, obj, list);
                return;
            }
            for (l next : list) {
                C0575b[] bVarArr = bVar.f8726d;
                if (bVarArr == null || d.a.i.a.a((T[]) bVarArr, next.f8730a)) {
                    this.f8722a.add(next);
                }
            }
            a(bVar, obj, this.f8722a);
            this.f8722a.clear();
        }
    }

    private static class b {

        /* renamed from: a  reason: collision with root package name */
        k f8723a;

        /* renamed from: b  reason: collision with root package name */
        Object f8724b;

        /* renamed from: c  reason: collision with root package name */
        Object f8725c;

        /* renamed from: d  reason: collision with root package name */
        C0575b[] f8726d;
        b e;
        b f;

        private b() {
        }

        /* synthetic */ b(a aVar) {
            this();
        }
    }

    static class c {

        /* renamed from: a  reason: collision with root package name */
        List<l> f8727a = new ArrayList();

        c() {
        }

        /* access modifiers changed from: package-private */
        public void a(l lVar) {
            if (!this.f8727a.contains(lVar)) {
                this.f8727a.add(lVar);
            }
        }

        /* access modifiers changed from: package-private */
        public void a(Collection<l> collection) {
            for (l a2 : collection) {
                a(a2);
            }
        }
    }

    private static class d {

        /* renamed from: a  reason: collision with root package name */
        Object f8728a;

        /* renamed from: b  reason: collision with root package name */
        int f8729b = -1;

        d(Object obj, int i) {
            this.f8728a = obj;
            this.f8729b = i;
        }

        /* access modifiers changed from: package-private */
        public boolean a(Object obj, int i) {
            Object obj2 = this.f8728a;
            return obj2 != null && this.f8729b == i && obj2.equals(obj);
        }
    }

    private static abstract class e extends a {
        private e() {
            super((a) null);
        }

        /* synthetic */ e(a aVar) {
            this();
        }

        /* access modifiers changed from: package-private */
        public abstract void a(b bVar, Object obj, l lVar);

        /* access modifiers changed from: package-private */
        public void a(b bVar, Object obj, List<l> list) {
            for (l a2 : list) {
                a(bVar, obj, a2);
            }
        }
    }

    public j(d.a.d dVar) {
        this.f8721d = dVar;
    }

    private int a() {
        b bVar = this.f8720c;
        int i = 0;
        while (true) {
            bVar = bVar.e;
            if (bVar == null) {
                return i;
            }
            i++;
        }
    }

    private c a(int i, Object obj) {
        SparseArray sparseArray = this.f8719b.get(obj);
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            this.f8719b.put(obj, sparseArray);
        }
        c cVar = (c) sparseArray.get(i);
        if (cVar != null) {
            return cVar;
        }
        c cVar2 = new c();
        sparseArray.put(i, cVar2);
        return cVar2;
    }

    private void a(int i, Object obj, l lVar, Collection<l> collection) {
        if (a() != 0) {
            c a2 = a(i, obj);
            if (collection != null) {
                a2.a(collection);
            } else if (lVar != null) {
                a2.a(lVar);
            }
            d dVar = this.e;
            if (dVar == null) {
                this.e = new d(obj, i);
                a(obj, i, a2);
                for (d next : this.g) {
                    a(next.f8728a, next.f8729b, a(next.f8729b, next.f8728a));
                }
                this.e = null;
                this.g.clear();
            } else if (!dVar.a(obj, i)) {
                for (d a3 : this.g) {
                    if (a3.a(obj, i)) {
                        return;
                    }
                }
                this.g.add(new d(obj, i));
            }
        }
    }

    private void a(b bVar, Object obj, d.a.a.a aVar) {
        bVar.f8724b = obj;
        bVar.f8725c = aVar.g;
        bVar.f8726d = aVar.i;
    }

    private void a(Object obj, int i, c cVar) {
        b bVar = this.f8720c;
        while (true) {
            bVar = bVar.e;
            if (bVar != null) {
                Object obj2 = bVar.f8725c;
                if (obj2 == null || obj2.equals(obj)) {
                    f8718a[i].b(bVar, obj, cVar.f8727a);
                }
            } else {
                cVar.f8727a.clear();
                return;
            }
        }
    }

    private void a(Object obj, d.a.a.a aVar) {
        if (!aVar.j.isEmpty()) {
            b bVar = this.f8720c;
            while (true) {
                b bVar2 = bVar.e;
                if (bVar2 == null) {
                    break;
                }
                if (aVar.j.contains(bVar2.f8723a)) {
                    this.f.add(bVar2.f8723a);
                    a(bVar2, obj, aVar);
                }
                bVar = bVar2;
            }
            Iterator<k> it = aVar.j.iterator();
            while (it.hasNext()) {
                k next = it.next();
                if (!this.f.contains(next)) {
                    b bVar3 = new b((a) null);
                    bVar3.f8723a = next;
                    a(bVar3, obj, aVar);
                    bVar3.f = bVar;
                    bVar.e = bVar3;
                    bVar = bVar3;
                }
            }
            this.f.clear();
        }
    }

    /* access modifiers changed from: private */
    public static void b(b bVar, Object obj) {
        if (bVar.f8724b.equals(obj)) {
            b bVar2 = bVar.f;
            bVar2.e = bVar.e;
            b bVar3 = bVar.e;
            if (bVar3 != null) {
                bVar3.f = bVar2;
            }
        }
    }

    /* access modifiers changed from: private */
    public static void b(b bVar, Object obj, l lVar) {
        k kVar = bVar.f8723a;
        kVar.onUpdate(obj, lVar.f8730a, lVar.a(), lVar.f8732c);
        C0575b bVar2 = lVar.f8730a;
        if (bVar2 instanceof C0576c) {
            kVar.onUpdate(obj, (C0576c) bVar2, lVar.b(), lVar.f8731b, lVar.f8732c);
            return;
        }
        kVar.onUpdate(obj, bVar2, lVar.a(), lVar.f8731b, lVar.f8732c);
    }

    public void a(Object obj) {
        a(0, obj, (l) null, (Collection<l>) null);
    }

    public void a(Object obj, l lVar) {
        a(6, obj, lVar, (Collection<l>) null);
    }

    public void a(Object obj, List<l> list) {
        a(4, obj, (l) null, list);
    }

    public boolean a(Object obj, g gVar) {
        for (d.a.a.a a2 : gVar.f) {
            a(obj, a2);
        }
        Log.d("miuix_anim", "setListeners for " + this.f8721d.getTargetObject() + ", toTag = " + obj + ", listeners = " + a());
        return this.f8720c.e != null;
    }

    public void b(Object obj) {
        a(7, obj, (l) null, (Collection<l>) null);
    }

    public void b(Object obj, l lVar) {
        a(5, obj, lVar, (Collection<l>) null);
    }

    public void b(Object obj, List<l> list) {
        a(2, obj, (l) null, list);
    }

    public void c(Object obj) {
        a(8, obj, (l) null, (Collection<l>) null);
    }

    public void c(Object obj, l lVar) {
        a(1, obj, lVar, (Collection<l>) null);
    }

    public void c(Object obj, List<l> list) {
        a(3, obj, (l) null, list);
    }
}
