package androidx.appcompat.widget;

import android.view.View;

/* renamed from: androidx.appcompat.widget.ha  reason: case insensitive filesystem */
class C0102ha implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SearchView f606a;

    C0102ha(SearchView searchView) {
        this.f606a = searchView;
    }

    public void onClick(View view) {
        SearchView searchView = this.f606a;
        if (view == searchView.u) {
            searchView.e();
        } else if (view == searchView.w) {
            searchView.d();
        } else if (view == searchView.v) {
            searchView.f();
        } else if (view == searchView.x) {
            searchView.h();
        } else if (view == searchView.q) {
            searchView.b();
        }
    }
}
