package androidx.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.V;

class F implements V.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView f1084a;

    F(RecyclerView recyclerView) {
        this.f1084a = recyclerView;
    }

    public void a(RecyclerView.u uVar) {
        RecyclerView recyclerView = this.f1084a;
        recyclerView.v.a(uVar.itemView, recyclerView.k);
    }

    public void a(RecyclerView.u uVar, RecyclerView.ItemAnimator.c cVar, RecyclerView.ItemAnimator.c cVar2) {
        this.f1084a.a(uVar, cVar, cVar2);
    }

    public void b(RecyclerView.u uVar, @NonNull RecyclerView.ItemAnimator.c cVar, @Nullable RecyclerView.ItemAnimator.c cVar2) {
        this.f1084a.k.c(uVar);
        this.f1084a.b(uVar, cVar, cVar2);
    }

    public void c(RecyclerView.u uVar, @NonNull RecyclerView.ItemAnimator.c cVar, @NonNull RecyclerView.ItemAnimator.c cVar2) {
        uVar.setIsRecyclable(false);
        RecyclerView recyclerView = this.f1084a;
        if (recyclerView.M) {
            if (!recyclerView.V.a(uVar, uVar, cVar, cVar2)) {
                return;
            }
        } else if (!recyclerView.V.c(uVar, cVar, cVar2)) {
            return;
        }
        this.f1084a.t();
    }
}
