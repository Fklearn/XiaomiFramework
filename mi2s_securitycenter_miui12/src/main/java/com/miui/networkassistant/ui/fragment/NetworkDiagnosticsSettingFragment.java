package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import b.b.c.c.b.f;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.securitycenter.R;

public class NetworkDiagnosticsSettingFragment extends f implements Preference.b {
    private static final String PREF_KEY_DIAGNOSTICS_SHOW_FLOAT_NOTIFICATION = "pref_key_diagnostics_show_float_notification";
    private static final int TITLE_FILED = 2131757817;
    private CommonConfig mCommonConfig;
    private boolean mDiagnosticsShowFloatNotification;
    private CheckBoxPreference mDiagnosticsShowFloatNotificationPreference;

    private void initDiagnosticsNoShowFloatNotificationCheckboxPref() {
        this.mDiagnosticsShowFloatNotification = this.mCommonConfig.isNetworkDiagnosticsFloatNotificationEnabled();
        this.mDiagnosticsShowFloatNotificationPreference.setChecked(this.mDiagnosticsShowFloatNotification);
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.network_diagnostic_setting_preference;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        this.mCommonConfig = CommonConfig.getInstance(this.mAppContext);
        this.mDiagnosticsShowFloatNotificationPreference = (CheckBoxPreference) findPreference(PREF_KEY_DIAGNOSTICS_SHOW_FLOAT_NOTIFICATION);
        this.mDiagnosticsShowFloatNotificationPreference.setOnPreferenceChangeListener(this);
        initDiagnosticsNoShowFloatNotificationCheckboxPref();
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference != this.mDiagnosticsShowFloatNotificationPreference || this.mDiagnosticsShowFloatNotification == booleanValue) {
            return true;
        }
        this.mDiagnosticsShowFloatNotification = booleanValue;
        this.mCommonConfig.setNetworkDiagnosticsFloatNotificationEnabled(booleanValue);
        return true;
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.settings;
    }
}
