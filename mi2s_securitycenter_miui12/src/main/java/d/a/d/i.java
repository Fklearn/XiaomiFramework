package d.a.d;

import android.util.ArrayMap;
import android.util.Log;
import d.a.a.g;
import d.a.d;
import d.a.e.j;
import d.a.e.l;
import d.a.g.C0575b;
import d.a.g.C0576c;
import d.a.i.a;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private ConcurrentLinkedQueue<h> f8710a = new ConcurrentLinkedQueue<>();

    /* renamed from: b  reason: collision with root package name */
    private List<k> f8711b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private List<h> f8712c = new ArrayList();

    /* renamed from: d  reason: collision with root package name */
    private Map<Object, List<l>> f8713d = new ArrayMap();
    private List<h> e = new ArrayList();
    private d f;
    private j g;
    private j h;

    public i(d dVar) {
        this.f = dVar;
        this.h = new j(dVar);
    }

    private List<l> a(Object obj) {
        List<l> list = this.f8713d.get(obj);
        if (list != null) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        this.f8713d.put(obj, arrayList);
        return arrayList;
    }

    private void a(int i, List<h> list) {
        Iterator<h> it = this.f8710a.iterator();
        while (it.hasNext()) {
            h next = it.next();
            if (next.e == i) {
                list.add(next);
            }
        }
    }

    private void a(long j) {
        this.f8712c.clear();
        a(2, this.f8712c);
        if (!this.f8712c.isEmpty()) {
            for (h next : this.f8712c) {
                long d2 = next.f8708c.d(next.g, next.f8707b);
                long a2 = next.a();
                if (a2 >= d2 || d2 - a2 < j) {
                    next.e();
                }
            }
            e();
        }
    }

    private void a(long j, long j2) {
        Iterator<h> it = this.f8710a.iterator();
        while (it.hasNext()) {
            h next = it.next();
            if (next.d()) {
                next.b(j2);
                if (a(next, j)) {
                    next.a(!next.f().f8733d);
                }
            }
        }
        e();
    }

    private void a(h hVar) {
        hVar.k = null;
        hVar.e();
        Log.d("miuix_anim", "cancelAnim, cancel " + hVar.f8707b.getName());
    }

    private void a(h hVar, h hVar2) {
        int i = hVar.e;
        if (i == 0) {
            hVar.e = 3;
        } else if (i == 1) {
            long d2 = hVar.f8708c.d(hVar.g, hVar.f8707b);
            if (hVar.a() < d2) {
                Log.d("miuix_anim", "handleSameAnim, " + hVar.f8707b.getName() + ", prev.config.minDuration = " + d2 + ", prev.runningTime = " + hVar.a() + ", pending current info");
                hVar.a(hVar2);
                return;
            }
            hVar.e();
        } else if (i == 2) {
            hVar.k.e();
            hVar.k = hVar2;
        }
    }

    private void a(h hVar, k kVar, List<C0575b> list, long j) {
        for (C0575b next : kVar.e.keySet()) {
            if (hVar.f8707b.equals(next)) {
                hVar.a(kVar, j);
                list.add(next);
                return;
            }
        }
    }

    private void a(d dVar, long j) {
        this.f8712c.clear();
        a(0, this.f8712c);
        if (!this.f8712c.isEmpty()) {
            ArrayList arrayList = new ArrayList();
            for (h next : this.f8712c) {
                if (j - next.j >= next.f8708c.a(next.g, next.f8707b)) {
                    Object obj = next.g;
                    if (!arrayList.contains(obj)) {
                        arrayList.add(obj);
                        this.h.a(next.g);
                    }
                    next.a(dVar, j);
                    if (!c(next)) {
                        this.h.c(next.g, next.f());
                    }
                }
            }
        }
    }

    private void a(d dVar, C0575b bVar, Number number) {
        if (bVar instanceof C0576c) {
            int intValue = number.intValue();
            if (intValue != Integer.MAX_VALUE) {
                dVar.setIntValue((C0576c) bVar, intValue);
                return;
            }
            return;
        }
        float a2 = j.a(dVar, bVar, number.floatValue());
        if (a2 != Float.MAX_VALUE) {
            dVar.setValue(bVar, a2);
        }
    }

    private void a(j jVar, Object obj, List<l> list) {
        jVar.b(obj, list);
        jVar.c(obj, list);
        jVar.a(obj, list);
    }

    private void a(boolean z, C0575b... bVarArr) {
        ArrayList arrayList = new ArrayList();
        for (k next : this.f8711b) {
            if (a(next, z, bVarArr)) {
                arrayList.add(next);
            }
        }
        this.f8711b.removeAll(arrayList);
    }

    private void a(long... jArr) {
        long j = jArr.length > 0 ? jArr[0] : 0;
        if (j > 0) {
            Iterator<h> it = this.f8710a.iterator();
            while (it.hasNext()) {
                h next = it.next();
                if (!a.a(next.h, j)) {
                    this.e.add(next);
                }
            }
            this.f8710a.removeAll(this.e);
        }
    }

    private boolean a(h hVar, long j) {
        return hVar.a(j) && !hVar.f.k();
    }

    private boolean a(h hVar, Object obj) {
        return hVar.e != 3 && hVar.g.equals(obj);
    }

    private boolean a(k kVar, boolean z, C0575b... bVarArr) {
        for (C0575b next : kVar.e.keySet()) {
            if (bVarArr.length <= 0 || a(next, bVarArr)) {
                if (z) {
                    a(kVar.f8714a, next, kVar.e.get(next));
                }
                kVar.e.remove(next);
            }
        }
        return kVar.e.keySet().isEmpty();
    }

    private boolean a(C0575b bVar, C0575b... bVarArr) {
        for (C0575b equals : bVarArr) {
            if (bVar.equals(equals)) {
                return true;
            }
        }
        return false;
    }

    private boolean a(Object obj, boolean z) {
        if (a((Collection<h>) this.f8710a, obj) || a((Collection<h>) this.e, obj)) {
            return false;
        }
        if (z) {
            this.h.b(obj);
            return true;
        }
        this.h.c(obj);
        return true;
    }

    private boolean a(Collection<h> collection, Object obj) {
        for (h a2 : collection) {
            if (a(a2, obj)) {
                return true;
            }
        }
        return false;
    }

    private void b(long j) {
        if (this.f8710a.isEmpty() && !this.f8711b.isEmpty()) {
            b(j, this.f8711b.remove(0));
        }
    }

    private void b(long j, k kVar) {
        ArrayList<C0575b> arrayList = new ArrayList<>();
        Iterator<h> it = this.f8710a.iterator();
        while (it.hasNext()) {
            h next = it.next();
            if (next.g.equals(kVar.f8716c) && kVar.e.get(next.f8707b) != null && kVar.f8715b.a(next.g, next.f8707b) == 0) {
                a(next, kVar, arrayList, j);
            }
        }
        for (C0575b remove : arrayList) {
            kVar.e.remove(remove);
        }
        if (kVar.e.isEmpty()) {
            Log.d("miuix_anim", "startTransition, trans.toPropValues.isEmpty, target = " + this.f.getTargetObject() + ", trans.tag = " + kVar.f8716c);
            return;
        }
        this.h.a(kVar.f8716c, kVar.f8715b);
        for (h next2 : a.a(this.f, kVar).values()) {
            next2.j = j;
            if (next2.f8708c.a(next2.g, next2.f8707b) > 0) {
                next2.h |= 2;
            }
            if (!a.a(next2.h, 2)) {
                b(next2);
            }
            this.f8710a.add(next2);
        }
    }

    private void b(h hVar) {
        Iterator<h> it = this.f8710a.iterator();
        while (it.hasNext()) {
            h next = it.next();
            if (next != hVar && !a.a(next.h, 2) && next.f8707b.equals(hVar.f8707b)) {
                a(next, hVar);
                return;
            }
        }
    }

    private void c() {
        l f2;
        for (List<l> clear : this.f8713d.values()) {
            clear.clear();
        }
        Iterator<h> it = this.f8710a.iterator();
        while (it.hasNext()) {
            h next = it.next();
            if (!this.f8710a.isEmpty()) {
                if (!(next.e == 0 || (f2 = next.f()) == null)) {
                    List<l> a2 = a(next.g);
                    if (!a2.contains(f2)) {
                        a2.add(f2);
                    }
                    if (f2.f8732c) {
                        Log.d("miuix_anim", "anim end, tag = " + next.g + ", property = " + next.f8707b.getName());
                    }
                }
            } else {
                return;
            }
        }
    }

    private boolean c(h hVar) {
        Iterator<h> it = this.f8710a.iterator();
        boolean z = false;
        while (it.hasNext()) {
            h next = it.next();
            if (next != hVar && next.f8707b.equals(hVar.f8707b) && next.d()) {
                z = true;
                next.e();
                this.f8710a.remove(next);
            }
        }
        return z;
    }

    private List<h> d() {
        Collection collection;
        ArrayList arrayList = new ArrayList();
        if (this.e.isEmpty()) {
            collection = this.f8710a;
        } else {
            arrayList.addAll(this.f8710a);
            collection = this.e;
        }
        arrayList.addAll(collection);
        return arrayList;
    }

    private void e() {
        c();
        ArrayList<Object> arrayList = new ArrayList<>();
        for (Map.Entry next : this.f8713d.entrySet()) {
            Object key = next.getKey();
            List list = (List) next.getValue();
            if (!list.isEmpty()) {
                a(this.h, key, (List<l>) list);
                if (!a(key, false)) {
                }
            }
            arrayList.add(key);
        }
        for (Object remove : arrayList) {
            this.f8713d.remove(remove);
        }
    }

    private void f() {
        this.f8712c.clear();
        Iterator<h> it = this.f8710a.iterator();
        while (it.hasNext()) {
            h next = it.next();
            if (next.e == 3) {
                this.f8712c.add(next);
            }
        }
        this.f8710a.removeAll(this.f8712c);
    }

    private void g() {
        this.f8710a.addAll(this.e);
        this.e.clear();
    }

    public void a(long j, long j2, long... jArr) {
        a(jArr);
        if (!this.f8710a.isEmpty()) {
            a(this.f, j);
            a(j, j2);
            a(j2);
            f();
            b(j);
        }
        g();
    }

    /* access modifiers changed from: package-private */
    public void a(long j, k kVar) {
        if (a.a(kVar.f8715b.b((Object) null, (C0575b) null), 1)) {
            this.f8711b.add(kVar);
        } else {
            b(j, kVar);
        }
    }

    public void a(d.a.b.a aVar, g gVar) {
        Object c2 = aVar.c();
        if (this.g == null) {
            this.g = new j(this.f);
        }
        if (this.g.a(c2, gVar)) {
            this.g.a(c2);
            ArrayList<l> arrayList = new ArrayList<>();
            for (C0575b next : aVar.d()) {
                l lVar = new l();
                lVar.f8730a = next;
                lVar.f8731b = (float) this.f.getVelocity(next);
                lVar.a(Float.valueOf(next instanceof C0576c ? (float) this.f.getIntValue((C0576c) next) : this.f.getValue(next)));
                arrayList.add(lVar);
                lVar.f8732c = true;
            }
            for (l c3 : arrayList) {
                this.g.c(c2, c3);
            }
            a(this.g, c2, (List<l>) arrayList);
            this.g.c(c2);
        }
    }

    public void a(C0575b... bVarArr) {
        ArrayList<Object> arrayList = new ArrayList<>();
        boolean z = false;
        for (h next : d()) {
            if (next.d() && (bVarArr.length <= 0 || a(next.f8707b, bVarArr))) {
                a(next);
                Object obj = next.g;
                if (!arrayList.contains(obj)) {
                    arrayList.add(obj);
                }
                this.h.a(obj, next.f());
                z = true;
            }
        }
        if (z) {
            f();
            for (Object a2 : arrayList) {
                a(a2, true);
            }
            arrayList.clear();
        }
        if (bVarArr.length > 0) {
            a(false, bVarArr);
        }
    }

    public boolean a() {
        return this.f8710a.isEmpty();
    }

    public boolean a(C0575b bVar) {
        Iterator<h> it = this.f8710a.iterator();
        while (it.hasNext()) {
            if (it.next().f8707b.equals(bVar)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void b(C0575b... bVarArr) {
        ArrayList<Object> arrayList = new ArrayList<>();
        boolean z = !this.f8710a.isEmpty();
        for (h next : d()) {
            if (bVarArr.length <= 0 || a(next.f8707b, bVarArr)) {
                if (next.f != null) {
                    next.a(true);
                    next.f().e = true;
                } else {
                    a(next.f8706a, next.f8707b, next.i);
                }
                if (!arrayList.contains(next.g)) {
                    arrayList.add(next.g);
                }
                this.h.b(next.g, next.f());
            }
        }
        a(true, bVarArr);
        f();
        if (z) {
            for (Object a2 : arrayList) {
                a(a2, false);
            }
        }
        arrayList.clear();
    }

    public boolean b() {
        return !this.f8710a.isEmpty() || !this.f8711b.isEmpty();
    }
}
