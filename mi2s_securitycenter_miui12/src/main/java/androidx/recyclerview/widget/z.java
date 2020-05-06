package androidx.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

class z extends B {
    z(RecyclerView.g gVar) {
        super(gVar, (z) null);
    }

    public int a() {
        return this.f1079a.r();
    }

    public int a(View view) {
        return this.f1079a.i(view) + ((RecyclerView.h) view.getLayoutParams()).rightMargin;
    }

    public void a(int i) {
        this.f1079a.d(i);
    }

    public int b() {
        return this.f1079a.r() - this.f1079a.p();
    }

    public int b(View view) {
        RecyclerView.h hVar = (RecyclerView.h) view.getLayoutParams();
        return this.f1079a.h(view) + hVar.leftMargin + hVar.rightMargin;
    }

    public int c() {
        return this.f1079a.p();
    }

    public int c(View view) {
        RecyclerView.h hVar = (RecyclerView.h) view.getLayoutParams();
        return this.f1079a.g(view) + hVar.topMargin + hVar.bottomMargin;
    }

    public int d() {
        return this.f1079a.s();
    }

    public int d(View view) {
        return this.f1079a.f(view) - ((RecyclerView.h) view.getLayoutParams()).leftMargin;
    }

    public int e() {
        return this.f1079a.i();
    }

    public int e(View view) {
        this.f1079a.a(view, true, this.f1081c);
        return this.f1081c.right;
    }

    public int f() {
        return this.f1079a.o();
    }

    public int f(View view) {
        this.f1079a.a(view, true, this.f1081c);
        return this.f1081c.left;
    }

    public int g() {
        return (this.f1079a.r() - this.f1079a.o()) - this.f1079a.p();
    }
}
