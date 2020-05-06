package com.miui.appmanager.b;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.appmanager.AppManageUtils;
import com.miui.appmanager.B;
import com.miui.appmanager.i;
import com.miui.securitycenter.R;
import miuix.preference.s;

public class b extends s implements Preference.b {

    /* renamed from: a  reason: collision with root package name */
    private CheckBoxPreference f3590a;

    /* renamed from: b  reason: collision with root package name */
    private CheckBoxPreference f3591b;

    /* renamed from: c  reason: collision with root package name */
    private CheckBoxPreference f3592c;

    /* renamed from: d  reason: collision with root package name */
    private i f3593d;

    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R.xml.app_manager_settings);
        this.f3593d = new i(getActivity());
        this.f3590a = (CheckBoxPreference) findPreference("am_update_remind");
        this.f3590a.setChecked(this.f3593d.e());
        this.f3590a.setOnPreferenceChangeListener(this);
        this.f3591b = (CheckBoxPreference) findPreference("key_open_ads");
        this.f3591b.setChecked(this.f3593d.b());
        this.f3591b.setOnPreferenceChangeListener(this);
        this.f3592c = (CheckBoxPreference) findPreference("key_anomaly_analysis");
        if (AppManageUtils.e((Context) getActivity())) {
            this.f3592c.setChecked(this.f3593d.c());
            this.f3592c.setOnPreferenceChangeListener(this);
            return;
        }
        getPreferenceScreen().d(this.f3592c);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if ("am_update_remind".equals(key)) {
            this.f3593d.d(booleanValue);
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(B.f3568a, (ContentObserver) null);
            }
            return true;
        } else if ("key_open_ads".equals(key)) {
            this.f3593d.a(booleanValue);
            return true;
        } else if (!"key_anomaly_analysis".equals(key)) {
            return false;
        } else {
            this.f3593d.b(booleanValue);
            return true;
        }
    }
}
