package miuix.preference;

import android.view.View;
import android.widget.AdapterView;

class c implements AdapterView.OnItemSelectedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DropDownPreference f8896a;

    c(DropDownPreference dropDownPreference) {
        this.f8896a = dropDownPreference;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (i >= 0) {
            this.f8896a.k.post(new C0579b(this, (String) this.f8896a.i[i]));
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
