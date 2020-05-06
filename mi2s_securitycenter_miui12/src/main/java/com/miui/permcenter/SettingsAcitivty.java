package com.miui.permcenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.networkassistant.config.Constants;
import com.miui.permcenter.install.d;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;
import miuix.preference.s;

public class SettingsAcitivty extends b.b.c.c.a {

    public static class a extends s implements Preference.b {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f6034a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public CheckBoxPreference f6035b;

        private void a(boolean z) {
            if (z) {
                n.a(this.f6034a, true);
            } else {
                new AlertDialog.Builder(this.f6034a).setTitle(R.string.permission_close_permission_control_dialog_title).setMessage(R.string.permission_close_permission_control_dialog_msg).setPositiveButton(R.string.ok, new r(this)).setNegativeButton(R.string.cancel, new q(this)).setOnCancelListener(new p(this)).show();
            }
        }

        public void onCreatePreferences(Bundle bundle, String str) {
            setPreferencesFromResource(R.xml.pm_settings, str);
            this.f6034a = getContext();
            this.f6035b = (CheckBoxPreference) findPreference(getString(R.string.preference_key_app_permission_control));
            if (n.a()) {
                this.f6035b.setOnPreferenceChangeListener(this);
            } else {
                getPreferenceScreen().d(this.f6035b);
            }
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.preference_key_permcenter_install_intercept_enabled));
            d a2 = d.a(this.f6034a);
            if (d.h()) {
                checkBoxPreference.setChecked(a2.f());
                checkBoxPreference.setOnPreferenceChangeListener(this);
                return;
            }
            getPreferenceScreen().d(checkBoxPreference);
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (TextUtils.equals(preference.getKey(), getString(R.string.preference_key_app_permission_control))) {
                a(booleanValue);
                return true;
            } else if (!TextUtils.equals(preference.getKey(), getString(R.string.preference_key_permcenter_install_intercept_enabled))) {
                return false;
            } else {
                d.a(this.f6034a).b(booleanValue);
                return true;
            }
        }

        public void onResume() {
            super.onResume();
            this.f6035b.setChecked(n.b(this.f6034a));
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null) {
            String stringExtra = intent.getStringExtra(Constants.System.EXTRA_SETTINGS_TITLE);
            if (!TextUtils.isEmpty(stringExtra)) {
                setTitle(stringExtra);
            }
        }
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, new a()).commit();
        }
    }
}
