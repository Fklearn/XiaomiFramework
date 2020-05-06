package androidx.appcompat.widget;

import android.text.Editable;
import android.text.TextWatcher;

/* renamed from: androidx.appcompat.widget.ca  reason: case insensitive filesystem */
class C0092ca implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SearchView f598a;

    C0092ca(SearchView searchView) {
        this.f598a = searchView;
    }

    public void afterTextChanged(Editable editable) {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        this.f598a.b(charSequence);
    }
}
