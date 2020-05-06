package com.miui.warningcenter.mijia;

import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.securitycenter.R;
import com.miui.warningcenter.analytics.AnalyticHelper;
import miuix.preference.s;

public class WarningCenterMijiaFragment extends s {
    private CheckBoxPreference mOpenMijia;

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.warning_center_mijia_main, str);
        this.mOpenMijia = (CheckBoxPreference) findPreference("preference_key_open_mijia_warning");
        this.mOpenMijia.setOnPreferenceChangeListener(new Preference.b() {
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                MijiaUtils.setMijiaWarningOpen(booleanValue);
                AnalyticHelper.trackMijiaModuleClick(booleanValue ? AnalyticHelper.MIJIA_TOGGLE_OPEN : AnalyticHelper.MIJIA_TOGGLE_CLOSE);
                return true;
            }
        });
        this.mOpenMijia.setChecked(MijiaUtils.isMijiaWarningOpen());
    }
}
