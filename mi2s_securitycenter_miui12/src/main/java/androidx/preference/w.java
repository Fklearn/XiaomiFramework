package androidx.preference;

import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;

class w implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PreferenceGroup f1062a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ x f1063b;

    w(x xVar, PreferenceGroup preferenceGroup) {
        this.f1063b = xVar;
        this.f1062a = preferenceGroup;
    }

    public boolean onPreferenceClick(Preference preference) {
        this.f1062a.b(Integer.MAX_VALUE);
        this.f1063b.d(preference);
        PreferenceGroup.a b2 = this.f1062a.b();
        if (b2 == null) {
            return true;
        }
        b2.a();
        return true;
    }
}
