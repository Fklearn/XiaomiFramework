package androidx.preference;

import android.view.View;
import android.widget.AdapterView;

/* renamed from: androidx.preference.a  reason: case insensitive filesystem */
class C0147a implements AdapterView.OnItemSelectedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DropDownPreference f1033a;

    C0147a(DropDownPreference dropDownPreference) {
        this.f1033a = dropDownPreference;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (i >= 0) {
            String charSequence = this.f1033a.i()[i].toString();
            if (!charSequence.equals(this.f1033a.j()) && this.f1033a.callChangeListener(charSequence)) {
                this.f1033a.b(charSequence);
            }
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
