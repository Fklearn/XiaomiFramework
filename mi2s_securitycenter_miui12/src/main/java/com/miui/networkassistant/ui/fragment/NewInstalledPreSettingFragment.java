package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.content.DialogInterface;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import b.b.c.c.b.f;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.ui.dialog.CommonDialog;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.TextPrepareUtil;
import com.miui.securitycenter.R;

public class NewInstalledPreSettingFragment extends f {
    private static final String PREF_KEY_NEW_INSTALLED_APP_FIREWALL_MOBILE1 = "pref_key_new_installed_app_firewall_mobile1";
    private static final String PREF_KEY_NEW_INSTALLED_APP_FIREWALL_MOBILE2 = "pref_key_new_installed_app_firewall_mobile2";
    private static final String PREF_KEY_NEW_INSTALLED_APP_FIREWALL_WIFI = "pref_key_new_installed_app_firewall_wifi";
    private static final int TITLE_FILED = 2131757452;
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    private boolean mIsDualSimInserted;
    private DialogInterface.OnClickListener mMobileClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
            NewInstalledPreSettingFragment.this.handleMobileDialogClick(i, 0);
        }
    };
    private DialogInterface.OnClickListener mMobileClickListener2 = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
            NewInstalledPreSettingFragment.this.handleMobileDialogClick(i, 1);
        }
    };
    /* access modifiers changed from: private */
    public CheckBoxPreference[] mMobilePreConfig = new CheckBoxPreference[2];
    private Preference.b mOnPreferenceChangeListener = new Preference.b() {
        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (NewInstalledPreSettingFragment.this.mMobilePreConfig[0] == preference) {
                NewInstalledPreSettingFragment.this.showMobileCloseDialog(0, !booleanValue);
            } else if (NewInstalledPreSettingFragment.this.mMobilePreConfig[1] == preference) {
                NewInstalledPreSettingFragment.this.showMobileCloseDialog(1, !booleanValue);
            } else if (NewInstalledPreSettingFragment.this.mWifiPreConfig == preference) {
                if (booleanValue) {
                    NewInstalledPreSettingFragment.this.mCommonConfig.setFirewallWifiPreConfig(FirewallRule.Allow.value());
                } else {
                    NewInstalledPreSettingFragment.this.showWifiCloseDialog();
                }
            }
            return true;
        }
    };
    private SimUserInfo[] mSimUserInfo = new SimUserInfo[2];
    private DialogInterface.OnClickListener mWifiClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                NewInstalledPreSettingFragment.this.mCommonConfig.setFirewallWifiPreConfig(FirewallRule.Restrict.value());
            } else {
                NewInstalledPreSettingFragment.this.mWifiPreConfig.setChecked(true);
            }
        }
    };
    /* access modifiers changed from: private */
    public CheckBoxPreference mWifiPreConfig;

    /* access modifiers changed from: private */
    public void handleMobileDialogClick(int i, int i2) {
        if (i == -1) {
            this.mSimUserInfo[i2].setFirewallMobilePreConfig(FirewallRule.Restrict.value());
        } else {
            this.mMobilePreConfig[i2].setChecked(true);
        }
    }

    private void initMobilePreference(int i) {
        this.mSimUserInfo[i] = SimUserInfo.getInstance(this.mAppContext, i);
        this.mMobilePreConfig[i].setOnPreferenceChangeListener(this.mOnPreferenceChangeListener);
        this.mMobilePreConfig[i].setChecked(FirewallRule.Allow.value() == this.mSimUserInfo[i].getFirewallMobilePreConfig());
    }

    private void setDualCardMobileConfig() {
        char c2 = 1;
        if (!DeviceUtil.IS_DUAL_CARD || !this.mIsDualSimInserted) {
            int currentActiveSlotNum = Sim.getCurrentActiveSlotNum();
            initMobilePreference(currentActiveSlotNum);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (currentActiveSlotNum != 0) {
                c2 = 0;
            }
            preferenceScreen.d(this.mMobilePreConfig[c2]);
            return;
        }
        setMobilePreference(0);
        setMobilePreference(1);
    }

    private void setMobilePreference(int i) {
        this.mMobilePreConfig[i].setTitle((CharSequence) TextPrepareUtil.getDualCardTitle(this.mAppContext, this.mMobilePreConfig[i].getTitle(), i));
        initMobilePreference(i);
    }

    /* access modifiers changed from: private */
    public void showMobileCloseDialog(int i, boolean z) {
        if (z) {
            CommonDialog commonDialog = new CommonDialog(this.mActivity, i == 0 ? this.mMobileClickListener : this.mMobileClickListener2);
            commonDialog.setTitle(this.mAppContext.getString(R.string.dialog_title_attention));
            commonDialog.setMessage(this.mAppContext.getString(R.string.firewall_mobile_dialog_message));
            commonDialog.show();
            return;
        }
        this.mSimUserInfo[i].setFirewallMobilePreConfig(FirewallRule.Allow.value());
    }

    /* access modifiers changed from: private */
    public void showWifiCloseDialog() {
        CommonDialog commonDialog = new CommonDialog(this.mActivity, this.mWifiClickListener);
        commonDialog.setTitle(this.mAppContext.getString(R.string.dialog_title_attention));
        commonDialog.setMessage(this.mAppContext.getString(R.string.firewall_wifi_dialog_message));
        commonDialog.show();
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.new_installed_app_firewall_preferences;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        this.mCommonConfig = CommonConfig.getInstance(this.mAppContext);
        this.mIsDualSimInserted = SimCardHelper.getInstance(this.mAppContext).isDualSimInserted();
        boolean z = false;
        this.mMobilePreConfig[0] = (CheckBoxPreference) findPreference(PREF_KEY_NEW_INSTALLED_APP_FIREWALL_MOBILE1);
        this.mMobilePreConfig[1] = (CheckBoxPreference) findPreference(PREF_KEY_NEW_INSTALLED_APP_FIREWALL_MOBILE2);
        setDualCardMobileConfig();
        this.mWifiPreConfig = (CheckBoxPreference) findPreference(PREF_KEY_NEW_INSTALLED_APP_FIREWALL_WIFI);
        this.mWifiPreConfig.setOnPreferenceChangeListener(this.mOnPreferenceChangeListener);
        CheckBoxPreference checkBoxPreference = this.mWifiPreConfig;
        if (FirewallRule.Allow.value() == this.mCommonConfig.getFirewallWifiPreConfig()) {
            z = true;
        }
        checkBoxPreference.setChecked(z);
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.pref_new_installed_preconfig_title;
    }
}
