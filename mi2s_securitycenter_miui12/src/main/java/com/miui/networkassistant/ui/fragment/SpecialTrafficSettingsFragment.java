package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import b.b.c.c.a.b;
import b.b.c.c.b.g;
import com.miui.networkassistant.ui.base.TrafficRelatedPreFragment;
import com.miui.networkassistant.ui.dialog.DatePickerDialog;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.ui.dialog.TimePickerDialog;
import com.miui.networkassistant.ui.dialog.TrafficInputDialog;
import com.miui.networkassistant.utils.CollectionUtils;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.util.Calendar;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class SpecialTrafficSettingsFragment extends TrafficRelatedPreFragment implements TrafficInputDialog.TrafficInputDialogListener, TimePickerDialog.TimePickerDialogListener, DatePickerDialog.DatePickerDialogListener, Preference.c, Preference.b {
    private static final int ACTION_FLAG_NORMAL_EXTRA_TRAFFIC = 1;
    private static final int ACTION_FLAG_NORMAL_HALF_YEAR_TRAFFIC = 2;
    private static final int ACTION_FLAG_NORMAL_LEISURE_TRAFFIC = 0;
    private static final String CATEGORY_HALF_YEAR_PACKAGE = "category_half_year_package";
    private static final String CATEGORY_KEY_LEISURE_TRAFFIC = "category_key_leisure_traffic";
    private static final int FLAG_END_TIME = 2;
    private static final int FLAG_START_TIME = 1;
    private static final int OVER_LEISURE_TRAFFIC_LIMIT = 4;
    private static final String PREF_DATA_USAGE_IGNORE_SETTINGS = "pref_data_usage_ignore_settings";
    private static final String PREF_KEY_END_TIME = "pref_key_end_time";
    private static final String PREF_KEY_EXTRA_TRAFFIC = "pref_key_extra_traffic";
    private static final String PREF_KEY_HALF_YEAR_START = "pref_key_half_year_start";
    private static final String PREF_KEY_HALF_YEAR_TRAFFIC = "pref_key_half_year_traffic";
    private static final String PREF_KEY_HALF_YEAR_TRAFFIC_SETTING_SWITCH = "pref_key_half_year_traffic_setting_switch";
    private static final String PREF_KEY_LEISURE_TRAFFIC = "pref_key_leisure_traffic";
    private static final String PREF_KEY_LEISURE_TRAFFIC_SETTING_SWITCH = "pref_key_leisure_traffic_setting_switch";
    private static final String PREF_KEY_START_TIME = "pref_key_start_time";
    private static final String PREF_LEISURE_TRAFFIC_LIMIT = "pref_leisure_traffic_limit";
    private static final String PREF_LEISURE_TRAFFIC_LIMIT_OLD = "pref_leisure_traffic_limit_old";
    private static final int TITLE_FILED = 2131757463;
    private boolean mChanged;
    private TextPreference mDataUsageIgnorePreference;
    private DatePickerDialog mDatePickerDialog;
    private SingleChoiceItemsDialog.SingleChoiceItemsDialogListener mDialogListener = new SingleChoiceItemsDialog.SingleChoiceItemsDialogListener() {
        public void onSelectItemUpdate(int i, int i2) {
            if (i2 == 4) {
                int unused = SpecialTrafficSettingsFragment.this.mOverLeisureLimitSelected = i;
                SpecialTrafficSettingsFragment.this.onSelectLeisureTrafficLimit(i);
            }
        }
    };
    private int mEndHour;
    private int mEndMinute;
    private TextPreference mEndTimePreference;
    private TextPreference mExtraPackagePreference;
    private PreferenceCategory mHalfYearCategory;
    private CheckBoxPreference mHalfYearTrafficPreference;
    private TextPreference mHalfYearTrafficStart;
    private TextPreference mHalfYearTrafficValue;
    private TrafficInputDialog mInputDialog;
    private CheckBoxPreference mLeisureSettingCheckBoxPreference;
    private DropDownPreference mLeisureTrafficLimit;
    private TextPreference mLeisureTrafficLimitOld;
    private TextPreference mLeisureTrafficPreference;
    /* access modifiers changed from: private */
    public int mOverLeisureLimitSelected;
    private String[] mOverLimitOperatorType;
    private SingleChoiceItemsDialog mSingleChoiceItemsDialog;
    private int mStartHour;
    private int mStartMinute;
    private TextPreference mStartTimePreference;
    private TimePickerDialog mTimePickerDialog;

    private void activateComponent(boolean z) {
        this.mLeisureTrafficPreference.setEnabled(z);
        this.mStartTimePreference.setEnabled(z);
        this.mEndTimePreference.setEnabled(z);
        (DeviceUtil.IS_MIUI12 ? this.mLeisureTrafficLimit : this.mLeisureTrafficLimitOld).setEnabled(z);
    }

    private void initAppTrafficIgnoreCount() {
        int i;
        try {
            i = this.mTrafficManageBinder.getIgnoreAppCount(this.mSlotNum);
        } catch (Exception e) {
            e.printStackTrace();
            i = 0;
        }
        this.mDataUsageIgnorePreference.a(this.mAppContext.getResources().getQuantityString(R.plurals.traffic_setting_app_count, i, new Object[]{Integer.valueOf(i)}));
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mServiceConnected) {
            boolean isLeisureDataUsageOn = this.mSimUserInfos[this.mSlotNum].isLeisureDataUsageOn();
            this.mLeisureSettingCheckBoxPreference.setChecked(isLeisureDataUsageOn);
            activateComponent(isLeisureDataUsageOn);
            this.mLeisureTrafficPreference.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mSimUserInfos[this.mSlotNum].getLeisureDataUsageTotal(), 2));
            long leisureDataUsageFromTime = this.mSimUserInfos[this.mSlotNum].getLeisureDataUsageFromTime();
            this.mStartHour = DateUtil.getHourInMilliTime(leisureDataUsageFromTime);
            this.mStartMinute = DateUtil.getMinuteInMilliTime(leisureDataUsageFromTime);
            this.mStartTimePreference.a(DateUtil.getFormatTime(this.mStartHour, this.mStartMinute));
            this.mOverLeisureLimitSelected = this.mSimUserInfos[this.mSlotNum].isLeisureDataUsageOverLimitWarning() ^ true ? 1 : 0;
            if (DeviceUtil.IS_MIUI12) {
                this.mLeisureTrafficLimit.b(this.mOverLimitOperatorType[this.mOverLeisureLimitSelected]);
            } else {
                this.mLeisureTrafficLimitOld.a(this.mOverLimitOperatorType[this.mOverLeisureLimitSelected]);
            }
            long leisureDataUsageToTime = this.mSimUserInfos[this.mSlotNum].getLeisureDataUsageToTime();
            this.mEndHour = DateUtil.getHourInMilliTime(leisureDataUsageToTime);
            this.mEndMinute = DateUtil.getMinuteInMilliTime(leisureDataUsageToTime);
            this.mEndTimePreference.a(DateUtil.getFormatTime(this.mEndHour, this.mEndMinute));
            initExtraPackage();
            initHalfYearPackage();
        }
    }

    private void initExtraPackage() {
        if (DateUtil.isCurrentCycleMonth(this.mSimUserInfos[this.mSlotNum].getDataUsageOverlayPackageTime(), this.mSimUserInfos[this.mSlotNum].getMonthStart())) {
            long dataUsageOverlayPackage = this.mSimUserInfos[this.mSlotNum].getDataUsageOverlayPackage();
            if (dataUsageOverlayPackage > 0) {
                this.mExtraPackagePreference.a(FormatBytesUtil.formatBytes(this.mAppContext, dataUsageOverlayPackage, 2));
            }
        }
    }

    private void initHalfYearPackage() {
        boolean isHalfYearPackageEnable = this.mSimUserInfos[this.mSlotNum].isHalfYearPackageEnable();
        this.mHalfYearTrafficPreference.setChecked(isHalfYearPackageEnable);
        this.mHalfYearTrafficValue.setEnabled(isHalfYearPackageEnable);
        this.mHalfYearTrafficStart.setEnabled(isHalfYearPackageEnable);
        this.mHalfYearTrafficValue.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mSimUserInfos[this.mSlotNum].getHalfYearPackageValue(), 2));
        this.mHalfYearTrafficStart.a(DateUtil.formatDataTime(this.mSimUserInfos[this.mSlotNum].getHalfYearPackageBeginTime(), 2));
    }

    /* access modifiers changed from: private */
    public void onSelectLeisureTrafficLimit(int i) {
        if (i >= 0) {
            this.mOverLeisureLimitSelected = i;
            if (DeviceUtil.IS_MIUI12) {
                this.mLeisureTrafficLimit.b(this.mOverLimitOperatorType[i]);
            } else {
                this.mLeisureTrafficLimitOld.a(this.mOverLimitOperatorType[i]);
            }
            this.mSimUserInfos[this.mSlotNum].toggleLeisureDataUsageOverLimitWarning(i == 0);
            this.mChanged = true;
        }
    }

    private void setChanged() {
        this.mChanged = true;
        if (isAttatched()) {
            this.mActivity.setResult(-1);
        }
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.special_trafffic_setting_preferences;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(CATEGORY_KEY_LEISURE_TRAFFIC);
        this.mOverLimitOperatorType = getResources().getStringArray(R.array.over_limit_traffic_waring_style);
        this.mSingleChoiceItemsDialog = new SingleChoiceItemsDialog(this.mActivity, this.mDialogListener);
        this.mLeisureSettingCheckBoxPreference = (CheckBoxPreference) findPreference(PREF_KEY_LEISURE_TRAFFIC_SETTING_SWITCH);
        this.mLeisureSettingCheckBoxPreference.setOnPreferenceChangeListener(this);
        this.mLeisureTrafficPreference = (TextPreference) findPreference(PREF_KEY_LEISURE_TRAFFIC);
        this.mLeisureTrafficPreference.setOnPreferenceClickListener(this);
        this.mStartTimePreference = (TextPreference) findPreference(PREF_KEY_START_TIME);
        this.mStartTimePreference.setOnPreferenceClickListener(this);
        this.mEndTimePreference = (TextPreference) findPreference(PREF_KEY_END_TIME);
        this.mEndTimePreference.setOnPreferenceClickListener(this);
        this.mExtraPackagePreference = (TextPreference) findPreference(PREF_KEY_EXTRA_TRAFFIC);
        this.mExtraPackagePreference.setOnPreferenceClickListener(this);
        this.mDataUsageIgnorePreference = (TextPreference) findPreference(PREF_DATA_USAGE_IGNORE_SETTINGS);
        this.mDataUsageIgnorePreference.setOnPreferenceClickListener(this);
        this.mHalfYearTrafficPreference = (CheckBoxPreference) findPreference(PREF_KEY_HALF_YEAR_TRAFFIC_SETTING_SWITCH);
        this.mHalfYearTrafficPreference.setOnPreferenceChangeListener(this);
        this.mHalfYearTrafficValue = (TextPreference) findPreference(PREF_KEY_HALF_YEAR_TRAFFIC);
        this.mHalfYearTrafficValue.setOnPreferenceClickListener(this);
        this.mHalfYearTrafficStart = (TextPreference) findPreference(PREF_KEY_HALF_YEAR_START);
        this.mHalfYearTrafficStart.setOnPreferenceClickListener(this);
        this.mLeisureTrafficLimit = (DropDownPreference) findPreference(PREF_LEISURE_TRAFFIC_LIMIT);
        this.mLeisureTrafficLimitOld = (TextPreference) findPreference(PREF_LEISURE_TRAFFIC_LIMIT_OLD);
        if (DeviceUtil.IS_MIUI12) {
            this.mLeisureTrafficLimit.setOnPreferenceChangeListener(this);
        } else {
            this.mLeisureTrafficLimitOld.setOnPreferenceClickListener(this);
        }
        preferenceCategory.d(DeviceUtil.IS_MIUI12 ? this.mLeisureTrafficLimitOld : this.mLeisureTrafficLimit);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mHalfYearCategory = (PreferenceCategory) findPreference(CATEGORY_HALF_YEAR_PACKAGE);
        preferenceScreen.d(this.mHalfYearCategory);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mInputDialog = new TrafficInputDialog(this.mActivity, this);
        this.mTimePickerDialog = new TimePickerDialog(this.mActivity, this);
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDateChanged(int i, int i2, int i3) {
        long somedayTimeMillis = DateUtil.getSomedayTimeMillis(i, i2 + 1, i3);
        this.mSimUserInfos[this.mSlotNum].setHalfYearPackageBeginTime(somedayTimeMillis);
        this.mHalfYearTrafficStart.a(DateUtil.formatDataTime(somedayTimeMillis, 2));
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mServiceConnected && this.mChanged) {
            try {
                this.mTrafficManageBinder.updateTrafficStatusMonitor(this.mSlotNum);
                boolean isDailyUsedCardEffective = this.mSimUserInfos[this.mSlotNum].isDailyUsedCardEffective();
                if (isDailyUsedCardEffective) {
                    this.mTrafficManageBinder.toggleDataUsageAutoCorrection(false, this.mSlotNum);
                }
                if (this.mSimUserInfos[this.mSlotNum].isCorrectionEffective() && !isDailyUsedCardEffective) {
                    this.mTrafficManageBinder.startCorrection(false, this.mSlotNum, false, 1);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mLeisureSettingCheckBoxPreference) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            activateComponent(booleanValue);
            this.mSimUserInfos[this.mSlotNum].toggleLeisureDataUsageOn(booleanValue);
            return true;
        } else if (preference == this.mHalfYearTrafficPreference) {
            boolean booleanValue2 = ((Boolean) obj).booleanValue();
            this.mSimUserInfos[this.mSlotNum].setHalfYearPackageEnable(booleanValue2);
            this.mHalfYearTrafficValue.setEnabled(booleanValue2);
            this.mHalfYearTrafficStart.setEnabled(booleanValue2);
            return true;
        } else if (preference != this.mLeisureTrafficLimit) {
            return true;
        } else {
            onSelectLeisureTrafficLimit(CollectionUtils.getArrayIndex(this.mOverLimitOperatorType, String.valueOf(obj)));
            return true;
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        TimePickerDialog timePickerDialog;
        int i;
        int i2;
        if (preference == this.mLeisureTrafficPreference) {
            this.mInputDialog.buildInputDialog(this.mActivity.getString(R.string.free_time_traffic_title), this.mActivity.getString(R.string.free_time_traffic_hint), 0);
        } else {
            if (preference == this.mStartTimePreference) {
                this.mTimePickerDialog.buildTimePickerdialog(this.mAppContext.getString(R.string.power_save_on_time_start_time_summary_na), 1);
                timePickerDialog = this.mTimePickerDialog;
                i = this.mStartHour;
                i2 = this.mStartMinute;
            } else if (preference == this.mEndTimePreference) {
                this.mTimePickerDialog.buildTimePickerdialog(this.mAppContext.getString(R.string.power_save_on_time_end_time_summary_na), 2);
                timePickerDialog = this.mTimePickerDialog;
                i = this.mEndHour;
                i2 = this.mEndMinute;
            } else if (preference == this.mExtraPackagePreference) {
                this.mInputDialog.buildInputDialog(this.mActivity.getString(R.string.input_traffic_pkg), this.mActivity.getString(R.string.pkg_traffic), 1);
            } else {
                if (preference == this.mDataUsageIgnorePreference) {
                    g.startWithFragmentForResult(this.mActivity, (Class<? extends Fragment>) DataUsageIgnoreAppListFragment.class, (Bundle) null, 0);
                } else if (preference == this.mHalfYearTrafficValue) {
                    this.mInputDialog.buildInputDialog(this.mActivity.getString(R.string.input_traffic_pkg), this.mActivity.getString(R.string.pkg_traffic), 2);
                } else if (preference == this.mHalfYearTrafficStart) {
                    this.mDatePickerDialog = new DatePickerDialog(this.mActivity, this);
                    this.mDatePickerDialog.buildDatePickerDialog(this.mActivity.getString(R.string.input_traffic_pkg));
                    Calendar instance = Calendar.getInstance();
                    this.mDatePickerDialog.setData(instance.get(1), instance.get(2), instance.get(5));
                } else if (preference == this.mLeisureTrafficLimitOld) {
                    this.mSingleChoiceItemsDialog.buildDialog(getString(R.string.pref_title_over_traffic_limit_warning), this.mOverLimitOperatorType, this.mOverLeisureLimitSelected, 4);
                }
                return true;
            }
            timePickerDialog.setTimePicker(i, i2);
            return true;
        }
        this.mInputDialog.clearInputText();
        return true;
    }

    public void onResume() {
        super.onResume();
        initAppTrafficIgnoreCount();
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.pref_special_package_setting;
    }

    public void onTimeUpdated(int i, int i2, int i3) {
        if (i3 == 1) {
            this.mStartHour = i;
            this.mStartMinute = i2;
            this.mStartTimePreference.a(DateUtil.getFormatTime(this.mStartHour, this.mStartMinute));
            this.mSimUserInfos[this.mSlotNum].saveLeisureDataUsageFromTime(DateUtil.getMillisUsingHM(this.mStartHour, this.mStartMinute));
        } else if (i3 == 2) {
            this.mEndHour = i;
            this.mEndMinute = i2;
            this.mEndTimePreference.a(DateUtil.getFormatTime(this.mEndHour, this.mEndMinute));
            this.mSimUserInfos[this.mSlotNum].saveLeisureDataUsageToTime(DateUtil.getMillisUsingHM(this.mEndHour, this.mEndMinute));
        }
        setChanged();
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        postOnUiThread(new b(this) {
            public void runOnUiThread() {
                SpecialTrafficSettingsFragment.this.initData();
            }
        });
    }

    public void onTrafficUpdated(long j, int i) {
        if (i != 0) {
            if (i == 1) {
                this.mExtraPackagePreference.a(FormatBytesUtil.formatBytes(this.mAppContext, j, 2));
                this.mSimUserInfos[this.mSlotNum].saveDataUsageOverlayPackage(j);
                this.mSimUserInfos[this.mSlotNum].saveDataUsageOverlayPackageTime(System.currentTimeMillis());
                try {
                    this.mTrafficManageBinder.updateGlobleDataUsage(this.mSlotNum);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (i == 2) {
                this.mSimUserInfos[this.mSlotNum].setHalfYearPackageValue(j);
                this.mHalfYearTrafficValue.a(FormatBytesUtil.formatBytes(this.mAppContext, j, 2));
            } else {
                return;
            }
        } else if (this.mServiceConnected) {
            this.mSimUserInfos[this.mSlotNum].saveLeisureDataUsageTotal(j);
            this.mLeisureTrafficPreference.a(FormatBytesUtil.formatBytes(this.mAppContext, j, 2));
            this.mSimUserInfos[this.mSlotNum].saveTrafficCorrectionAutoModify(false);
        }
        setChanged();
    }
}
