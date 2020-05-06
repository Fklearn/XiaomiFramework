package com.miui.warningcenter;

import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import com.miui.earthquakewarning.ui.EarthquakeWarningMainActivity;
import com.miui.securitycenter.R;
import com.miui.warningcenter.analytics.AnalyticHelper;
import com.miui.warningcenter.mijia.WarningCenterMijiaActivity;
import com.miui.warningcenter.widget.WarningcenterImagePreference;
import miuix.preference.s;

public class WarningCenterMainFragment extends s implements Preference.c {
    private static final String KEY_EW = "preference_key_ew";
    private static final String KEY_MIJIA = "preference_key_mijia";
    private static final String KEY_TITLE_EW = "preference_key_title_ew";
    private static final String KEY_TITLE_MIJIA = "preference_key_title_mijia";

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.warning_center_main, str);
        Preference findPreference = findPreference(KEY_TITLE_EW);
        Preference findPreference2 = findPreference(KEY_TITLE_MIJIA);
        findPreference.setOnPreferenceClickListener(this);
        findPreference2.setOnPreferenceClickListener(this);
        ((WarningcenterImagePreference) findPreference(KEY_EW)).setResource(R.drawable.warningcenter_ew);
        ((WarningcenterImagePreference) findPreference(KEY_MIJIA)).setResource(R.drawable.warningcenter_mijia);
    }

    public boolean onPreferenceClick(Preference preference) {
        String str;
        String key = preference.getKey();
        if (KEY_TITLE_EW.equals(key)) {
            startActivity(new Intent(getActivity(), EarthquakeWarningMainActivity.class));
            str = AnalyticHelper.MAIN_ITEM_EARTHQUAKE;
        } else if (!KEY_TITLE_MIJIA.equals(key)) {
            return true;
        } else {
            startActivity(new Intent(getActivity(), WarningCenterMijiaActivity.class));
            str = AnalyticHelper.MAIN_ITEM_MIJIA;
        }
        AnalyticHelper.trackMainModuleClick(str);
        return true;
    }
}
