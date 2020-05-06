package com.miui.powercenter;

import androidx.preference.Preference;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

class p implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f7141a;

    p(x xVar) {
        this.f7141a = xVar;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!"preference_key_thermal_configure".equals(preference.getKey())) {
            return false;
        }
        if ("performance".equals((String) obj)) {
            new AlertDialog.Builder(this.f7141a.getActivity()).setTitle(this.f7141a.getResources().getString(R.string.power_setting_important_warning)).setMessage(this.f7141a.getResources().getString(R.string.power_setting_warm_performance_tip)).setPositiveButton(this.f7141a.getResources().getString(17039370), new o(this)).setNegativeButton(17039369, new n(this)).show();
            return false;
        }
        this.f7141a.b("default");
        return false;
    }
}
