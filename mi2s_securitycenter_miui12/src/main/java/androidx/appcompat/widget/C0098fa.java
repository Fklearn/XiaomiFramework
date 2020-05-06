package androidx.appcompat.widget;

import android.view.View;

/* renamed from: androidx.appcompat.widget.fa  reason: case insensitive filesystem */
class C0098fa implements View.OnFocusChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SearchView f604a;

    C0098fa(SearchView searchView) {
        this.f604a = searchView;
    }

    public void onFocusChange(View view, boolean z) {
        SearchView searchView = this.f604a;
        View.OnFocusChangeListener onFocusChangeListener = searchView.N;
        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(searchView, z);
        }
    }
}
