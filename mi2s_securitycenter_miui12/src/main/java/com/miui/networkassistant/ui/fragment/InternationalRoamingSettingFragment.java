package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import b.b.c.c.a.b;
import b.b.c.c.b.f;
import b.b.c.c.b.g;
import com.miui.luckymoney.config.AppConstants;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.wrapper.TmBinderCacher;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.ui.dialog.TrafficInputDialog;
import com.miui.networkassistant.utils.CollectionUtils;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.app.AlertDialog;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class InternationalRoamingSettingFragment extends f implements Preference.b, Preference.c {
    private static final String CATEGORY_DOMESTIC_ROAMING_SETTING = "category_domestic_roaming_setting";
    private static final String CATEGORY_ROAMING_LIMIT_SETTING = "category_roaming_limit_setting";
    private static final String CATEGORY_ROAMING_SETTING = "category_roaming_setting";
    private static final String PREF_ALLOW_CONNECT_NETWORK_SWITCH = "pref_allow_connect_network_switch";
    private static final String PREF_ALLOW_CONNECT_NETWORK_SWITCH_DEFAULT = "pref_allow_connect_network_switch_default";
    private static final String PREF_ALLOW_CONNECT_NETWORK_SWITCH_OLD = "pref_allow_connect_network_switch_old";
    private static final String PREF_KEY_DOMESTIC_ROAMING = "pref_key_domestic_roaming";
    private static final String PREF_KEY_OVER_LIMIT_OPT = "pref_key_over_limit_opt";
    private static final String PREF_KEY_OVER_LIMIT_OPT_OLD = "pref_key_over_limit_opt_old";
    private static final String PREF_KEY_ROAMING_DAILY_LIMIT = "pref_key_roaming_daily_limit";
    private static final String PREF_KEY_ROAMING_DAILY_LIMIT_VALUE = "pref_key_roaming_daily_limit_value";
    private static final String PREF_WHITE_LIST_SETTING = "pref_whitelist_setting";
    private static final int ROAMING_TYPE_ALWAYS = 0;
    private static final int ROAMING_TYPE_EXCEPTIONS = 1;
    private static final int ROAMING_TYPE_NEVER = 2;
    private static final int SINGLE_CHOICE_OPT_TYPE_FLAG = 1;
    private static final int SINGLE_CHOICE_ROAMING_TYPE_FLAG = 2;
    private static final int TITLE_FILED = 2131756620;
    private static List<String> sDefaultEnableList = new ArrayList();
    /* access modifiers changed from: private */
    public CheckBoxPreference mAllowNetworkDefaultPreference;
    private DropDownPreference mAllowNetworkPreference;
    private TextPreference mAllowNetworkPreferenceOld;
    /* access modifiers changed from: private */
    public int mAllowNetworkType;
    private String[] mAllowNetworkTypeArray;
    private int mChoiceIndex;
    private SingleChoiceItemsDialog.SingleChoiceItemsDialogListener mChoiceItemsDialogListener = new SingleChoiceItemsDialog.SingleChoiceItemsDialogListener() {
        public void onSelectItemUpdate(int i, int i2) {
            if (i2 == 1) {
                InternationalRoamingSettingFragment.this.onSelectOverLimitOpt(i);
            } else if (i2 == 2 && InternationalRoamingSettingFragment.this.mAllowNetworkType != i) {
                if (i == 0 || i == 1) {
                    InternationalRoamingSettingFragment.this.checkShowWarningDialog(i);
                } else {
                    InternationalRoamingSettingFragment.this.updateRoamingType(i);
                }
            }
            boolean unused = InternationalRoamingSettingFragment.this.mSettedChanged = true;
        }
    };
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    private PreferenceCategory mDomesticRoamingCategory;
    private CheckBoxPreference mDomesticRoamingSwitch;
    /* access modifiers changed from: private */
    public IFirewallBinder mFirewallBinder;
    private ServiceConnection mFirewallConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IFirewallBinder unused = InternationalRoamingSettingFragment.this.mFirewallBinder = IFirewallBinder.Stub.asInterface(iBinder);
            InternationalRoamingSettingFragment.this.initRoamingButtonState();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            IFirewallBinder unused = InternationalRoamingSettingFragment.this.mFirewallBinder = null;
        }
    };
    private TrafficInputDialog mInputDialog;
    private DropDownPreference mOverLimitOptType;
    private TextPreference mOverLimitOptTypeOld;
    private PreferenceScreen mPreferenceScreen;
    private CheckBoxPreference mRoamingDailyLimitCheckBox;
    /* access modifiers changed from: private */
    public TextPreference mRoamingDailyLimitTextPreference;
    private PreferenceCategory mRoamingLimitCategory;
    private PreferenceCategory mRoamingSettingCategory;
    protected boolean mServiceConnected;
    /* access modifiers changed from: private */
    public boolean mSettedChanged;
    /* access modifiers changed from: private */
    public SimUserInfo mSimUserInfo;
    private String[] mSingleChoiceItemsArray;
    private SingleChoiceItemsDialog mSingleChoiceItemsDialog;
    private int mSlotNum = 0;
    private TrafficInputDialog.TrafficInputDialogListener mTrafficInputDialogListener = new TrafficInputDialog.TrafficInputDialogListener() {
        public void onTrafficUpdated(long j, int i) {
            InternationalRoamingSettingFragment.this.mRoamingDailyLimitTextPreference.a(FormatBytesUtil.formatBytes(InternationalRoamingSettingFragment.this.mAppContext, j));
            InternationalRoamingSettingFragment.this.mSimUserInfo.setRoamingDailyLimitTraffic(j);
            boolean unused = InternationalRoamingSettingFragment.this.mSettedChanged = true;
        }
    };
    protected ITrafficManageBinder mTrafficManageBinder;
    private ServiceConnection mTrafficManageConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            InternationalRoamingSettingFragment internationalRoamingSettingFragment = InternationalRoamingSettingFragment.this;
            internationalRoamingSettingFragment.mServiceConnected = true;
            internationalRoamingSettingFragment.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
            InternationalRoamingSettingFragment internationalRoamingSettingFragment2 = InternationalRoamingSettingFragment.this;
            internationalRoamingSettingFragment2.postOnUiThread(new b(internationalRoamingSettingFragment2) {
                public void runOnUiThread() {
                    InternationalRoamingSettingFragment.this.initData();
                }
            });
        }

        public void onServiceDisconnected(ComponentName componentName) {
            InternationalRoamingSettingFragment internationalRoamingSettingFragment = InternationalRoamingSettingFragment.this;
            internationalRoamingSettingFragment.mTrafficManageBinder = null;
            internationalRoamingSettingFragment.mServiceConnected = false;
        }
    };
    private TextPreference mWhiteListSettingPreference;

    static {
        sDefaultEnableList.add(AppConstants.Package.PACKAGE_NAME_QQ);
        sDefaultEnableList.add(AppConstants.Package.PACKAGE_NAME_MM);
        sDefaultEnableList.add("com.miui.virtualsim");
        sDefaultEnableList.add("com.mipay.wallet");
        sDefaultEnableList.add("com.xiaomi.account");
    }

    private void bindTrafficManageService() {
        TmBinderCacher.getInstance().bindTmService(this.mTrafficManageConn);
    }

    /* access modifiers changed from: private */
    public void checkShowWarningDialog(final int i) {
        if (this.mCommonConfig.isNoMoreAskRoaming()) {
            updateRoamingType(i);
        } else {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.firewall_restrict_android_dialog_title).setMessage(R.string.dialog_roaming_warning_message).setPositiveButton(R.string.dialog_roaming_warning_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (((AlertDialog) dialogInterface).isChecked()) {
                        InternationalRoamingSettingFragment.this.mCommonConfig.setNoMoreAskRoaming(true);
                    }
                    InternationalRoamingSettingFragment.this.updateRoamingType(i);
                }
            }).setNegativeButton(17039360, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (DeviceUtil.IS_CUSTOMIZED_VERSION) {
                        InternationalRoamingSettingFragment.this.mAllowNetworkDefaultPreference.setChecked(false);
                        TelephonyUtil.setDomesticRoamingEnable(InternationalRoamingSettingFragment.this.mAppContext, false);
                    }
                }
            }).setCheckBox(false, getString(R.string.dialog_roaming_warning_checkbox)).setCancelable(false).create().show();
        }
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mTrafficManageBinder != null) {
            this.mSimUserInfo = SimUserInfo.getInstance(this.mAppContext, this.mSlotNum);
            boolean roamingDailyLimitEnabled = this.mSimUserInfo.getRoamingDailyLimitEnabled();
            this.mRoamingDailyLimitCheckBox.setChecked(roamingDailyLimitEnabled);
            this.mRoamingDailyLimitTextPreference.setEnabled(roamingDailyLimitEnabled);
            (DeviceUtil.IS_MIUI12 ? this.mOverLimitOptType : this.mOverLimitOptTypeOld).setEnabled(roamingDailyLimitEnabled);
            this.mRoamingDailyLimitTextPreference.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mSimUserInfo.getRoamingDailyLimitTraffic()));
            this.mChoiceIndex = this.mSimUserInfo.getRoamingOverLimitOptType();
            setOverLimitPreferenceText(this.mSingleChoiceItemsArray[this.mChoiceIndex]);
        }
    }

    /* access modifiers changed from: private */
    public void initRoamingButtonState() {
        boolean dataRoamingEnabled = TelephonyUtil.getDataRoamingEnabled(this.mAppContext);
        boolean dataRoamingWhiteListEnable = this.mCommonConfig.getDataRoamingWhiteListEnable();
        if (!DeviceUtil.IS_CUSTOMIZED_VERSION || !dataRoamingEnabled || !dataRoamingWhiteListEnable) {
            boolean z = true;
            if (!dataRoamingEnabled) {
                setAllowNetworkPreferenceText(this.mAllowNetworkTypeArray[2]);
                if (DeviceUtil.IS_CUSTOMIZED_VERSION) {
                    this.mAllowNetworkDefaultPreference.setChecked(false);
                }
                this.mAllowNetworkType = 2;
                showRoamingAppExceptionVisible(false);
            } else if (dataRoamingWhiteListEnable) {
                setAllowNetworkPreferenceText(this.mAllowNetworkTypeArray[1]);
                this.mAllowNetworkType = 1;
                updateDefaultEnabledList();
            } else {
                setAllowNetworkPreferenceText(this.mAllowNetworkTypeArray[0]);
                if (DeviceUtil.IS_CUSTOMIZED_VERSION) {
                    this.mAllowNetworkDefaultPreference.setChecked(true);
                }
                this.mAllowNetworkType = 0;
            }
            if (!dataRoamingEnabled || !dataRoamingWhiteListEnable) {
                z = false;
            }
            showRoamingAppExceptionVisible(z);
            showRoamingLimitVisible(dataRoamingEnabled);
            return;
        }
        updateRoamingType(0);
    }

    private void onSelectAllowNetwork(int i) {
        if (this.mAllowNetworkType != i && i >= 0) {
            if (i == 0 || i == 1) {
                checkShowWarningDialog(i);
            } else {
                updateRoamingType(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onSelectOverLimitOpt(int i) {
        if (i >= 0) {
            this.mChoiceIndex = i;
            setOverLimitPreferenceText(this.mSingleChoiceItemsArray[i]);
            this.mSimUserInfo.setRoamingOverLimitOptType(i);
        }
    }

    private void setAllowNetworkPreferenceText(String str) {
        if (DeviceUtil.IS_MIUI12) {
            this.mAllowNetworkPreference.b(str);
        } else {
            this.mAllowNetworkPreferenceOld.a(str);
        }
    }

    private void setOverLimitPreferenceText(String str) {
        if (DeviceUtil.IS_MIUI12) {
            this.mOverLimitOptType.b(str);
        } else {
            this.mOverLimitOptTypeOld.a(str);
        }
    }

    private void showRoamingAppExceptionVisible(boolean z) {
        if (!z) {
            this.mRoamingSettingCategory.d(this.mWhiteListSettingPreference);
        } else if (this.mRoamingSettingCategory.a((CharSequence) PREF_WHITE_LIST_SETTING) == null) {
            this.mRoamingSettingCategory.b((Preference) this.mWhiteListSettingPreference);
        }
    }

    private void showRoamingLimitVisible(boolean z) {
        if (!z) {
            this.mPreferenceScreen.d(this.mRoamingLimitCategory);
        } else if (this.mPreferenceScreen.a((CharSequence) CATEGORY_ROAMING_LIMIT_SETTING) == null) {
            this.mPreferenceScreen.b((Preference) this.mRoamingLimitCategory);
        }
    }

    private void unbindTrafficManageService() {
        TmBinderCacher.getInstance().unbindTmService(this.mTrafficManageConn);
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x0016  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAllowRoamingAppCount() {
        /*
            r6 = this;
            r0 = 0
            com.miui.networkassistant.service.IFirewallBinder r1 = r6.mFirewallBinder     // Catch:{ RemoteException -> 0x000e }
            if (r1 == 0) goto L_0x0012
            com.miui.networkassistant.service.IFirewallBinder r1 = r6.mFirewallBinder     // Catch:{ RemoteException -> 0x000e }
            com.miui.networkassistant.model.FirewallRule r2 = com.miui.networkassistant.model.FirewallRule.Allow     // Catch:{ RemoteException -> 0x000e }
            int r1 = r1.getRoamingAppCountByRule(r2)     // Catch:{ RemoteException -> 0x000e }
            goto L_0x0013
        L_0x000e:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0012:
            r1 = r0
        L_0x0013:
            if (r1 <= 0) goto L_0x0016
            goto L_0x0017
        L_0x0016:
            r1 = r0
        L_0x0017:
            android.content.Context r2 = r6.mAppContext
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2131624071(0x7f0e0087, float:1.8875311E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r1)
            r4[r0] = r5
            java.lang.String r0 = r2.getQuantityString(r3, r1, r4)
            miuix.preference.TextPreference r1 = r6.mWhiteListSettingPreference
            r1.a((java.lang.String) r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.InternationalRoamingSettingFragment.updateAllowRoamingAppCount():void");
    }

    private void updateDefaultEnabledList() {
        try {
            if (this.mCommonConfig.isRoamingAppWhiteListDefault()) {
                this.mCommonConfig.setRoamingAppWhiteListDefault(false);
                for (String next : sDefaultEnableList) {
                    if (PackageUtil.isInstalledPackage(this.mAppContext, next)) {
                        this.mFirewallBinder.setRoamingRule(next, FirewallRule.Allow);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateAllowRoamingAppCount();
    }

    /* access modifiers changed from: private */
    public void updateRoamingType(int i) {
        this.mAllowNetworkType = i;
        setAllowNetworkPreferenceText(this.mAllowNetworkTypeArray[i]);
        if (i == 0) {
            if (DeviceUtil.IS_CUSTOMIZED_VERSION) {
                this.mAllowNetworkDefaultPreference.setChecked(true);
            }
            TelephonyUtil.setDataRoamingEnabled(this.mAppContext, true);
            try {
                this.mFirewallBinder.setRoamingWhiteListEnable(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            this.mCommonConfig.setRoamingWhiteListNotifyEnable(true);
            showRoamingLimitVisible(true);
        } else if (i == 1) {
            TelephonyUtil.setDataRoamingEnabled(this.mAppContext, true);
            try {
                this.mFirewallBinder.setRoamingWhiteListEnable(true);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
            showRoamingLimitVisible(true);
            showRoamingAppExceptionVisible(true);
            return;
        } else if (i == 2) {
            TelephonyUtil.setDataRoamingEnabled(this.mAppContext, false);
            if (DeviceUtil.IS_CUSTOMIZED_VERSION) {
                this.mAllowNetworkDefaultPreference.setChecked(false);
            }
            try {
                this.mFirewallBinder.setRoamingWhiteListEnable(false);
            } catch (RemoteException e3) {
                e3.printStackTrace();
            }
            showRoamingLimitVisible(false);
        } else {
            return;
        }
        showRoamingAppExceptionVisible(false);
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.international_roaming_setting_preferences;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x013b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initPreferenceView() {
        /*
            r5 = this;
            int r0 = com.miui.networkassistant.dual.Sim.getCurrentActiveSlotNum()
            r5.mSlotNum = r0
            android.app.Activity r0 = r5.mActivity
            android.content.Intent r1 = new android.content.Intent
            java.lang.Class<com.miui.networkassistant.service.FirewallService> r2 = com.miui.networkassistant.service.FirewallService.class
            r1.<init>(r0, r2)
            android.content.ServiceConnection r2 = r5.mFirewallConn
            android.os.UserHandle r3 = b.b.c.j.B.k()
            r4 = 1
            b.b.c.j.g.a((android.content.Context) r0, (android.content.Intent) r1, (android.content.ServiceConnection) r2, (int) r4, (android.os.UserHandle) r3)
            android.content.Context r0 = r5.mAppContext
            com.miui.networkassistant.config.CommonConfig r0 = com.miui.networkassistant.config.CommonConfig.getInstance(r0)
            r5.mCommonConfig = r0
            androidx.preference.PreferenceScreen r0 = r5.getPreferenceScreen()
            r5.mPreferenceScreen = r0
            java.lang.String r0 = "category_roaming_setting"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            androidx.preference.PreferenceCategory r0 = (androidx.preference.PreferenceCategory) r0
            r5.mRoamingSettingCategory = r0
            java.lang.String r0 = "category_roaming_limit_setting"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            androidx.preference.PreferenceCategory r0 = (androidx.preference.PreferenceCategory) r0
            r5.mRoamingLimitCategory = r0
            java.lang.String r0 = "pref_allow_connect_network_switch_old"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            miuix.preference.TextPreference r0 = (miuix.preference.TextPreference) r0
            r5.mAllowNetworkPreferenceOld = r0
            java.lang.String r0 = "pref_allow_connect_network_switch"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            miuix.preference.DropDownPreference r0 = (miuix.preference.DropDownPreference) r0
            r5.mAllowNetworkPreference = r0
            java.lang.String r0 = "pref_allow_connect_network_switch_default"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            androidx.preference.CheckBoxPreference r0 = (androidx.preference.CheckBoxPreference) r0
            r5.mAllowNetworkDefaultPreference = r0
            java.lang.String r0 = "pref_whitelist_setting"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            miuix.preference.TextPreference r0 = (miuix.preference.TextPreference) r0
            r5.mWhiteListSettingPreference = r0
            androidx.preference.CheckBoxPreference r0 = r5.mAllowNetworkDefaultPreference
            r0.setOnPreferenceChangeListener(r5)
            miuix.preference.TextPreference r0 = r5.mWhiteListSettingPreference
            r0.setOnPreferenceClickListener(r5)
            java.lang.String r0 = "pref_key_roaming_daily_limit"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            androidx.preference.CheckBoxPreference r0 = (androidx.preference.CheckBoxPreference) r0
            r5.mRoamingDailyLimitCheckBox = r0
            java.lang.String r0 = "pref_key_roaming_daily_limit_value"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            miuix.preference.TextPreference r0 = (miuix.preference.TextPreference) r0
            r5.mRoamingDailyLimitTextPreference = r0
            java.lang.String r0 = "pref_key_over_limit_opt_old"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            miuix.preference.TextPreference r0 = (miuix.preference.TextPreference) r0
            r5.mOverLimitOptTypeOld = r0
            java.lang.String r0 = "pref_key_over_limit_opt"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            miuix.preference.DropDownPreference r0 = (miuix.preference.DropDownPreference) r0
            r5.mOverLimitOptType = r0
            androidx.preference.CheckBoxPreference r0 = r5.mRoamingDailyLimitCheckBox
            r0.setOnPreferenceChangeListener(r5)
            miuix.preference.TextPreference r0 = r5.mRoamingDailyLimitTextPreference
            r0.setOnPreferenceClickListener(r5)
            com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog r0 = new com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog
            android.app.Activity r1 = r5.mActivity
            com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog$SingleChoiceItemsDialogListener r2 = r5.mChoiceItemsDialogListener
            r0.<init>(r1, r2)
            r5.mSingleChoiceItemsDialog = r0
            android.content.res.Resources r0 = r5.getResources()
            r1 = 2130903070(0x7f03001e, float:1.7412948E38)
            java.lang.String[] r0 = r0.getStringArray(r1)
            r5.mSingleChoiceItemsArray = r0
            android.content.res.Resources r0 = r5.getResources()
            r1 = 2130903082(0x7f03002a, float:1.7412972E38)
            java.lang.String[] r0 = r0.getStringArray(r1)
            r5.mAllowNetworkTypeArray = r0
            java.lang.String r0 = "category_domestic_roaming_setting"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            androidx.preference.PreferenceCategory r0 = (androidx.preference.PreferenceCategory) r0
            r5.mDomesticRoamingCategory = r0
            java.lang.String r0 = "pref_key_domestic_roaming"
            androidx.preference.Preference r0 = r5.findPreference(r0)
            androidx.preference.CheckBoxPreference r0 = (androidx.preference.CheckBoxPreference) r0
            r5.mDomesticRoamingSwitch = r0
            androidx.preference.CheckBoxPreference r0 = r5.mDomesticRoamingSwitch
            r0.setOnPreferenceChangeListener(r5)
            boolean r0 = com.miui.networkassistant.utils.TelephonyUtil.isSupportDomesticRoaming()
            if (r0 == 0) goto L_0x00ef
            androidx.preference.CheckBoxPreference r0 = r5.mDomesticRoamingSwitch
            android.content.Context r1 = r5.mAppContext
            boolean r1 = com.miui.networkassistant.utils.TelephonyUtil.isDomesticRoamingEnable(r1)
            r0.setChecked(r1)
            goto L_0x00f6
        L_0x00ef:
            androidx.preference.PreferenceScreen r0 = r5.mPreferenceScreen
            androidx.preference.PreferenceCategory r1 = r5.mDomesticRoamingCategory
            r0.d(r1)
        L_0x00f6:
            boolean r0 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r0 == 0) goto L_0x0105
            miuix.preference.DropDownPreference r0 = r5.mAllowNetworkPreference
            r0.setOnPreferenceChangeListener(r5)
            miuix.preference.DropDownPreference r0 = r5.mOverLimitOptType
            r0.setOnPreferenceChangeListener(r5)
            goto L_0x010f
        L_0x0105:
            miuix.preference.TextPreference r0 = r5.mAllowNetworkPreferenceOld
            r0.setOnPreferenceClickListener(r5)
            miuix.preference.TextPreference r0 = r5.mOverLimitOptTypeOld
            r0.setOnPreferenceClickListener(r5)
        L_0x010f:
            boolean r0 = com.miui.networkassistant.utils.DeviceUtil.IS_CUSTOMIZED_VERSION
            if (r0 == 0) goto L_0x0122
            androidx.preference.PreferenceCategory r0 = r5.mRoamingSettingCategory
            miuix.preference.TextPreference r1 = r5.mAllowNetworkPreferenceOld
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r5.mRoamingSettingCategory
        L_0x011c:
            miuix.preference.DropDownPreference r1 = r5.mAllowNetworkPreference
        L_0x011e:
            r0.d(r1)
            goto L_0x0132
        L_0x0122:
            androidx.preference.PreferenceCategory r0 = r5.mRoamingSettingCategory
            androidx.preference.CheckBoxPreference r1 = r5.mAllowNetworkDefaultPreference
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r5.mRoamingSettingCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 == 0) goto L_0x011c
            miuix.preference.TextPreference r1 = r5.mAllowNetworkPreferenceOld
            goto L_0x011e
        L_0x0132:
            androidx.preference.PreferenceCategory r0 = r5.mRoamingLimitCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 == 0) goto L_0x013b
            miuix.preference.TextPreference r1 = r5.mOverLimitOptTypeOld
            goto L_0x013d
        L_0x013b:
            miuix.preference.DropDownPreference r1 = r5.mOverLimitOptType
        L_0x013d:
            r0.d(r1)
            android.content.Context r0 = r5.mAppContext
            com.miui.networkassistant.utils.NotificationUtil.cancelOpenDataRoamingNotify(r0)
            android.content.Context r0 = r5.mAppContext
            com.miui.networkassistant.utils.NotificationUtil.cancelRoamingDailyLimitWarning(r0)
            r5.bindTrafficManageService()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.InternationalRoamingSettingFragment.initPreferenceView():void");
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        this.mActivity.unbindService(this.mFirewallConn);
        unbindTrafficManageService();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mRoamingDailyLimitCheckBox) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            this.mSimUserInfo.setRoamingDailyLimitEnabled(booleanValue);
            this.mRoamingDailyLimitTextPreference.setEnabled(booleanValue);
            (DeviceUtil.IS_MIUI12 ? this.mOverLimitOptType : this.mOverLimitOptTypeOld).setEnabled(booleanValue);
            this.mSettedChanged = true;
        } else if (preference == this.mDomesticRoamingSwitch) {
            TelephonyUtil.setDomesticRoamingEnable(this.mAppContext, ((Boolean) obj).booleanValue());
        } else if (preference != this.mAllowNetworkDefaultPreference) {
            DropDownPreference dropDownPreference = this.mAllowNetworkPreference;
            if (preference == dropDownPreference) {
                onSelectAllowNetwork(CollectionUtils.getArrayIndex(dropDownPreference.b(), String.valueOf(obj)));
            } else {
                DropDownPreference dropDownPreference2 = this.mOverLimitOptType;
                if (preference == dropDownPreference2) {
                    onSelectOverLimitOpt(CollectionUtils.getArrayIndex(dropDownPreference2.b(), String.valueOf(obj)));
                }
            }
        } else if (((Boolean) obj).booleanValue()) {
            checkShowWarningDialog(0);
        } else {
            updateRoamingType(2);
        }
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mWhiteListSettingPreference) {
            g.startWithFragment(this.mActivity, RoamingWhiteListFragment.class);
        } else if (preference == this.mRoamingDailyLimitTextPreference) {
            TrafficInputDialog trafficInputDialog = this.mInputDialog;
            if (trafficInputDialog == null) {
                this.mInputDialog = new TrafficInputDialog(this.mActivity, this.mTrafficInputDialogListener);
            } else {
                trafficInputDialog.clearInputText();
            }
            this.mInputDialog.buildInputDialog(getString(R.string.pref_title_traffic_limit_number), getString(R.string.hints_input_roaming_daily_limit));
        } else if (preference == this.mOverLimitOptTypeOld) {
            this.mSingleChoiceItemsDialog.buildDialog(getString(R.string.pref_title_over_traffic_limit_warning), this.mSingleChoiceItemsArray, this.mChoiceIndex, 1);
        } else if (preference == this.mAllowNetworkPreferenceOld) {
            this.mSingleChoiceItemsDialog.buildDialog(getString(R.string.roaming_allow_network_dialog_title), this.mAllowNetworkTypeArray, this.mAllowNetworkType, 2);
        }
        return true;
    }

    public void onResume() {
        super.onResume();
        updateAllowRoamingAppCount();
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.international_roaming_setting;
    }

    public void onStop() {
        super.onStop();
        if (this.mServiceConnected && this.mSettedChanged) {
            try {
                this.mTrafficManageBinder.forceCheckRoamingDailyLimitStatus(this.mSlotNum);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
