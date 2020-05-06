package androidx.recyclerview.widget;

import androidx.recyclerview.widget.C0160a;
import androidx.recyclerview.widget.RecyclerView;

class H implements C0160a.C0018a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView f1090a;

    H(RecyclerView recyclerView) {
        this.f1090a = recyclerView;
    }

    public RecyclerView.u a(int i) {
        RecyclerView.u a2 = this.f1090a.a(i, true);
        if (a2 != null && !this.f1090a.n.c(a2.itemView)) {
            return a2;
        }
        return null;
    }

    public void a(int i, int i2) {
        this.f1090a.g(i, i2);
        this.f1090a.ta = true;
    }

    public void a(int i, int i2, Object obj) {
        this.f1090a.a(i, i2, obj);
        this.f1090a.ua = true;
    }

    public void a(C0160a.b bVar) {
        c(bVar);
    }

    public void b(int i, int i2) {
        this.f1090a.a(i, i2, false);
        this.f1090a.ta = true;
    }

    public void b(C0160a.b bVar) {
        c(bVar);
    }

    public void c(int i, int i2) {
        this.f1090a.f(i, i2);
        this.f1090a.ta = true;
    }

    /* access modifiers changed from: package-private */
    public void c(C0160a.b bVar) {
        int i = bVar.f1181a;
        if (i == 1) {
            RecyclerView recyclerView = this.f1090a;
            recyclerView.v.a(recyclerView, bVar.f1182b, bVar.f1184d);
        } else if (i == 2) {
            RecyclerView recyclerView2 = this.f1090a;
            recyclerView2.v.b(recyclerView2, bVar.f1182b, bVar.f1184d);
        } else if (i == 4) {
            RecyclerView recyclerView3 = this.f1090a;
            recyclerView3.v.a(recyclerView3, bVar.f1182b, bVar.f1184d, bVar.f1183c);
        } else if (i == 8) {
            RecyclerView recyclerView4 = this.f1090a;
            recyclerView4.v.a(recyclerView4, bVar.f1182b, bVar.f1184d, 1);
        }
    }

    public void d(int i, int i2) {
        this.f1090a.a(i, i2, true);
        RecyclerView recyclerView = this.f1090a;
        recyclerView.ta = true;
        recyclerView.qa.f1149d += i2;
    }
}
