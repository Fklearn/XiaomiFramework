package miuix.preference;

import androidx.preference.A;
import d.a.a.a;
import d.a.b;
import miui.external.widget.Spinner;

class e implements Spinner.OnSpinnerDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ A f8898a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ DropDownPreference f8899b;

    e(DropDownPreference dropDownPreference, A a2) {
        this.f8899b = dropDownPreference;
        this.f8898a = a2;
    }

    public void onSpinnerDismiss() {
        b.a(this.f8898a.itemView).touch().c(new a[0]);
    }
}
