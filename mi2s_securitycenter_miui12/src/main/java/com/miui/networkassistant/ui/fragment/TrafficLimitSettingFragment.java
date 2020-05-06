package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.miui.networkassistant.traffic.lockscreen.LockScreenTrafficHelper;
import com.miui.networkassistant.ui.base.TrafficRelatedPreFragment;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.ui.dialog.TrafficInputDialog;
import com.miui.networkassistant.utils.CollectionUtils;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class TrafficLimitSettingFragment extends TrafficRelatedPreFragment implements Preference.b, Preference.c {
    private static final String CATEGORY_KEY_LIMIT_TRAFFIC_PER_DAY = "category_key_limit_traffic_per_day";
    private static final String CATEGORY_KEY_LOCK_SCREEN = "category_key_lock_screen";
    private static final int CUSTOMIZE = 0;
    private static final int FIVE = 5;
    private static final int LOCK_SCREEN_WARNING_TYPE_COUNT = 5;
    private static final int LOCK_SCREEN_WARNING_VALUE = 3;
    private static final long MIN_TRAFFIC_PKG = 35651584;
    private static final int MSG_SERVICE_CONNECTED = 6;
    private static final int OVER_TRAFFIC_DAILY_LIMIT = 2;
    private static final String PREF_KEY_LIMIT_TRAFFIC_PER_DAY = "pref_key_limit_traffic_per_day";
    private static final String PREF_KEY_LOCK_SCREEN_SWITCH = "pref_key_lock_screen_switch";
    private static final String PREF_KEY_LOCK_SCREEN_WARNING_VALUE = "pref_key_lock_screen_warning_value";
    private static final String PREF_KEY_LOCK_SCREEN_WARNING_VALUE_OLD = "pref_key_lock_screen_warning_value_old";
    private static final String PREF_KEY_OVER_TRAFFIC_LIMIT_WARNING = "pref_key_over_traffic_limit_warning";
    private static final String PREF_KEY_OVER_TRAFFIC_LIMIT_WARNING_OLD = "pref_key_over_traffic_limit_warning_old";
    private static final String PREF_KEY_TRAFFIC_LIMIT_NUMBER = "pref_key_traffic_limit_number";
    private static final String PREF_KEY_TRAFFIC_LIMIT_NUMBER_OLD = "pref_key_traffic_limit_number_old";
    private static final String TAG = "TrafficLimit";
    private static final int TEN = 10;
    private static final int THREE = 3;
    private static final int TITLE_FILED = 2131755857;
    private static final int TRAFFIC_DAILY_LIMIT_VALUE = 1;
    private static final int TRAFFIC_LIMIT_TYPE_COUNT = 4;
    private boolean mDailyLimitChanged;
    /* access modifiers changed from: private */
    public String[] mDailyLimitValue;
    private int mDailyLimitValueSelected;
    private SingleChoiceItemsDialog.SingleChoiceItemsDialogListener mDialogListener = new SingleChoiceItemsDialog.SingleChoiceItemsDialogListener() {
        public void onSelectItemUpdate(int i, int i2) {
            if (i2 == 1) {
                TrafficLimitSettingFragment.this.onSelectTrafficDailyLimitValue(i);
            } else if (i2 == 2) {
                TrafficLimitSettingFragment.this.onSelectOverDailyLimit(i);
            } else if (i2 == 3) {
                TrafficLimitSettingFragment.this.onSelectLockScreen(i);
            }
        }
    };
    private UIHandler mHandler = new UIHandler(this);
    private boolean mLockScreenChanged;
    private CheckBoxPreference mLockScreenCheckBoxPreference;
    private boolean mLockScreenSwitchEnable;
    private String[] mLockScreenWarningArray;
    private int mLockScreenWarningSelected;
    private DropDownPreference mLockScreenWarningTextPreference;
    private TextPreference mLockScreenWarningTextPreferenceOld;
    private DropDownPreference mOverDailyLimitOperate;
    private TextPreference mOverDailyLimitOperateOld;
    private int mOverDailyLimitOperatorSelected;
    private String[] mOverLimitOperatorType;
    private SingleChoiceItemsDialog mSingleChoiceItemsDialog;
    private CheckBoxPreference mTrafficDailyLimitSwitch;
    private String[] mTrafficDailyLimitType;
    private DropDownPreference mTrafficDailyLimitValue;
    private TextPreference mTrafficDailyLimitValueOld;

    static class UIHandler extends Handler {
        private WeakReference<TrafficLimitSettingFragment> mFragment;

        UIHandler(TrafficLimitSettingFragment trafficLimitSettingFragment) {
            this.mFragment = new WeakReference<>(trafficLimitSettingFragment);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            TrafficLimitSettingFragment trafficLimitSettingFragment = (TrafficLimitSettingFragment) this.mFragment.get();
            if (trafficLimitSettingFragment != null && message.what == 6) {
                trafficLimitSettingFragment.initData();
            }
        }
    }

    private int getSelected(int i) {
        if (i == 0) {
            return 3;
        }
        if (i == 3) {
            return 0;
        }
        if (i != 5) {
            return i != 10 ? 0 : 2;
        }
        return 1;
    }

    private int getValue(int i) {
        if (i == 0) {
            return 3;
        }
        if (i == 1) {
            return 5;
        }
        if (i != 2) {
            return i != 3 ? 3 : 0;
        }
        return 10;
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (isAttatched()) {
            long j = 0;
            try {
                j = this.mTrafficManageBinder.getCurrentMonthTotalPackage(this.mSlotNum);
            } catch (RemoteException e) {
                Log.i(TAG, "getCurrentMonthTotalPackage", e);
            }
            this.mDailyLimitValueSelected = getSelected(this.mSimUserInfos[this.mSlotNum].getTrafficLimitValue());
            this.mOverDailyLimitOperatorSelected = this.mSimUserInfos[this.mSlotNum].getDailyLimitWarningType();
            updateDailyLimitValue(j);
            initTrafficLimitDailyCategory();
            initLockScreenWarningArray();
            initLockScreenWarningValue(j);
            initLockScreenMonitor();
        }
    }

    private void initLockScreenMonitor() {
        this.mLockScreenSwitchEnable = this.mSimUserInfos[this.mSlotNum].isLockScreenTrafficEnable();
        this.mLockScreenCheckBoxPreference.setChecked(this.mLockScreenSwitchEnable);
        (DeviceUtil.IS_MIUI12 ? this.mLockScreenWarningTextPreference : this.mLockScreenWarningTextPreferenceOld).setEnabled(this.mLockScreenSwitchEnable);
        setLockScreenWarningText(this.mLockScreenWarningArray[this.mLockScreenWarningSelected]);
    }

    private void initLockScreenWarningArray() {
        this.mLockScreenWarningArray = new String[5];
        String kBString = FormatBytesUtil.getKBString(this.mAppContext);
        String mBString = FormatBytesUtil.getMBString(this.mAppContext);
        this.mLockScreenWarningArray[0] = String.format(Locale.getDefault(), "%d%s", new Object[]{100, kBString});
        this.mLockScreenWarningArray[1] = String.format(Locale.getDefault(), "%d%s", new Object[]{500, kBString});
        this.mLockScreenWarningArray[2] = String.format(Locale.getDefault(), "%d%s", new Object[]{1, mBString});
        this.mLockScreenWarningArray[3] = String.format(Locale.getDefault(), "%d%s", new Object[]{2, mBString});
        this.mLockScreenWarningArray[4] = String.format(Locale.getDefault(), "%d%s", new Object[]{5, mBString});
        this.mLockScreenWarningTextPreference.a((CharSequence[]) this.mLockScreenWarningArray);
        this.mLockScreenWarningTextPreference.b((CharSequence[]) this.mLockScreenWarningArray);
    }

    private void initLockScreenWarningValue(long j) {
        this.mLockScreenWarningSelected = this.mSimUserInfos[this.mSlotNum].getLockScreenWarningLevel();
        if (this.mLockScreenWarningSelected < 0) {
            this.mLockScreenWarningSelected = LockScreenTrafficHelper.getLockScreenLevel(j);
        }
    }

    private void initTrafficLimitDailyCategory() {
        boolean dailyLimitEnabled = this.mSimUserInfos[this.mSlotNum].getDailyLimitEnabled();
        this.mTrafficDailyLimitSwitch.setChecked(dailyLimitEnabled);
        (DeviceUtil.IS_MIUI12 ? this.mTrafficDailyLimitValue : this.mTrafficDailyLimitValueOld).setEnabled(dailyLimitEnabled);
        (DeviceUtil.IS_MIUI12 ? this.mOverDailyLimitOperate : this.mOverDailyLimitOperateOld).setEnabled(dailyLimitEnabled);
        if (DeviceUtil.IS_MIUI12) {
            this.mOverDailyLimitOperate.b(this.mOverLimitOperatorType[this.mOverDailyLimitOperatorSelected]);
        } else {
            this.mOverDailyLimitOperateOld.a(this.mOverLimitOperatorType[this.mOverDailyLimitOperatorSelected]);
        }
        setTrafficDailyLimitText(this.mDailyLimitValue[this.mDailyLimitValueSelected]);
    }

    /* access modifiers changed from: private */
    public void onSelectLockScreen(int i) {
        if (i >= 0) {
            this.mLockScreenWarningSelected = i;
            setLockScreenWarningText(this.mLockScreenWarningArray[i]);
            this.mSimUserInfos[this.mSlotNum].setLockScreenWarningLevel(i);
            this.mLockScreenChanged = true;
        }
    }

    /* access modifiers changed from: private */
    public void onSelectOverDailyLimit(int i) {
        if (i >= 0) {
            this.mOverDailyLimitOperatorSelected = i;
            if (DeviceUtil.IS_MIUI12) {
                this.mOverDailyLimitOperate.b(this.mOverLimitOperatorType[i]);
            } else {
                this.mOverDailyLimitOperateOld.a(this.mOverLimitOperatorType[i]);
            }
            this.mSimUserInfos[this.mSlotNum].setDailyLimitWarningType(i);
            this.mDailyLimitChanged = true;
        }
    }

    /* access modifiers changed from: private */
    public void onSelectTrafficDailyLimitValue(int i) {
        if (i >= 0) {
            this.mDailyLimitValueSelected = i;
            if (i == 3) {
                TrafficInputDialog trafficInputDialog = new TrafficInputDialog(this.mActivity, new TrafficInputDialog.TrafficInputDialogListener() {
                    public void onTrafficUpdated(long j, int i) {
                        TrafficLimitSettingFragment.this.mSimUserInfos[TrafficLimitSettingFragment.this.mSlotNum].setCustomizeDailyLimitWarning(j);
                        TrafficLimitSettingFragment.this.updateCustomizeDailyLimit(j);
                        TrafficLimitSettingFragment trafficLimitSettingFragment = TrafficLimitSettingFragment.this;
                        trafficLimitSettingFragment.setTrafficDailyLimitText(trafficLimitSettingFragment.mDailyLimitValue[3]);
                    }
                });
                trafficInputDialog.buildInputDialog(this.mActivity.getString(R.string.pref_title_traffic_limit_number), this.mActivity.getString(R.string.hints_input_roaming_daily_limit));
                trafficInputDialog.clearInputText();
            } else {
                setTrafficDailyLimitText(this.mDailyLimitValue[i]);
            }
            this.mSimUserInfos[this.mSlotNum].setTrafficLimitValue(getValue(i));
            this.mDailyLimitChanged = true;
        }
    }

    private void setLockScreenWarningText(String str) {
        if (DeviceUtil.IS_MIUI12) {
            this.mLockScreenWarningTextPreference.b(str);
        } else {
            this.mLockScreenWarningTextPreferenceOld.a(str);
        }
    }

    /* access modifiers changed from: private */
    public void setTrafficDailyLimitText(String str) {
        if (DeviceUtil.IS_MIUI12) {
            this.mTrafficDailyLimitValue.b(str);
        } else {
            this.mTrafficDailyLimitValueOld.a(str);
        }
    }

    /* access modifiers changed from: private */
    public void updateCustomizeDailyLimit(long j) {
        this.mDailyLimitValue[3] = FormatBytesUtil.formatBytesByMB(this.mAppContext, j);
        if (j > 0) {
            this.mTrafficDailyLimitType[3] = String.format(Locale.getDefault(), getString(R.string.customize_limit_pkg), new Object[]{this.mDailyLimitValue[3]});
            return;
        }
        this.mTrafficDailyLimitType[3] = String.format(Locale.getDefault(), getString(R.string.customize_limit_pkg), new Object[]{""});
    }

    private void updateDailyLimitValue(long j) {
        this.mTrafficDailyLimitType = new String[4];
        this.mDailyLimitValue = new String[4];
        if (j <= MIN_TRAFFIC_PKG) {
            this.mTrafficDailyLimitType[0] = FormatBytesUtil.formatBytesByMB(this.mAppContext, 1048576);
            this.mTrafficDailyLimitType[1] = FormatBytesUtil.formatBytesByMB(this.mAppContext, 2097152);
            this.mTrafficDailyLimitType[2] = FormatBytesUtil.formatBytesByMB(this.mAppContext, 3145728);
        } else {
            this.mDailyLimitValue[0] = FormatBytesUtil.formatBytesWithUintLong(this.mAppContext, (3 * j) / 100);
            this.mTrafficDailyLimitType[0] = String.format(Locale.getDefault(), getString(R.string.three_percent_total_pkg), new Object[]{this.mDailyLimitValue[0]});
            this.mDailyLimitValue[1] = FormatBytesUtil.formatBytesWithUintLong(this.mAppContext, (5 * j) / 100);
            this.mTrafficDailyLimitType[1] = String.format(Locale.getDefault(), getString(R.string.five_percent_total_pkg), new Object[]{this.mDailyLimitValue[1]});
            this.mDailyLimitValue[2] = FormatBytesUtil.formatBytesWithUintLong(this.mAppContext, (j * 10) / 100);
            this.mTrafficDailyLimitType[2] = String.format(Locale.getDefault(), getString(R.string.ten_percent_total_pkg), new Object[]{this.mDailyLimitValue[2]});
        }
        this.mDailyLimitValue = this.mTrafficDailyLimitType;
        updateCustomizeDailyLimit(this.mSimUserInfos[this.mSlotNum].getCustomizeDailyLimitWarning());
        this.mTrafficDailyLimitValue.a((CharSequence[]) this.mTrafficDailyLimitType);
        this.mTrafficDailyLimitValue.b((CharSequence[]) this.mTrafficDailyLimitType);
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.traffic_limit_per_day_preferences;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(CATEGORY_KEY_LIMIT_TRAFFIC_PER_DAY);
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference(CATEGORY_KEY_LOCK_SCREEN);
        this.mOverLimitOperatorType = getResources().getStringArray(R.array.over_limit_traffic_waring_style);
        this.mTrafficDailyLimitSwitch = (CheckBoxPreference) findPreference(PREF_KEY_LIMIT_TRAFFIC_PER_DAY);
        this.mTrafficDailyLimitValueOld = (TextPreference) findPreference(PREF_KEY_TRAFFIC_LIMIT_NUMBER_OLD);
        this.mTrafficDailyLimitValue = (DropDownPreference) findPreference(PREF_KEY_TRAFFIC_LIMIT_NUMBER);
        this.mOverDailyLimitOperateOld = (TextPreference) findPreference(PREF_KEY_OVER_TRAFFIC_LIMIT_WARNING_OLD);
        this.mOverDailyLimitOperate = (DropDownPreference) findPreference(PREF_KEY_OVER_TRAFFIC_LIMIT_WARNING);
        this.mTrafficDailyLimitSwitch.setOnPreferenceChangeListener(this);
        this.mSingleChoiceItemsDialog = new SingleChoiceItemsDialog(this.mActivity, this.mDialogListener);
        this.mLockScreenCheckBoxPreference = (CheckBoxPreference) findPreference(PREF_KEY_LOCK_SCREEN_SWITCH);
        this.mLockScreenCheckBoxPreference.setOnPreferenceChangeListener(this);
        this.mLockScreenWarningTextPreferenceOld = (TextPreference) findPreference(PREF_KEY_LOCK_SCREEN_WARNING_VALUE_OLD);
        this.mLockScreenWarningTextPreference = (DropDownPreference) findPreference(PREF_KEY_LOCK_SCREEN_WARNING_VALUE);
        if (DeviceUtil.IS_MIUI12) {
            this.mTrafficDailyLimitValue.setOnPreferenceChangeListener(this);
            this.mOverDailyLimitOperate.setOnPreferenceChangeListener(this);
            this.mLockScreenWarningTextPreference.setOnPreferenceChangeListener(this);
        } else {
            this.mTrafficDailyLimitValueOld.setOnPreferenceClickListener(this);
            this.mOverDailyLimitOperateOld.setOnPreferenceClickListener(this);
            this.mLockScreenWarningTextPreferenceOld.setOnPreferenceClickListener(this);
        }
        preferenceCategory.d(DeviceUtil.IS_MIUI12 ? this.mOverDailyLimitOperateOld : this.mOverDailyLimitOperate);
        preferenceCategory.d(DeviceUtil.IS_MIUI12 ? this.mTrafficDailyLimitValueOld : this.mTrafficDailyLimitValue);
        preferenceCategory2.d(DeviceUtil.IS_MIUI12 ? this.mLockScreenWarningTextPreferenceOld : this.mLockScreenWarningTextPreference);
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeCallbacksAndMessages((Object) null);
        this.mServiceConnected = false;
    }

    public void onPause() {
        super.onPause();
        if (this.mDailyLimitChanged && this.mServiceConnected) {
            try {
                this.mTrafficManageBinder.forceCheckDailyLimitStatus(this.mSlotNum);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (this.mServiceConnected && this.mLockScreenChanged) {
            try {
                this.mTrafficManageBinder.forceCheckLockScreenStatus(this.mSlotNum);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
        this.mDailyLimitChanged = false;
        this.mLockScreenChanged = false;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mTrafficDailyLimitSwitch) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            (DeviceUtil.IS_MIUI12 ? this.mTrafficDailyLimitValue : this.mTrafficDailyLimitValueOld).setEnabled(booleanValue);
            (DeviceUtil.IS_MIUI12 ? this.mOverDailyLimitOperate : this.mOverDailyLimitOperateOld).setEnabled(booleanValue);
            this.mSimUserInfos[this.mSlotNum].setDailyLimitEnabled(booleanValue);
            this.mDailyLimitChanged = true;
        } else if (preference == this.mLockScreenCheckBoxPreference) {
            boolean booleanValue2 = ((Boolean) obj).booleanValue();
            this.mSimUserInfos[this.mSlotNum].setLockScreenTrafficEnable(booleanValue2);
            (DeviceUtil.IS_MIUI12 ? this.mLockScreenWarningTextPreference : this.mLockScreenWarningTextPreferenceOld).setEnabled(booleanValue2);
            this.mLockScreenChanged = true;
        } else {
            DropDownPreference dropDownPreference = this.mOverDailyLimitOperate;
            if (preference == dropDownPreference) {
                onSelectOverDailyLimit(CollectionUtils.getArrayIndex(dropDownPreference.b(), String.valueOf(obj)));
            } else {
                DropDownPreference dropDownPreference2 = this.mTrafficDailyLimitValue;
                if (preference == dropDownPreference2) {
                    onSelectTrafficDailyLimitValue(CollectionUtils.getArrayIndex(dropDownPreference2.b(), String.valueOf(obj)));
                } else {
                    DropDownPreference dropDownPreference3 = this.mLockScreenWarningTextPreference;
                    if (preference == dropDownPreference3) {
                        onSelectLockScreen(CollectionUtils.getArrayIndex(dropDownPreference3.b(), String.valueOf(obj)));
                    }
                }
            }
        }
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        SingleChoiceItemsDialog singleChoiceItemsDialog;
        String string;
        String[] strArr;
        int i;
        int i2;
        if (preference == this.mTrafficDailyLimitValueOld) {
            this.mSingleChoiceItemsDialog.buildDialog(getString(R.string.pref_title_traffic_limit_number), this.mTrafficDailyLimitType, this.mDailyLimitValueSelected, 1);
        } else {
            if (preference == this.mOverDailyLimitOperateOld) {
                singleChoiceItemsDialog = this.mSingleChoiceItemsDialog;
                string = getString(R.string.pref_title_over_traffic_limit_warning);
                strArr = this.mOverLimitOperatorType;
                i = this.mOverDailyLimitOperatorSelected;
                i2 = 2;
            } else if (preference == this.mLockScreenWarningTextPreferenceOld) {
                singleChoiceItemsDialog = this.mSingleChoiceItemsDialog;
                string = getString(R.string.lock_screen_warning_value);
                strArr = this.mLockScreenWarningArray;
                i = this.mLockScreenWarningSelected;
                i2 = 3;
            }
            singleChoiceItemsDialog.buildDialog(string, strArr, i, i2);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.daily_limit_and_lock_screen;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        this.mHandler.sendEmptyMessage(6);
    }
}
