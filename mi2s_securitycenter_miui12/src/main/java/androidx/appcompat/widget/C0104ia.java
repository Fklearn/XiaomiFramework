package androidx.appcompat.widget;

import android.view.KeyEvent;
import android.view.View;

/* renamed from: androidx.appcompat.widget.ia  reason: case insensitive filesystem */
class C0104ia implements View.OnKeyListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SearchView f610a;

    C0104ia(SearchView searchView) {
        this.f610a = searchView;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        SearchView searchView = this.f610a;
        if (searchView.ga == null) {
            return false;
        }
        if (searchView.q.isPopupShowing() && this.f610a.q.getListSelection() != -1) {
            return this.f610a.a(view, i, keyEvent);
        }
        if (this.f610a.q.b() || !keyEvent.hasNoModifiers() || keyEvent.getAction() != 1 || i != 66) {
            return false;
        }
        view.cancelLongPress();
        SearchView searchView2 = this.f610a;
        searchView2.a(0, (String) null, searchView2.q.getText().toString());
        return true;
    }
}
