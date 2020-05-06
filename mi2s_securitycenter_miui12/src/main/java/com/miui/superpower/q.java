package com.miui.superpower;

import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;
import com.miui.superpower.b.g;
import com.miui.superpower.b.k;
import miuix.preference.s;

public class q extends s {

    /* renamed from: a  reason: collision with root package name */
    private CheckBoxPreference f8138a;

    /* renamed from: b  reason: collision with root package name */
    private CheckBoxPreference f8139b;

    /* renamed from: c  reason: collision with root package name */
    private Preference.b f8140c = new p(this);

    /* access modifiers changed from: private */
    public void a() {
        o.a(getActivity(), true, true);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.pc_superpower_settings, str);
        getPreferenceScreen().setEnabled(k.o(getActivity()));
        this.f8138a = (CheckBoxPreference) findPreference("preference_key_superpower_switch");
        this.f8138a.setChecked(o.m(getActivity()));
        this.f8138a.setOnPreferenceChangeListener(this.f8140c);
        this.f8139b = (CheckBoxPreference) findPreference("preference_key_superpower_autoleave");
        this.f8139b.setChecked(g.b());
        this.f8139b.setOnPreferenceChangeListener(this.f8140c);
    }

    public void onResume() {
        super.onResume();
        this.f8138a.setChecked(o.m(getActivity()));
    }
}
