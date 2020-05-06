package androidx.fragment.app;

import android.view.View;
import androidx.annotation.Nullable;

/* renamed from: androidx.fragment.app.e  reason: case insensitive filesystem */
class C0135e extends C0138h {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Fragment f913a;

    C0135e(Fragment fragment) {
        this.f913a = fragment;
    }

    @Nullable
    public View a(int i) {
        View view = this.f913a.H;
        if (view != null) {
            return view.findViewById(i);
        }
        throw new IllegalStateException("Fragment " + this + " does not have a view");
    }

    public boolean c() {
        return this.f913a.H != null;
    }
}
