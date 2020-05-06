package com.miui.googlebase.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.Nullable;
import b.b.a.d.b.m;
import com.miui.activityutil.o;
import com.miui.googlebase.b.b;
import com.miui.securitycenter.R;
import miui.app.Activity;
import miui.os.Build;
import miui.os.SystemProperties;

public class GmsCoreSettings extends Activity {

    public static class a extends m implements Preference.OnPreferenceChangeListener {

        /* renamed from: a  reason: collision with root package name */
        private CheckBoxPreference f5455a;

        private void a() {
            boolean z = false;
            if (!Build.IS_INTERNATIONAL_BUILD && o.f2310b.equals(SystemProperties.get("ro.miui.has_gmscore"))) {
                int a2 = b.a(getActivity());
                if (a2 == -2 || a2 == -1) {
                    this.f5455a.setChecked(false);
                } else {
                    if (a2 != 0) {
                        z = true;
                        if (a2 != 1) {
                            return;
                        }
                    }
                    this.f5455a.setChecked(z);
                    return;
                }
            }
            this.f5455a.setEnabled(false);
        }

        public void onCreate(@Nullable Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.googlebase_gmscore_settings);
            this.f5455a = (CheckBoxPreference) findPreference("key_gmscore_enable");
            this.f5455a.setOnPreferenceChangeListener(this);
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            if (((Boolean) obj).booleanValue()) {
                b.a((Context) getActivity(), 1);
            } else {
                b.a((Context) getActivity(), 2);
            }
            a();
            return true;
        }

        public void onResume() {
            super.onResume();
            a();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        GmsCoreSettings.super.onCreate(bundle);
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, new a()).commit();
        }
    }
}
