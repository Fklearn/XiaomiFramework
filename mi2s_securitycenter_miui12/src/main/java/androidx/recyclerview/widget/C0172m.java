package androidx.recyclerview.widget;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: androidx.recyclerview.widget.m  reason: case insensitive filesystem */
public class C0172m extends O {
    private static TimeInterpolator h;
    private ArrayList<RecyclerView.u> i = new ArrayList<>();
    private ArrayList<RecyclerView.u> j = new ArrayList<>();
    private ArrayList<b> k = new ArrayList<>();
    private ArrayList<a> l = new ArrayList<>();
    ArrayList<ArrayList<RecyclerView.u>> m = new ArrayList<>();
    ArrayList<ArrayList<b>> n = new ArrayList<>();
    ArrayList<ArrayList<a>> o = new ArrayList<>();
    ArrayList<RecyclerView.u> p = new ArrayList<>();
    ArrayList<RecyclerView.u> q = new ArrayList<>();
    ArrayList<RecyclerView.u> r = new ArrayList<>();
    ArrayList<RecyclerView.u> s = new ArrayList<>();

    /* renamed from: androidx.recyclerview.widget.m$a */
    private static class a {

        /* renamed from: a  reason: collision with root package name */
        public RecyclerView.u f1221a;

        /* renamed from: b  reason: collision with root package name */
        public RecyclerView.u f1222b;

        /* renamed from: c  reason: collision with root package name */
        public int f1223c;

        /* renamed from: d  reason: collision with root package name */
        public int f1224d;
        public int e;
        public int f;

        private a(RecyclerView.u uVar, RecyclerView.u uVar2) {
            this.f1221a = uVar;
            this.f1222b = uVar2;
        }

        a(RecyclerView.u uVar, RecyclerView.u uVar2, int i, int i2, int i3, int i4) {
            this(uVar, uVar2);
            this.f1223c = i;
            this.f1224d = i2;
            this.e = i3;
            this.f = i4;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.f1221a + ", newHolder=" + this.f1222b + ", fromX=" + this.f1223c + ", fromY=" + this.f1224d + ", toX=" + this.e + ", toY=" + this.f + '}';
        }
    }

    /* renamed from: androidx.recyclerview.widget.m$b */
    private static class b {

        /* renamed from: a  reason: collision with root package name */
        public RecyclerView.u f1225a;

        /* renamed from: b  reason: collision with root package name */
        public int f1226b;

        /* renamed from: c  reason: collision with root package name */
        public int f1227c;

        /* renamed from: d  reason: collision with root package name */
        public int f1228d;
        public int e;

        b(RecyclerView.u uVar, int i, int i2, int i3, int i4) {
            this.f1225a = uVar;
            this.f1226b = i;
            this.f1227c = i2;
            this.f1228d = i3;
            this.e = i4;
        }
    }

    private void a(List<a> list, RecyclerView.u uVar) {
        for (int size = list.size() - 1; size >= 0; size--) {
            a aVar = list.get(size);
            if (a(aVar, uVar) && aVar.f1221a == null && aVar.f1222b == null) {
                list.remove(aVar);
            }
        }
    }

    private boolean a(a aVar, RecyclerView.u uVar) {
        boolean z = false;
        if (aVar.f1222b == uVar) {
            aVar.f1222b = null;
        } else if (aVar.f1221a != uVar) {
            return false;
        } else {
            aVar.f1221a = null;
            z = true;
        }
        uVar.itemView.setAlpha(1.0f);
        uVar.itemView.setTranslationX(0.0f);
        uVar.itemView.setTranslationY(0.0f);
        a(uVar, z);
        return true;
    }

    private void b(a aVar) {
        RecyclerView.u uVar = aVar.f1221a;
        if (uVar != null) {
            a(aVar, uVar);
        }
        RecyclerView.u uVar2 = aVar.f1222b;
        if (uVar2 != null) {
            a(aVar, uVar2);
        }
    }

    private void u(RecyclerView.u uVar) {
        View view = uVar.itemView;
        ViewPropertyAnimator animate = view.animate();
        this.r.add(uVar);
        animate.setDuration(f()).alpha(0.0f).setListener(new C0167h(this, uVar, animate, view)).start();
    }

    private void v(RecyclerView.u uVar) {
        if (h == null) {
            h = new ValueAnimator().getInterpolator();
        }
        uVar.itemView.animate().setInterpolator(h);
        d(uVar);
    }

    /* access modifiers changed from: package-private */
    public void a(a aVar) {
        RecyclerView.u uVar = aVar.f1221a;
        View view = null;
        View view2 = uVar == null ? null : uVar.itemView;
        RecyclerView.u uVar2 = aVar.f1222b;
        if (uVar2 != null) {
            view = uVar2.itemView;
        }
        if (view2 != null) {
            ViewPropertyAnimator duration = view2.animate().setDuration(d());
            this.s.add(aVar.f1221a);
            duration.translationX((float) (aVar.e - aVar.f1223c));
            duration.translationY((float) (aVar.f - aVar.f1224d));
            duration.alpha(0.0f).setListener(new C0170k(this, aVar, duration, view2)).start();
        }
        if (view != null) {
            ViewPropertyAnimator animate = view.animate();
            this.s.add(aVar.f1222b);
            animate.translationX(0.0f).translationY(0.0f).setDuration(d()).alpha(1.0f).setListener(new C0171l(this, aVar, animate, view)).start();
        }
    }

