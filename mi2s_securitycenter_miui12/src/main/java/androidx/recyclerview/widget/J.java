package androidx.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ViewBoundsCheck;

class J implements ViewBoundsCheck.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.g f1092a;

    J(RecyclerView.g gVar) {
        this.f1092a = gVar;
    }

    public int a() {
        return this.f1092a.q();
    }

    public int a(View view) {
        return this.f1092a.j(view) - ((RecyclerView.h) view.getLayoutParams()).topMargin;
    }

    public View a(int i) {
        return this.f1092a.c(i);
    }

    public int b() {
        return this.f1092a.h() - this.f1092a.n();
    }

    public int b(View view) {
        return this.f1092a.e(view) + ((RecyclerView.h) view.getLayoutParams()).bottomMargin;
    }
}
