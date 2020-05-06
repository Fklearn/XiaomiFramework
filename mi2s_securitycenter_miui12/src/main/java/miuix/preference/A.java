package miuix.preference;

import android.widget.Checkable;
import androidx.preference.Preference;
import miuix.preference.RadioButtonPreferenceCategory;

class A implements p {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RadioButtonPreferenceCategory f8875a;

    A(RadioButtonPreferenceCategory radioButtonPreferenceCategory) {
        this.f8875a = radioButtonPreferenceCategory;
    }

    public void a(Preference preference) {
        RadioButtonPreferenceCategory.c a2 = this.f8875a.e(preference);
        this.f8875a.b(a2);
        this.f8875a.a(a2);
    }

    public boolean a(Preference preference, Object obj) {
        return !((Checkable) preference).isChecked();
    }
}
