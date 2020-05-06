package androidx.preference;

import android.util.SparseArray;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

public class A extends RecyclerView.u {

    /* renamed from: a  reason: collision with root package name */
    private final SparseArray<View> f1006a = new SparseArray<>(4);

    /* renamed from: b  reason: collision with root package name */
    private boolean f1007b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f1008c;

    A(View view) {
        super(view);
        this.f1006a.put(16908310, view.findViewById(16908310));
        this.f1006a.put(16908304, view.findViewById(16908304));
        this.f1006a.put(16908294, view.findViewById(16908294));
        SparseArray<View> sparseArray = this.f1006a;
        int i = E.icon_frame;
        sparseArray.put(i, view.findViewById(i));
        this.f1006a.put(16908350, view.findViewById(16908350));
    }

    public void a(boolean z) {
        this.f1007b = z;
    }

    public boolean a() {
        return this.f1007b;
    }

    public View b(@IdRes int i) {
        View view = this.f1006a.get(i);
        if (view != null) {
            return view;
        }
        View findViewById = this.itemView.findViewById(i);
        if (findViewById != null) {
            this.f1006a.put(i, findViewById);
        }
        return findViewById;
    }

    public void b(boolean z) {
        this.f1008c = z;
    }

    public boolean b() {
        return this.f1008c;
    }
}
