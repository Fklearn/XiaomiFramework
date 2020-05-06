package com.miui.permcenter;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.text.TextUtils;
import b.b.c.c.c;
import com.miui.permcenter.install.d;
import com.miui.securitycenter.R;

public class DebugSettingsAcitivty extends c implements Preference.OnPreferenceChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private CheckBoxPreference f6029a;

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.permcenter.DebugSettingsAcitivty, android.content.Context, android.preference.Preference$OnPreferenceChangeListener, miui.preference.PreferenceActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        DebugSettingsAcitivty.super.onCreate(bundle);
        addPreferencesFromResource(R.xml.pm_debug_settings);
        this.f6029a = (CheckBoxPreference) findPreference(getString(R.string.preference_key_app_permission_install_debug));
        this.f6029a.setOnPreferenceChangeListener(this);
        this.f6029a.setChecked(d.a((Context) this).g());
        getWindow().getDecorView().setAccessibilityDelegate(new d(this));
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.permcenter.DebugSettingsAcitivty, android.content.Context, miui.preference.PreferenceActivity] */
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (!TextUtils.equals(preference.getKey(), getString(R.string.preference_key_app_permission_install_debug))) {
            return false;
        }
        d.a((Context) this).c(booleanValue);
        return true;
    }
}
