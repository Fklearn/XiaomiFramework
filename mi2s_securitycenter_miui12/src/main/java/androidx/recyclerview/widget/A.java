package androidx.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

class A extends B {
    A(RecyclerView.g gVar) {
        super(gVar, (z) null);
    }

    public int a() {
        return this.f1079a.h();
    }

    public int a(View view) {
        return this.f1079a.e(view) + ((RecyclerView.h) view.getLayoutParams()).bottomMargin;
    }

    public void a(int i) {
        this.f1079a.e(i);
    }

    public int b() {
        return this.f1079a.h() - this.f1079a.n();
    }

    public int b(View view) {
        RecyclerView.h hVar = (RecyclerView.h) view.getLayoutParams();
        return this.f1079a.g(view) + hVar.topMargin + hVar.bottomMargin;
    }

    public int c() {
        return this.f1079a.n();
    }

    public int c(View view) {
        RecyclerView.h hVar = (RecyclerView.h) view.getLayoutParams();
        return this.f1079a.h(view) + hVar.leftMargin + hVar.rightMargin;
    }

    public int d() {
        return this.f1079a.i();
    }

    public int d(View view) {
        return this.f1079a.j(view) - ((RecyclerView.h) view.getLayoutParams()).topMargin;
    }

    public int e() {
        return this.f1079a.s();
    }

    public int e(View view) {
        this.f1079a.a(view, true, this.f1081c);
        return this.f1081c.bottom;
    }

    public int f() {
        return this.f1079a.q();
    }

    public int f(View view) {
        this.f1079a.a(view, true, this.f1081c);
        return this.f1081c.top;
    }

    public int g() {
        return (this.f1079a.h() - this.f1079a.q()) - this.f1079a.n();
    }
}
