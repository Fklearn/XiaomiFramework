package androidx.appcompat.widget;

import android.view.View;
import android.widget.AdapterView;

/* renamed from: androidx.appcompat.widget.ka  reason: case insensitive filesystem */
class C0108ka implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SearchView f618a;

    C0108ka(SearchView searchView) {
        this.f618a = searchView;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        this.f618a.a(i, 0, (String) null);
    }
}
