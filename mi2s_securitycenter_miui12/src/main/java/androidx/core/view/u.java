package androidx.core.view;

import android.view.View;
import android.view.WindowInsets;

class u implements View.OnApplyWindowInsetsListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ q f834a;

    u(q qVar) {
        this.f834a = qVar;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.f834a.a(view, H.a(windowInsets)).f();
    }
}
