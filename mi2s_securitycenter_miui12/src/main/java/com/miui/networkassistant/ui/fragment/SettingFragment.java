package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.j.B;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.ui.base.TrafficRelatedPreFragment;
import com.miui.networkassistant.ui.dialog.MessageDialog;
import com.miui.networkassistant.ui.dialog.TrafficInputDialog;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.MiSimUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.VirtualSimUtil;
import com.miui.securitycenter.R;
import com.miui.support.provider.g;
import java.lang.ref.WeakReference;
import miuix.preference.TextPreference;

public class SettingFragment extends TrafficRelatedPreFragment implements Preference.b, Preference.c {
    private static final int MSG_TRAFFIC_MANAGE_SERVICE_CONNECTED = 1;
    private static final String TAG = "NASettingFragment";
    private static final int TITLE_FILED = 2131757817;
    private final String PREF_CATEGORY_KEY_TRAFFIC = "category_key_traffic";
    private final String PREF_CATEGORY_KEY_TRAFFIC2 = "category_key_traffic2";
    private final String PREF_KEY_DECLARATION = "pref_key_declaration";
    private final String PREF_KEY_LIMIT_TRAFFIC_PER_DAY = "pref_key_limit_traffic_per_day";
    private final String PREF_KEY_LIMIT_TRAFFIC_PER_DAY2 = "pref_key_limit_traffic_per_day2";
    private final String PREF_KEY_MI_SIM_SETTING = "pref_key_mi_sim_settings";
    private final String PREF_KEY_MI_SIM_SETTINGS2 = "pref_key_mi_sim_settings2";
    private final String PREF_KEY_PACKAGE_TRAFFIC = "pref_key_package_traffic";
    private final String PREF_KEY_PACKAGE_TRAFFIC2 = "pref_key_package_traffic2";
    private final String PREF_KEY_TRAFFIC_CORRECTION = "pref_key_traffic_correction";
    private final String PREF_KEY_TRAFFIC_CORRECTION2 = "pref_key_traffic_correction2";
    private final String PREF_KEY_UPLOAD_DATA_USAGE = "pref_key_upload_data_usage";
    private final String PREF_SHOW_NETWORK_SPEED = "pref_show_traffic_speed_state";
    private final String PREF_SHOW_TRAFFIC_NOTIFICATION = "pref_show_traffic_notification";
    private final String PREF_TRAFFIC_MANAGE = "pref_traffic_manage";
    private final String PREF_TRAFFIC_MANAGE2 = "pref_traffic_manage2";
    private Preference mDeclarationPreference;
    private int mDisplayTrafficInBar;
    private UiHandler mHandler;
    private TrafficInputDialog mInputDialog;
    private Preference[] mLimitTrafficPreferences = new Preference[2];
    private Preference[] mMiSimSettingPreferences = new Preference[2];
    private final ContentObserver mNetworkSpeedObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            SettingFragment.this.initNetworkSpeedCheckboxPref();
        }
    };
    private TextPreference[] mPackageTrafficPreferences = new TextPreference[2];
    private final ContentObserver mPermanentNotificationEnableObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            SettingFragment.this.initTrafficNotificationCheckboxPref();
        }
    };
    private CheckBoxPreference mShowNetworkSpeed;
    private int mShowNetworkSpeedBar;
    private CheckBoxPreference mShowTrafficNotification;
    private TextPreference[] mTrafficCorrectionPreferences = new TextPreference[2];
    private MyTrafficInputDialogListener mTrafficInputDialogListener;
    private CheckBoxPreference[] mTrafficManagerPreferences = new CheckBoxPreference[2];
    private PreferenceCategory[] mTrafficPreferenceCategorys = new PreferenceCategory[2];
    private CheckBoxPreference mUploadDataUsagePreference;

    private static class MyTrafficInputDialogListener implements TrafficInputDialog.TrafficInputDialogListener {
        private WeakReference<SettingFragment> activityRef;

        MyTrafficInputDialogListener(SettingFragment settingFragment) {
            this.activityRef = new WeakReference<>(settingFragment);
        }

        public void onTrafficUpdated(long j, int i) {
            SettingFragment settingFragment = (SettingFragment) this.activityRef.get();
            if (settingFragment != null && settingFragment.mServiceConnected) {
                try {
                    settingFragment.mTrafficManageBinder.manualCorrectNormalDataUsage(j, settingFragment.mSlotNum);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class UiHandler extends Handler {
        private WeakReference<SettingFragment> activityRef;

        UiHandler(SettingFragment settingFragment) {
            this.activityRef = new WeakReference<>(settingFragment);
        }

        public void handleMessage(Message message) {
            SettingFragment settingFragment = (SettingFragment) this.activityRef.get();
            if (settingFragment != null && message.what == 1) {
                settingFragment.initData();
            }
        }
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mServiceConnected && isAttatched()) {
            if (this.mSimCardHelper.isDualSimInserted()) {
                setSimTitle();
                initSimRelated(0);
                initSimRelated(1);
                return;
            }
            initSimRelated(this.mSlotNum);
            removeNoSimCardCategory(this.mSlotNum);
        }
    }

    /* access modifiers changed from: private */
    public void initNetworkSpeedCheckboxPref() {
        boolean z = false;
        this.mShowNetworkSpeedBar = Settings.System.getInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, 0);
        CheckBoxPreference checkBoxPreference = this.mShowNetworkSpeed;
        if (1 == this.mShowNetworkSpeedBar) {
            z = true;
        }
        checkBoxPreference.setChecked(z);
    }

    private void initSimRelated(int i) {
        Preference preference;
        PreferenceCategory preferenceCategory;
        SimUserInfo[] simUserInfoArr = this.mSimUserInfos;
        if (simUserInfoArr[i] != null) {
            boolean hasImsi = simUserInfoArr[i].hasImsi();
            boolean z = hasImsi && this.mSimUserInfos[i].isTrafficManageControlEnable();
            boolean isMiSimEnable = MiSimUtil.isMiSimEnable(this.mAppContext, i);
            boolean isNotLimitCardEnable = this.mSimUserInfos[i].isNotLimitCardEnable();
            this.mTrafficManagerPreferences[i].setEnabled(hasImsi);
            updateTrafficPreference(i, z);
            if (isMiSimEnable) {
                this.mMiSimSettingPreferences[i].setTitle((CharSequence) getString(R.string.pref_mi_sim_settings, new Object[]{this.mSimUserInfos[i].getSimName()}));
                this.mTrafficPreferenceCategorys[i].d(this.mTrafficManagerPreferences[i]);
            } else {
                this.mTrafficPreferenceCategorys[i].b((Preference) this.mTrafficManagerPreferences[i]);
            }
            if (!isMiSimEnable || !hasImsi) {
                this.mTrafficPreferenceCategorys[i].d(this.mMiSimSettingPreferences[i]);
                preferenceCategory = this.mTrafficPreferenceCategorys[i];
                preference = this.mPackageTrafficPreferences[i];
            } else {
                this.mTrafficPreferenceCategorys[i].d(this.mPackageTrafficPreferences[i]);
                preferenceCategory = this.mTrafficPreferenceCategorys[i];
                preference = this.mMiSimSettingPreferences[i];
            }
            preferenceCategory.b(preference);
            if (!this.mSimUserInfos[i].isBrandSetted()) {
                this.mPackageTrafficPreferences[i].setTitle((int) R.string.traffic_setting_fragment_title_guide);
            }
            if (!isTotalDataUsageSetted(i) || !hasImsi || isMiSimEnable) {
                this.mPackageTrafficPreferences[i].a((int) R.string.pref_data_usage_not_set);
                this.mTrafficPreferenceCategorys[i].d(this.mTrafficCorrectionPreferences[i]);
            } else {
                showOperatorSettings(i);
                this.mPackageTrafficPreferences[i].a((String) null);
                this.mTrafficPreferenceCategorys[i].b((Preference) this.mTrafficCorrectionPreferences[i]);
            }
            if (!hasImsi || isMiSimEnable || !isTotalDataUsageSetted(i) || isNotLimitCardEnable) {
                this.mTrafficPreferenceCategorys[i].d(this.mLimitTrafficPreferences[i]);
            } else {
                this.mTrafficPreferenceCategorys[i].b(this.mLimitTrafficPreferences[i]);
            }
            if (!DeviceUtil.IS_INTERNATIONAL_BUILD) {
                return;
            }
            if (isTotalDataUsageSetted(i)) {
                this.mTrafficCorrectionPreferences[i].setTitle((int) R.string.input_used_hint);
            } else {
                this.mTrafficPreferenceCategorys[i].d(this.mTrafficCorrectionPreferences[i]);
            }
        }
    }

    /* access modifiers changed from: private */
    public void initTrafficNotificationCheckboxPref() {
        boolean z = false;
        this.mDisplayTrafficInBar = g.a(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, 0, 0);
        CheckBoxPreference checkBoxPreference = this.mShowTrafficNotification;
        if (1 == this.mDisplayTrafficInBar) {
            z = true;
        }
        checkBoxPreference.setChecked(z);
    }

    private boolean isTotalDataUsageSetted(int i) {
        return this.mSimUserInfos[i].isTotalDataUsageSetted();
    }

    private void overseaAdjustManually(int i) {
        if (this.mServiceConnected) {
            if (this.mTrafficInputDialogListener == null) {
                this.mTrafficInputDialogListener = new MyTrafficInputDialogListener(this);
            }
            TrafficInputDialog trafficInputDialog = this.mInputDialog;
            if (trafficInputDialog == null) {
                this.mSlotNum = i;
                this.mInputDialog = new TrafficInputDialog(this.mActivity, this.mTrafficInputDialogListener);
            } else {
                trafficInputDialog.clearInputText();
            }
            this.mInputDialog.buildInputDialog(getString(R.string.manual_input_traffic), getString(R.string.input_used_hint));
        }
    }

    private void registerNetworkSpeedObserver() {
        this.mActivity.getContentResolver().registerContentObserver(Settings.System.getUriFor(Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED), false, this.mNetworkSpeedObserver);
    }

    private void registerPermanentNotificationEnableObserver() {
        this.mActivity.getContentResolver().registerContentObserver(Settings.System.getUriFor(Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT), false, this.mPermanentNotificationEnableObserver);
    }

    private void removeNoSimCardCategory(int i) {
        getPreferenceScreen().d(this.mTrafficPreferenceCategorys[i == 0 ? (char) 1 : 0]);
    }

    private void setSimTitle() {
        setSimTitle(0, R.string.dual_setting_simcard1);
        setSimTitle(1, R.string.dual_setting_simcard2);
    }

    private void setSimTitle(int i, int i2) {
        SimUserInfo[] simUserInfoArr = this.mSimUserInfos;
        if (simUserInfoArr[i] != null && simUserInfoArr[i].hasImsi()) {
            this.mTrafficPreferenceCategorys[i].setTitle((CharSequence) String.format("%s(%s)", new Object[]{this.mSimUserInfos[i].getSimName(), getString(i2)}));
        }
    }

    private void showOperatorSettings(int i) {
        if (!TelephonyUtil.MIMOBILE.equals(this.mSimUserInfos[i].getOperator()) || !this.mSimUserInfos[i].isMiMobileOperatorModify()) {
        }
    }

    private void startPackageSettingFragment(int i) {
        if (this.mSimUserInfos[i].isBrandSetted()) {
            b.b.c.c.b.g.startWithFragment(this.mActivity, PackageSettingFragment.class);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(OperatorSettingFragment.BUNDLE_KEY_TRAFFIC_GUIDE, true);
        b.b.c.c.b.g.startWithFragment(this.mActivity, OperatorSettingFragment.class, bundle);
    }

    private void unRegisterNetworkSpeedObserver() {
        this.mActivity.getContentResolver().unregisterContentObserver(this.mNetworkSpeedObserver);
    }

    private void unRegisterPermanentNotificationEnableObserver() {
        this.mActivity.getContentResolver().unregisterContentObserver(this.mPermanentNotificationEnableObserver);
    }

    private void updateTrafficPreference(int i, boolean z) {
        this.mTrafficManagerPreferences[i].setChecked(z);
        this.mPackageTrafficPreferences[i].setEnabled(z);
        this.mTrafficCorrectionPreferences[i].setEnabled(z);
        this.mLimitTrafficPreferences[i].setEnabled(z);
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.setting_preferences;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        this.mTrafficPreferenceCategorys[0] = (PreferenceCategory) findPreference("category_key_traffic");
        this.mPackageTrafficPreferences[0] = (TextPreference) findPreference("pref_key_package_traffic");
        this.mTrafficCorrectionPreferences[0] = (TextPreference) findPreference("pref_key_traffic_correction");
        this.mLimitTrafficPreferences[0] = findPreference("pref_key_limit_traffic_per_day");
        this.mMiSimSettingPreferences[0] = findPreference("pref_key_mi_sim_settings");
        this.mTrafficManagerPreferences[0] = (CheckBoxPreference) findPreference("pref_traffic_manage");
        this.mPackageTrafficPreferences[0].setOnPreferenceClickListener(this);
        this.mTrafficCorrectionPreferences[0].setOnPreferenceClickListener(this);
        this.mLimitTrafficPreferences[0].setOnPreferenceClickListener(this);
        this.mMiSimSettingPreferences[0].setOnPreferenceClickListener(this);
        this.mTrafficManagerPreferences[0].setOnPreferenceChangeListener(this);
        this.mTrafficPreferenceCategorys[1] = (PreferenceCategory) findPreference("category_key_traffic2");
        this.mPackageTrafficPreferences[1] = (TextPreference) findPreference("pref_key_package_traffic2");
        this.mTrafficCorrectionPreferences[1] = (TextPreference) findPreference("pref_key_traffic_correction2");
        this.mLimitTrafficPreferences[1] = findPreference("pref_key_limit_traffic_per_day2");
        this.mMiSimSettingPreferences[1] = findPreference("pref_key_mi_sim_settings2");
        this.mTrafficManagerPreferences[1] = (CheckBoxPreference) findPreference("pref_traffic_manage2");
        if (DeviceUtil.IS_DUAL_CARD) {
            this.mPackageTrafficPreferences[1].setOnPreferenceClickListener(this);
            this.mTrafficCorrectionPreferences[1].setOnPreferenceClickListener(this);
            this.mLimitTrafficPreferences[1].setOnPreferenceClickListener(this);
            this.mMiSimSettingPreferences[1].setOnPreferenceClickListener(this);
            this.mTrafficManagerPreferences[1].setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().d(this.mTrafficPreferenceCategorys[1]);
        }
        this.mShowTrafficNotification = (CheckBoxPreference) findPreference("pref_show_traffic_notification");
        this.mShowNetworkSpeed = (CheckBoxPreference) findPreference("pref_show_traffic_speed_state");
        this.mShowTrafficNotification.setOnPreferenceChangeListener(this);
        this.mShowNetworkSpeed.setOnPreferenceChangeListener(this);
        this.mDeclarationPreference = findPreference("pref_key_declaration");
        this.mDeclarationPreference.setOnPreferenceClickListener(this);
        this.mUploadDataUsagePreference = (CheckBoxPreference) findPreference("pref_key_upload_data_usage");
        this.mUploadDataUsagePreference.setOnPreferenceChangeListener(this);
        this.mUploadDataUsagePreference.setChecked(com.miui.monthreport.g.b());
        initTrafficNotificationCheckboxPref();
        initNetworkSpeedCheckboxPref();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHandler = new UiHandler(this);
        registerNetworkSpeedObserver();
        registerPermanentNotificationEnableObserver();
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeCallbacksAndMessages((Object) null);
        unRegisterNetworkSpeedObserver();
        unRegisterPermanentNotificationEnableObserver();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!this.mServiceConnected) {
            return true;
        }
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mShowTrafficNotification) {
            if (this.mDisplayTrafficInBar != booleanValue) {
                this.mDisplayTrafficInBar = booleanValue;
                Settings.System.putInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, this.mDisplayTrafficInBar);
                if (B.j() != 0) {
                    g.b(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, this.mDisplayTrafficInBar, 0);
                }
            }
        } else if (preference != this.mShowNetworkSpeed) {
            CheckBoxPreference[] checkBoxPreferenceArr = this.mTrafficManagerPreferences;
            if (preference == checkBoxPreferenceArr[0]) {
                this.mSimUserInfos[0].setTrafficManageControlEnable(booleanValue);
                updateTrafficPreference(0, booleanValue);
            } else if (preference == checkBoxPreferenceArr[1]) {
                this.mSimUserInfos[1].setTrafficManageControlEnable(booleanValue);
                updateTrafficPreference(1, booleanValue);
            } else if (preference == this.mUploadDataUsagePreference) {
                com.miui.monthreport.g.a(booleanValue);
            }
        } else if (this.mShowNetworkSpeedBar != booleanValue) {
            this.mShowNetworkSpeedBar = booleanValue ? 1 : 0;
            Settings.System.putInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, this.mShowNetworkSpeedBar);
        }
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        Activity activity;
        Class cls;
        if (!this.mServiceConnected) {
            return true;
        }
        TextPreference[] textPreferenceArr = this.mPackageTrafficPreferences;
        if (preference == textPreferenceArr[0]) {
            Sim.operateOnSlot1();
            startPackageSettingFragment(0);
        } else {
            TextPreference[] textPreferenceArr2 = this.mTrafficCorrectionPreferences;
            if (preference == textPreferenceArr2[0]) {
                if (this.mSimUserInfos[0].isOversea() || !this.mSimUserInfos[0].isSmsAvailable()) {
                    overseaAdjustManually(0);
                } else {
                    Sim.operateOnSlot1();
                }
            } else if (preference == this.mDeclarationPreference) {
                new MessageDialog(this.mActivity).buildShowDialog(getString(R.string.pref_title_declaration), getString(R.string.declaration_dialog_msg));
            } else if (preference == textPreferenceArr[1]) {
                Sim.operateOnSlot2();
                startPackageSettingFragment(1);
            } else if (preference != textPreferenceArr2[1]) {
                Preference[] preferenceArr = this.mLimitTrafficPreferences;
                if (preference == preferenceArr[0]) {
                    Sim.operateOnSlot1();
                } else if (preference == preferenceArr[1]) {
                    Sim.operateOnSlot2();
                } else {
                    Preference[] preferenceArr2 = this.mMiSimSettingPreferences;
                    if (preference == preferenceArr2[0] || preference == preferenceArr2[1]) {
                        VirtualSimUtil.startVirtualSimActivity(this.mAppContext, VirtualSimUtil.ACTION_DETAIL_PAGE);
                    }
                }
                activity = this.mActivity;
                cls = TrafficLimitSettingFragment.class;
                b.b.c.c.b.g.startWithFragment(activity, cls);
            } else if (this.mSimUserInfos[1].isOversea() || !this.mSimUserInfos[1].isSmsAvailable()) {
                overseaAdjustManually(1);
            } else {
                Sim.operateOnSlot2();
            }
            activity = this.mActivity;
            cls = OperatorSettingFragment.class;
            b.b.c.c.b.g.startWithFragment(activity, cls);
        }
        return true;
    }

    public void onResume() {
        super.onResume();
        initData();
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        int intExtra;
        Intent intent = this.mActivity.getIntent();
        if (intent == null || (intExtra = intent.getIntExtra("extra_settings_title_res", -1)) == -1) {
            return R.string.settings;
        }
        setTitle(intExtra);
        return -1;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        this.mHandler.sendEmptyMessage(1);
    }

    /* access modifiers changed from: protected */
    public void resetTitle() {
    }
}
