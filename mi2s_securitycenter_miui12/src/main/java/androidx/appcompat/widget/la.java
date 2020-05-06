package androidx.appcompat.widget;

import android.view.View;
import android.widget.AdapterView;

class la implements AdapterView.OnItemSelectedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SearchView f622a;

    la(SearchView searchView) {
        this.f622a = searchView;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.f622a.d(i);
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
