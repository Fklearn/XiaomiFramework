package androidx.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ViewBoundsCheck;

class I implements ViewBoundsCheck.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.g f1091a;

    I(RecyclerView.g gVar) {
        this.f1091a = gVar;
    }

    public int a() {
        return this.f1091a.o();
    }

    public int a(View view) {
        return this.f1091a.f(view) - ((RecyclerView.h) view.getLayoutParams()).leftMargin;
    }

    public View a(int i) {
        return this.f1091a.c(i);
    }

    public int b() {
        return this.f1091a.r() - this.f1091a.p();
    }

    public int b(View view) {
        return this.f1091a.i(view) + ((RecyclerView.h) view.getLayoutParams()).rightMargin;
    }
}
