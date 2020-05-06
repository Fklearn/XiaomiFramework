package androidx.appcompat.widget;

import android.view.View;
import android.widget.AdapterView;

class T implements AdapterView.OnItemSelectedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ U f545a;

    T(U u) {
        this.f545a = u;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        O o;
        if (i != -1 && (o = this.f545a.f) != null) {
            o.setListSelectionHidden(false);
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
