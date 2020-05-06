package androidx.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: androidx.recyclerview.widget.b  reason: case insensitive filesystem */
public final class C0161b implements x {
    @NonNull

    /* renamed from: a  reason: collision with root package name */
    private final RecyclerView.a f1185a;

    public C0161b(@NonNull RecyclerView.a aVar) {
        this.f1185a = aVar;
    }

    public void a(int i, int i2) {
        this.f1185a.notifyItemRangeInserted(i, i2);
    }

    public void a(int i, int i2, Object obj) {
        this.f1185a.notifyItemRangeChanged(i, i2, obj);
    }

    public void b(int i, int i2) {
        this.f1185a.notifyItemRangeRemoved(i, i2);
    }

    public void c(int i, int i2) {
        this.f1185a.notifyItemMoved(i, i2);
    }
}
