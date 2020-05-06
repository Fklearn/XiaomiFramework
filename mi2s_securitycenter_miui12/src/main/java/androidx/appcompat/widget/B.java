package androidx.appcompat.widget;

import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.widget.AppCompatSpinner;

class B implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatSpinner f457a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppCompatSpinner.c f458b;

    B(AppCompatSpinner.c cVar, AppCompatSpinner appCompatSpinner) {
        this.f458b = cVar;
        this.f457a = appCompatSpinner;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        AppCompatSpinner.this.setSelection(i);
        if (AppCompatSpinner.this.getOnItemClickListener() != null) {
            AppCompatSpinner.c cVar = this.f458b;
            AppCompatSpinner.this.performItemClick(view, i, cVar.K.getItemId(i));
        }
        this.f458b.dismiss();
    }
}
