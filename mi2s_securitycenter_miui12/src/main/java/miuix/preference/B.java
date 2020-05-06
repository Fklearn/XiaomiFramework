package miuix.preference;

import androidx.preference.Preference;

class B implements p {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RadioSetPreferenceCategory f8876a;

    B(RadioSetPreferenceCategory radioSetPreferenceCategory) {
        this.f8876a = radioSetPreferenceCategory;
    }

    public void a(Preference preference) {
        if (preference instanceof RadioButtonPreference) {
            this.f8876a.setChecked(((RadioButtonPreference) preference).isChecked());
        }
        if (this.f8876a.j != null) {
            this.f8876a.j.a(preference);
        }
    }

    public boolean a(Preference preference, Object obj) {
        if (this.f8876a.j != null) {
            return this.f8876a.j.a(preference, obj);
        }
        return true;
    }
}
