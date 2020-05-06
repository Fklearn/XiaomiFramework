package androidx.appcompat.app;

import androidx.appcompat.widget.ContentFrameLayout;

class r implements ContentFrameLayout.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatDelegateImpl f321a;

    r(AppCompatDelegateImpl appCompatDelegateImpl) {
        this.f321a = appCompatDelegateImpl;
    }

    public void a() {
    }

    public void onDetachedFromWindow() {
        this.f321a.l();
    }
}
