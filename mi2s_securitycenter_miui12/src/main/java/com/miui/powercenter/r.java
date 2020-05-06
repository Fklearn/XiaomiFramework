package com.miui.powercenter;

import androidx.preference.Preference;

class r implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f7274a;

    r(x xVar) {
        this.f7274a = xVar;
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals("preference_key_deep_save_memory_clean_in_lockscreen")) {
            this.f7274a.o();
            return true;
        } else if (key.equals("preference_key_deep_save_disconnect_in_lockscreen")) {
            this.f7274a.n();
            return true;
        } else if (key.equals("preference_key_boot_shutdown_ontime")) {
            this.f7274a.k();
            return true;
        } else if (key.equals("preference_key_battery_style")) {
            this.f7274a.j();
            return true;
        } else if (key.equals("preference_key_settings_power_save")) {
            this.f7274a.p();
            return true;
        } else if (key.equals("preference_key_settings_super_save")) {
            this.f7274a.q();
            return true;
        } else if (key.equals("preference_key_background_app_save")) {
            this.f7274a.i();
            return true;
        } else if (!key.equals("preference_key_config_scenario_policies")) {
            return true;
        } else {
            this.f7274a.m();
            return true;
        }
    }
}
