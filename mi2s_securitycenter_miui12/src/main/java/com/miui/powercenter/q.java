package com.miui.powercenter;

import androidx.preference.Preference;
import com.miui.powercenter.utils.g;

class q implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f7183a;

    q(x xVar) {
        this.f7183a = xVar;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if (key.equals("preference_key_battery_consume_abnormal")) {
            y.g(((Boolean) obj).booleanValue());
        } else if (key.equals("preference_key_settings_5g_save")) {
            if (((Boolean) obj).booleanValue()) {
                g.a(this.f7183a.getActivity(), 1);
            } else {
                this.f7183a.l();
            }
        } else if (key.equals("preference_key_wireless_reverse_charging")) {
            this.f7183a.a(((Boolean) obj).booleanValue());
        } else if (key.equals("preference_key_deep_save_disconnect_in_lockscreen")) {
            this.f7183a.f7367b.b((String) obj);
            x xVar = this.f7183a;
            int unused = xVar.z = xVar.f7367b.d();
            y.c(this.f7183a.A[this.f7183a.z] * 60);
        } else if (key.equals("preference_key_deep_save_memory_clean_in_lockscreen")) {
            this.f7183a.f7369d.b((String) obj);
            x xVar2 = this.f7183a;
            int unused2 = xVar2.v = xVar2.f7369d.d();
            y.d(this.f7183a.w[this.f7183a.v] * 60);
        }
        return true;
    }
}
