package miuix.recyclerview.widget;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.O;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class c extends O {
    private static final List<RecyclerView.u> h = new ArrayList();
    private static final List<b> i = new ArrayList();
    private static final List<a> j = new ArrayList();
    private ArrayList<RecyclerView.u> k = new ArrayList<>();
    private ArrayList<RecyclerView.u> l = new ArrayList<>();
    private ArrayList<b> m = new ArrayList<>();
    private ArrayList<a> n = new ArrayList<>();
    private ArrayList<ArrayList<RecyclerView.u>> o = new ArrayList<>();
    private ArrayList<ArrayList<b>> p = new ArrayList<>();
    private ArrayList<ArrayList<a>> q = new ArrayList<>();
    private ArrayList<RecyclerView.u> r = new ArrayList<>();
    private ArrayList<RecyclerView.u> s = new ArrayList<>();
    private ArrayList<RecyclerView.u> t = new ArrayList<>();
    private ArrayList<RecyclerView.u> u = new ArrayList<>();

    static class a {

        /* renamed from: a  reason: collision with root package name */
        RecyclerView.u f8921a;

        /* renamed from: b  reason: collision with root package name */
        RecyclerView.u f8922b;

        /* renamed from: c  reason: collision with root package name */
        int f8923c;

        /* renamed from: d  reason: collision with root package name */
        int f8924d;
        int e;
        int f;

        private a(RecyclerView.u uVar, RecyclerView.u uVar2) {
            this.f8921a = uVar;
            this.f8922b = uVar2;
        }

        a(RecyclerView.u uVar, RecyclerView.u uVar2, int i, int i2, int i3, int i4) {
            this(uVar, uVar2);
            this.f8923c = i;
            this.f8924d = i2;
            this.e = i3;
            this.f = i4;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.f8921a + ", newHolder=" + this.f8922b + ", fromX=" + this.f8923c + ", fromY=" + this.f8924d + ", toX=" + this.e + ", toY=" + this.f + '}';
        }
    }

    static class b {

        /* renamed from: a  reason: collision with root package name */
        RecyclerView.u f8925a;

        /* renamed from: b  reason: collision with root package name */
        int f8926b;

        /* renamed from: c  reason: collision with root package name */
        int f8927c;

        /* renamed from: d  reason: collision with root package name */
        int f8928d;
        int e;

        b(RecyclerView.u uVar, int i, int i2, int i3, int i4) {
            this.f8925a = uVar;
            this.f8926b = i;
            this.f8927c = i2;
            this.f8928d = i3;
            this.e = i4;
        }

        public String toString() {
            return "MoveInfo{holder=" + this.f8925a + ", fromX=" + this.f8926b + ", fromY=" + this.f8927c + ", toX=" + this.f8928d + ", toY=" + this.e + '}';
        }
    }

    static void a(View view) {
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
        view.setAlpha(1.0f);
    }

    private void a(List<RecyclerView.u> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            b(list.get(size).itemView);
        }
        list.clear();
    }

    private void a(List<a> list, RecyclerView.u uVar) {
        for (int size = list.size() - 1; size >= 0; size--) {
            a aVar = list.get(size);
            if (a(aVar, uVar) && aVar.f8921a == null && aVar.f8922b == null) {
                list.remove(aVar);
            }
        }
    }

    private boolean a(a aVar, RecyclerView.u uVar) {
        boolean z = false;
        if (aVar.f8922b == uVar) {
            aVar.f8922b = null;
        } else if (aVar.f8921a != uVar) {
            return false;
        } else {
            aVar.f8921a = null;
            z = true;
        }
        uVar.itemView.setAlpha(1.0f);
        uVar.itemView.setTranslationX(0.0f);
        uVar.itemView.setTranslationY(0.0f);
        a(uVar, z);
        return true;
    }

    private void b(View view) {
        d.a.b.b((T[]) new View[]{view});
    }

    private void c(a aVar) {
        RecyclerView.u uVar = aVar.f8921a;
        if (uVar != null) {
            a(aVar, uVar);
        }
        RecyclerView.u uVar2 = aVar.f8922b;
        if (uVar2 != null) {
            a(aVar, uVar2);
        }
    }

    private void j() {
        if (!g()) {
            a();
        }
    }

    /* access modifiers changed from: private */
    public void k() {
        List<b> remove = this.p.isEmpty() ? i : this.p.remove(0);
        List<a> remove2 = this.q.isEmpty() ? j : this.q.remove(0);
        List<RecyclerView.u> remove3 = this.o.isEmpty() ? h : this.o.remove(0);
        for (b a2 : remove) {
            a(a2);
        }
        for (a a3 : remove2) {
            a(a3);
        }
        if (!remove3.isEmpty()) {
            b bVar = new b(this, remove3);
            if (!remove.isEmpty() || !remove2.isEmpty()) {
                remove3.get(0).itemView.postDelayed(bVar, 50);
            } else {
                bVar.run();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void A(RecyclerView.u uVar) {
        this.t.add(uVar);
        m(uVar);
    }

    /* access modifiers changed from: package-private */
    public abstract void B(RecyclerView.u uVar);

    /* access modifiers changed from: package-private */
    public abstract void C(RecyclerView.u uVar);

    /* access modifiers changed from: package-private */
    public abstract void a(a aVar);

    /* access modifiers changed from: package-private */
    public abstract void a(b bVar);

    public boolean a(RecyclerView.u uVar, int i2, int i3, int i4, int i5) {
        C(uVar);
        int i6 = i5 - i3;
        if (i4 - i2 == 0 && i6 == 0) {
            j(uVar);
            return false;
        }
        b bVar = new b(uVar, i2, i3, i4, i5);
        b(bVar);
        this.m.add(bVar);
        return true;
    }

    public boolean a(@NonNull RecyclerView.u uVar, @Nullable RecyclerView.ItemAnimator.c cVar, @NonNull RecyclerView.ItemAnimator.c cVar2) {
        if (cVar == null || (cVar.f1118a == cVar2.f1118a && cVar.f1119b == cVar2.f1119b)) {
            return f(uVar);
        }
        return a(uVar, cVar.f1118a, cVar.f1119b, cVar2.f1118a, cVar2.f1119b);
    }

    public boolean a(RecyclerView.u uVar, RecyclerView.u uVar2, int i2, int i3, int i4, int i5) {
        if (uVar == uVar2) {
            return a(uVar2, i2, i3, i4, i5);
        }
        a aVar = new a(uVar, uVar2, i2, i3, i4, i5);
        b(aVar);
        a(aVar);
        return true;
    }

    public boolean a(@NonNull RecyclerView.u uVar, @NonNull List<Object> list) {
        return !list.isEmpty() || super.a(uVar, list);
    }

    public void b() {
        int size = this.m.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            b bVar = this.m.get(size);
            View view = bVar.f8925a.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            j(bVar.f8925a);
            this.m.remove(size);
        }
        for (int size2 = this.k.size() - 1; size2 >= 0; size2--) {
            l(this.k.get(size2));
            this.k.remove(size2);
        }
        int size3 = this.l.size();
        while (true) {
            size3--;
            if (size3 < 0) {
                break;
            }
            RecyclerView.u uVar = this.l.get(size3);
            uVar.itemView.setAlpha(1.0f);
            h(uVar);
            this.l.remove(size3);
        }
        for (int size4 = this.n.size() - 1; size4 >= 0; size4--) {
            c(this.n.get(size4));
        }
        this.n.clear();
        if (g()) {
            for (int size5 = this.p.size() - 1; size5 >= 0; size5--) {
                ArrayList arrayList = this.p.get(size5);
                for (int size6 = arrayList.size() - 1; size6 >= 0; size6--) {
                    b bVar2 = (b) arrayList.get(size6);
                    View view2 = bVar2.f8925a.itemView;
                    view2.setTranslationY(0.0f);
                    view2.setTranslationX(0.0f);
                    j(bVar2.f8925a);
                    arrayList.remove(size6);
                    if (arrayList.isEmpty()) {
                        this.p.remove(arrayList);
                    }
                }
            }
            for (int size7 = this.o.size() - 1; size7 >= 0; size7--) {
                ArrayList arrayList2 = this.o.get(size7);
                for (int size8 = arrayList2.size() - 1; size8 >= 0; size8--) {
                    RecyclerView.u uVar2 = (RecyclerView.u) arrayList2.get(size8);
                    uVar2.itemView.setAlpha(1.0f);
                    h(uVar2);
                    arrayList2.remove(size8);
                    if (arrayList2.isEmpty()) {
                        this.o.remove(arrayList2);
                    }
                }
            }
            for (int size9 = this.q.size() - 1; size9 >= 0; size9--) {
                ArrayList arrayList3 = this.q.get(size9);
                for (int size10 = arrayList3.size() - 1; size10 >= 0; size10--) {
                    c((a) arrayList3.get(size10));
                    if (arrayList3.isEmpty()) {
                        this.q.remove(arrayList3);
                    }
                }
            }
            a((List<RecyclerView.u>) this.t);
            a((List<RecyclerView.u>) this.s);
            a((List<RecyclerView.u>) this.r);
            a((List<RecyclerView.u>) this.u);
            a();
        }
    }

    /* access modifiers changed from: package-private */
    public abstract void b(a aVar);

    /* access modifiers changed from: package-private */
    public abstract void b(b bVar);

    public void d(@NonNull RecyclerView.u uVar) {
        View view = uVar.itemView;
        b(view);
        int size = this.m.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (this.m.get(size).f8925a == uVar) {
                view.setTranslationY(0.0f);
                view.setTranslationX(0.0f);
                j(uVar);
                this.m.remove(size);
            }
        }
        a((List<a>) this.n, uVar);
        if (this.k.remove(uVar)) {
            view.setAlpha(1.0f);
            l(uVar);
        }
        if (this.l.remove(uVar)) {
            view.setAlpha(1.0f);
            h(uVar);
        }
        for (int size2 = this.q.size() - 1; size2 >= 0; size2--) {
            ArrayList arrayList = this.q.get(size2);
            a((List<a>) arrayList, uVar);
            if (arrayList.isEmpty()) {
                this.q.remove(size2);
            }
        }
        for (int size3 = this.p.size() - 1; size3 >= 0; size3--) {
            ArrayList arrayList2 = this.p.get(size3);
            int size4 = arrayList2.size() - 1;
            while (true) {
                if (size4 < 0) {
                    break;
                } else if (((b) arrayList2.get(size4)).f8925a == uVar) {
                    view.setTranslationY(0.0f);
                    view.setTranslationX(0.0f);
                    j(uVar);
                    arrayList2.remove(size4);
                    if (arrayList2.isEmpty()) {
                        this.p.remove(size3);
                    }
                } else {
                    size4--;
                }
            }
        }
        for (int size5 = this.o.size() - 1; size5 >= 0; size5--) {
            ArrayList arrayList3 = this.o.get(size5);
            if (arrayList3.remove(uVar)) {
                view.setAlpha(1.0f);
                h(uVar);
                if (arrayList3.isEmpty()) {
                    this.o.remove(size5);
                }
            }
        }
        this.t.remove(uVar);
        this.r.remove(uVar);
        this.u.remove(uVar);
        this.s.remove(uVar);
        j();
    }

    /* access modifiers changed from: package-private */
    public void e(RecyclerView.u uVar, boolean z) {
        a(uVar, z);
        this.u.remove(uVar);
        j();
    }

    /* access modifiers changed from: package-private */
    public void f(RecyclerView.u uVar, boolean z) {
        this.u.add(uVar);
        b(uVar, z);
    }

    public boolean f(RecyclerView.u uVar) {
        B(uVar);
        this.l.add(uVar);
        return true;
    }

    public boolean g() {
        return !this.l.isEmpty() || !this.n.isEmpty() || !this.m.isEmpty() || !this.k.isEmpty() || !this.s.isEmpty() || !this.t.isEmpty() || !this.r.isEmpty() || !this.u.isEmpty() || !this.p.isEmpty() || !this.o.isEmpty() || !this.q.isEmpty();
    }

    public boolean g(RecyclerView.u uVar) {
        C(uVar);
        this.k.add(uVar);
        return true;
    }

    public void i() {
        boolean z = !this.k.isEmpty();
        boolean z2 = !this.m.isEmpty();
        boolean z3 = !this.n.isEmpty();
        boolean z4 = !this.l.isEmpty();
        if (z || z2 || z3 || z4) {
            this.p.add(new ArrayList(this.m));
            this.m.clear();
            this.q.add(new ArrayList(this.n));
            this.n.clear();
            this.o.add(new ArrayList(this.l));
            this.l.clear();
            a aVar = new a(this);
            if (z) {
                Iterator<RecyclerView.u> it = this.k.iterator();
                while (it.hasNext()) {
                    u(it.next());
                }
                this.k.get(0).itemView.postDelayed(aVar, 100);
                this.k.clear();
                return;
            }
            aVar.run();
        }
    }

    /* access modifiers changed from: package-private */
    public abstract void t(RecyclerView.u uVar);

    /* access modifiers changed from: package-private */
    public abstract void u(RecyclerView.u uVar);

    /* access modifiers changed from: package-private */
    public void v(RecyclerView.u uVar) {
        h(uVar);
        this.r.remove(uVar);
        j();
    }

    /* access modifiers changed from: package-private */
    public void w(RecyclerView.u uVar) {
        this.r.add(uVar);
        i(uVar);
    }

    /* access modifiers changed from: package-private */
    public void x(RecyclerView.u uVar) {
        j(uVar);
        this.s.remove(uVar);
        j();
    }

    /* access modifiers changed from: package-private */
    public void y(RecyclerView.u uVar) {
        this.s.add(uVar);
        k(uVar);
    }

    /* access modifiers changed from: package-private */
    public void z(RecyclerView.u uVar) {
        l(uVar);
        this.t.remove(uVar);
        j();
    }
}