    /* access modifiers changed from: package-private */
    public void a(List<RecyclerView.u> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            list.get(size).itemView.animate().cancel();
        }
    }

    public boolean a(RecyclerView.u uVar, int i2, int i3, int i4, int i5) {
        View view = uVar.itemView;
        int translationX = i2 + ((int) view.getTranslationX());
        int translationY = i3 + ((int) uVar.itemView.getTranslationY());
        v(uVar);
        int i6 = i4 - translationX;
        int i7 = i5 - translationY;
        if (i6 == 0 && i7 == 0) {
            j(uVar);
            return false;
        }
        if (i6 != 0) {
            view.setTranslationX((float) (-i6));
        }
        if (i7 != 0) {
            view.setTranslationY((float) (-i7));
        }
        this.k.add(new b(uVar, translationX, translationY, i4, i5));
        return true;
    }

    public boolean a(RecyclerView.u uVar, RecyclerView.u uVar2, int i2, int i3, int i4, int i5) {
        if (uVar == uVar2) {
            return a(uVar, i2, i3, i4, i5);
        }
        float translationX = uVar.itemView.getTranslationX();
        float translationY = uVar.itemView.getTranslationY();
        float alpha = uVar.itemView.getAlpha();
        v(uVar);
        int i6 = (int) (((float) (i4 - i2)) - translationX);
        int i7 = (int) (((float) (i5 - i3)) - translationY);
        uVar.itemView.setTranslationX(translationX);
        uVar.itemView.setTranslationY(translationY);
        uVar.itemView.setAlpha(alpha);
        if (uVar2 != null) {
            v(uVar2);
            uVar2.itemView.setTranslationX((float) (-i6));
            uVar2.itemView.setTranslationY((float) (-i7));
            uVar2.itemView.setAlpha(0.0f);
        }
        this.l.add(new a(uVar, uVar2, i2, i3, i4, i5));
        return true;
    }

    public boolean a(@NonNull RecyclerView.u uVar, @NonNull List<Object> list) {
        return !list.isEmpty() || super.a(uVar, list);
    }

    public void b() {
        int size = this.k.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            b bVar = this.k.get(size);
            View view = bVar.f1225a.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            j(bVar.f1225a);
            this.k.remove(size);
        }
        for (int size2 = this.i.size() - 1; size2 >= 0; size2--) {
            l(this.i.get(size2));
            this.i.remove(size2);
        }
        int size3 = this.j.size();
        while (true) {
            size3--;
            if (size3 < 0) {
                break;
            }
            RecyclerView.u uVar = this.j.get(size3);
            uVar.itemView.setAlpha(1.0f);
            h(uVar);
            this.j.remove(size3);
        }
        for (int size4 = this.l.size() - 1; size4 >= 0; size4--) {
            b(this.l.get(size4));
        }
        this.l.clear();
        if (g()) {
            for (int size5 = this.n.size() - 1; size5 >= 0; size5--) {
                ArrayList arrayList = this.n.get(size5);
                for (int size6 = arrayList.size() - 1; size6 >= 0; size6--) {
                    b bVar2 = (b) arrayList.get(size6);
                    View view2 = bVar2.f1225a.itemView;
                    view2.setTranslationY(0.0f);
                    view2.setTranslationX(0.0f);
                    j(bVar2.f1225a);
                    arrayList.remove(size6);
                    if (arrayList.isEmpty()) {
                        this.n.remove(arrayList);
                    }
                }
            }
            for (int size7 = this.m.size() - 1; size7 >= 0; size7--) {
                ArrayList arrayList2 = this.m.get(size7);
                for (int size8 = arrayList2.size() - 1; size8 >= 0; size8--) {
                    RecyclerView.u uVar2 = (RecyclerView.u) arrayList2.get(size8);
                    uVar2.itemView.setAlpha(1.0f);
                    h(uVar2);
                    arrayList2.remove(size8);
                    if (arrayList2.isEmpty()) {
                        this.m.remove(arrayList2);
                    }
                }
            }
            for (int size9 = this.o.size() - 1; size9 >= 0; size9--) {
                ArrayList arrayList3 = this.o.get(size9);
                for (int size10 = arrayList3.size() - 1; size10 >= 0; size10--) {
                    b((a) arrayList3.get(size10));
                    if (arrayList3.isEmpty()) {
                        this.o.remove(arrayList3);
                    }
                }
            }
            a((List<RecyclerView.u>) this.r);
            a((List<RecyclerView.u>) this.q);
            a((List<RecyclerView.u>) this.p);
            a((List<RecyclerView.u>) this.s);
            a();
        }
    }

    /* access modifiers changed from: package-private */
    public void b(RecyclerView.u uVar, int i2, int i3, int i4, int i5) {
        View view = uVar.itemView;
        int i6 = i4 - i2;
        int i7 = i5 - i3;
        if (i6 != 0) {
            view.animate().translationX(0.0f);
        }
        if (i7 != 0) {
            view.animate().translationY(0.0f);
        }
        ViewPropertyAnimator animate = view.animate();
        this.q.add(uVar);
        animate.setDuration(e()).setListener(new C0169j(this, uVar, i6, view, i7, animate)).start();
    }

    public void d(RecyclerView.u uVar) {
        View view = uVar.itemView;
        view.animate().cancel();
        int size = this.k.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (this.k.get(size).f1225a == uVar) {
                view.setTranslationY(0.0f);
                view.setTranslationX(0.0f);
                j(uVar);
                this.k.remove(size);
            }
        }
        a((List<a>) this.l, uVar);
        if (this.i.remove(uVar)) {
            view.setAlpha(1.0f);
            l(uVar);
        }
        if (this.j.remove(uVar)) {
            view.setAlpha(1.0f);
            h(uVar);
        }
        for (int size2 = this.o.size() - 1; size2 >= 0; size2--) {
            ArrayList arrayList = this.o.get(size2);
            a((List<a>) arrayList, uVar);
            if (arrayList.isEmpty()) {
                this.o.remove(size2);
            }
        }
        for (int size3 = this.n.size() - 1; size3 >= 0; size3--) {
            ArrayList arrayList2 = this.n.get(size3);
            int size4 = arrayList2.size() - 1;
            while (true) {
                if (size4 < 0) {
                    break;
                } else if (((b) arrayList2.get(size4)).f1225a == uVar) {
                    view.setTranslationY(0.0f);
                    view.setTranslationX(0.0f);
                    j(uVar);
                    arrayList2.remove(size4);
                    if (arrayList2.isEmpty()) {
                        this.n.remove(size3);
                    }
                } else {
                    size4--;
                }
            }
        }
        for (int size5 = this.m.size() - 1; size5 >= 0; size5--) {
            ArrayList arrayList3 = this.m.get(size5);
            if (arrayList3.remove(uVar)) {
                view.setAlpha(1.0f);
                h(uVar);
                if (arrayList3.isEmpty()) {
                    this.m.remove(size5);
                }
            }
        }
        this.r.remove(uVar);
        this.p.remove(uVar);
        this.s.remove(uVar);
        this.q.remove(uVar);
        j();
    }

    public boolean f(RecyclerView.u uVar) {
        v(uVar);
        uVar.itemView.setAlpha(0.0f);
        this.j.add(uVar);
        return true;
    }

    public boolean g() {
        return !this.j.isEmpty() || !this.l.isEmpty() || !this.k.isEmpty() || !this.i.isEmpty() || !this.q.isEmpty() || !this.r.isEmpty() || !this.p.isEmpty() || !this.s.isEmpty() || !this.n.isEmpty() || !this.m.isEmpty() || !this.o.isEmpty();
    }

    public boolean g(RecyclerView.u uVar) {
        v(uVar);
        this.i.add(uVar);
        return true;
    }

    public void i() {
        boolean z = !this.i.isEmpty();
        boolean z2 = !this.k.isEmpty();
        boolean z3 = !this.l.isEmpty();
        boolean z4 = !this.j.isEmpty();
        if (z || z2 || z4 || z3) {
            Iterator<RecyclerView.u> it = this.i.iterator();
            while (it.hasNext()) {
                u(it.next());
            }
            this.i.clear();
            if (z2) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.k);
                this.n.add(arrayList);
                this.k.clear();
                C0164e eVar = new C0164e(this, arrayList);
                if (z) {
                    ViewCompat.a(((b) arrayList.get(0)).f1225a.itemView, (Runnable) eVar, f());
                } else {
                    eVar.run();
                }
            }
            if (z3) {
                ArrayList arrayList2 = new ArrayList();
                arrayList2.addAll(this.l);
                this.o.add(arrayList2);
                this.l.clear();
                C0165f fVar = new C0165f(this, arrayList2);
                if (z) {
                    ViewCompat.a(((a) arrayList2.get(0)).f1221a.itemView, (Runnable) fVar, f());
                } else {
                    fVar.run();
                }
            }
            if (z4) {
                ArrayList arrayList3 = new ArrayList();
                arrayList3.addAll(this.j);
                this.m.add(arrayList3);
                this.j.clear();
                C0166g gVar = new C0166g(this, arrayList3);
                if (z || z2 || z3) {
                    long j2 = 0;
                    long f = z ? f() : 0;
                    long e = z2 ? e() : 0;
                    if (z3) {
                        j2 = d();
                    }
                    ViewCompat.a(((RecyclerView.u) arrayList3.get(0)).itemView, (Runnable) gVar, f + Math.max(e, j2));
                    return;
                }
                gVar.run();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void j() {
        if (!g()) {
            a();
        }
    }

    /* access modifiers changed from: package-private */
    public void t(RecyclerView.u uVar) {
        View view = uVar.itemView;
        ViewPropertyAnimator animate = view.animate();
        this.p.add(uVar);
        animate.alpha(1.0f).setDuration(c()).setListener(new C0168i(this, uVar, view, animate)).start();
    }
}
