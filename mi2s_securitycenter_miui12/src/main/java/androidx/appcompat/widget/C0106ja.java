package androidx.appcompat.widget;

import android.view.KeyEvent;
import android.widget.TextView;

/* renamed from: androidx.appcompat.widget.ja  reason: case insensitive filesystem */
class C0106ja implements TextView.OnEditorActionListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SearchView f615a;

    C0106ja(SearchView searchView) {
        this.f615a = searchView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        this.f615a.f();
        return true;
    }
}
