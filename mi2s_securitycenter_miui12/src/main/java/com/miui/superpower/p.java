package com.miui.superpower;

import androidx.preference.Preference;
import com.miui.powercenter.utils.o;
import com.miui.superpower.b.g;
import com.miui.superpower.b.h;

class p implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ q f8137a;

    p(q qVar) {
        this.f8137a = qVar;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        String key = preference.getKey();
        if (key.equals("preference_key_superpower_switch")) {
            if (!booleanValue) {
                h.a("setting");
                o.a(this.f8137a.getActivity(), false, true);
            } else {
                this.f8137a.a();
            }
        } else if (key.equals("preference_key_superpower_autoleave")) {
            g.a(booleanValue);
        }
        return true;
    }
}
