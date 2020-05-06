package com.miui.earthquakewarning.ui;

import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.securitycenter.R;
import miuix.preference.s;

public class EarthquakeWarningSettingFragment extends s {
    private CheckBoxPreference mSlideNormal;
    /* access modifiers changed from: private */
    public CheckBoxPreference mSlidePush;

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.ew_setting, str);
        this.mSlideNormal = (CheckBoxPreference) findPreference("slide_normal");
        this.mSlidePush = (CheckBoxPreference) findPreference("slide_push");
        this.mSlideNormal.setChecked(true);
        if (Utils.isLowEarthquakeWarningOpen()) {
            this.mSlidePush.setChecked(true);
        }
        this.mSlidePush.setOnPreferenceChangeListener(new Preference.b() {
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                Utils.setLowEarthquakeWarningOpen(booleanValue);
                EarthquakeWarningSettingFragment.this.mSlidePush.setChecked(booleanValue);
                return true;
            }
        });
    }
}
