package androidx.recyclerview.widget;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class O extends RecyclerView.ItemAnimator {
    boolean g = true;

    public final void a(RecyclerView.u uVar, boolean z) {
        c(uVar, z);
        c(uVar);
    }

    public abstract boolean a(RecyclerView.u uVar, int i, int i2, int i3, int i4);

    public boolean a(@NonNull RecyclerView.u uVar, @Nullable RecyclerView.ItemAnimator.c cVar, @NonNull RecyclerView.ItemAnimator.c cVar2) {
        if (cVar == null || (cVar.f1118a == cVar2.f1118a && cVar.f1119b == cVar2.f1119b)) {
            return f(uVar);
        }
        return a(uVar, cVar.f1118a, cVar.f1119b, cVar2.f1118a, cVar2.f1119b);
    }

    public abstract boolean a(RecyclerView.u uVar, RecyclerView.u uVar2, int i, int i2, int i3, int i4);

    public boolean a(@NonNull RecyclerView.u uVar, @NonNull RecyclerView.u uVar2, @NonNull RecyclerView.ItemAnimator.c cVar, @NonNull RecyclerView.ItemAnimator.c cVar2) {
        int i;
        int i2;
        int i3 = cVar.f1118a;
        int i4 = cVar.f1119b;
        if (uVar2.shouldIgnore()) {
            int i5 = cVar.f1118a;
            i = cVar.f1119b;
            i2 = i5;
        } else {
            i2 = cVar2.f1118a;
            i = cVar2.f1119b;
        }
        return a(uVar, uVar2, i3, i4, i2, i);
    }

    public final void b(RecyclerView.u uVar, boolean z) {
        d(uVar, z);
    }

    public boolean b(@NonNull RecyclerView.u uVar) {
        return !this.g || uVar.isInvalid();
    }

    public boolean b(@NonNull RecyclerView.u uVar, @NonNull RecyclerView.ItemAnimator.c cVar, @Nullable RecyclerView.ItemAnimator.c cVar2) {
        int i = cVar.f1118a;
        int i2 = cVar.f1119b;
        View view = uVar.itemView;
        int left = cVar2 == null ? view.getLeft() : cVar2.f1118a;
        int top = cVar2 == null ? view.getTop() : cVar2.f1119b;
        if (uVar.isRemoved() || (i == left && i2 == top)) {
            return g(uVar);
        }
        view.layout(left, top, view.getWidth() + left, view.getHeight() + top);
        return a(uVar, i, i2, left, top);
    }

    public void c(RecyclerView.u uVar, boolean z) {
    }

    public boolean c(@NonNull RecyclerView.u uVar, @NonNull RecyclerView.ItemAnimator.c cVar, @NonNull RecyclerView.ItemAnimator.c cVar2) {
        if (cVar.f1118a == cVar2.f1118a && cVar.f1119b == cVar2.f1119b) {
            j(uVar);
            return false;
        }
        return a(uVar, cVar.f1118a, cVar.f1119b, cVar2.f1118a, cVar2.f1119b);
    }

    public void d(RecyclerView.u uVar, boolean z) {
    }

    public abstract boolean f(RecyclerView.u uVar);

    public abstract boolean g(RecyclerView.u uVar);

    public final void h(RecyclerView.u uVar) {
        n(uVar);
        c(uVar);
    }

    public final void i(RecyclerView.u uVar) {
        o(uVar);
    }

    public final void j(RecyclerView.u uVar) {
        p(uVar);
        c(uVar);
    }

    public final void k(RecyclerView.u uVar) {
        q(uVar);
    }

    public final void l(RecyclerView.u uVar) {
        r(uVar);
        c(uVar);
    }

    public final void m(RecyclerView.u uVar) {
        s(uVar);
    }

    public void n(RecyclerView.u uVar) {
    }

    public void o(RecyclerView.u uVar) {
    }

    public void p(RecyclerView.u uVar) {
    }

    public void q(RecyclerView.u uVar) {
    }

    public void r(RecyclerView.u uVar) {
    }

    public void s(RecyclerView.u uVar) {
    }
}
