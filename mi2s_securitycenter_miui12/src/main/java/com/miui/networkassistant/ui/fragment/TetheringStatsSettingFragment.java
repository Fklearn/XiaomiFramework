package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.os.RemoteException;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import b.b.c.c.a.b;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.CommonPerConstants;
import com.miui.networkassistant.ui.base.TrafficRelatedPreFragment;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.ui.dialog.TrafficInputDialog;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.util.Locale;
import miuix.preference.TextPreference;

public class TetheringStatsSettingFragment extends TrafficRelatedPreFragment implements Preference.b, Preference.c {
    private static final String PREF_KEY_TETHERING_LIMIT_SWITCH = "pref_key_tethering_stats_limit_switch";
    private static final String PREF_KEY_TETHERING_LIMIT_VALUE = "pref_key_tethering_stats_limit_value";
    private static final String PREF_KEY_TETHERING_OVER_LIMIT_OPT = "pref_key_tethering_over_limit_opt";
    private static final int SINGLE_CHOICE_LIMIT_VALUE_FLAG = 2;
    private static final int SINGLE_CHOICE_OVER_LIMIT_FLAG = 1;
    private static final int TITLE_FILED = 2131757447;
    private SingleChoiceItemsDialog.SingleChoiceItemsDialogListener mChoiceItemsDialogListener = new SingleChoiceItemsDialog.SingleChoiceItemsDialogListener() {
        public void onSelectItemUpdate(int i, int i2) {
            if (i2 == 1) {
                int unused = TetheringStatsSettingFragment.this.mOverLimitOptType = i;
                TetheringStatsSettingFragment.this.mTetheringOverLimitOptType.a(TetheringStatsSettingFragment.this.mSingleChoiceItemsArray[i]);
                TetheringStatsSettingFragment.this.mCommonConfig.setTetheringOverLimitOptType(i);
            } else if (i2 == 2) {
                if (i < 4) {
                    long access$700 = TetheringStatsSettingFragment.this.getLimitValue(i);
                    TetheringStatsSettingFragment.this.mTetheringLimitTextPreference.a(FormatBytesUtil.formatBytes(TetheringStatsSettingFragment.this.mAppContext, access$700));
                    TetheringStatsSettingFragment.this.mCommonConfig.setTetheringLimitTraffic(access$700);
                } else if (i == 4) {
                    TetheringStatsSettingFragment.this.showSetCustomValueDialog();
                } else {
                    new IllegalArgumentException("select value index illegal " + i);
                }
                int unused2 = TetheringStatsSettingFragment.this.mLimitValueType = i;
            }
            boolean unused3 = TetheringStatsSettingFragment.this.mDataChanged = true;
        }
    };
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    /* access modifiers changed from: private */
    public boolean mDataChanged;
    private TrafficInputDialog mInputDialog;
    private String[] mLimitValueArray;
    /* access modifiers changed from: private */
    public int mLimitValueType;
    /* access modifiers changed from: private */
    public int mOverLimitOptType;
    /* access modifiers changed from: private */
    public String[] mSingleChoiceItemsArray;
    private SingleChoiceItemsDialog mSingleChoiceItemsDialog;
    private CheckBoxPreference mTetheringLimitCheckBox;
    /* access modifiers changed from: private */
    public TextPreference mTetheringLimitTextPreference;
    /* access modifiers changed from: private */
    public TextPreference mTetheringOverLimitOptType;
    private TrafficInputDialog.TrafficInputDialogListener mTrafficInputDialogListener = new TrafficInputDialog.TrafficInputDialogListener() {
        public void onTrafficUpdated(long j, int i) {
            TetheringStatsSettingFragment.this.mTetheringLimitTextPreference.a(FormatBytesUtil.formatBytes(TetheringStatsSettingFragment.this.mAppContext, j));
            TetheringStatsSettingFragment.this.mCommonConfig.setTetheringLimitTraffic(j);
            boolean unused = TetheringStatsSettingFragment.this.mDataChanged = true;
        }
    };

    private int getLimitSelected(long j) {
        if (j == CommonPerConstants.DEFAULT.TETHERING_LIMIT_TRAFFIC_DEFAULT) {
            return 0;
        }
        if (j == 104857600) {
            return 1;
        }
        if (j == 157286400) {
            return 2;
        }
        return j == 209715200 ? 3 : 4;
    }

    /* access modifiers changed from: private */
    public long getLimitValue(int i) {
        if (i == 0) {
            return CommonPerConstants.DEFAULT.TETHERING_LIMIT_TRAFFIC_DEFAULT;
        }
        if (i == 1) {
            return 104857600;
        }
        if (i == 2) {
            return 157286400;
        }
        if (i != 3) {
            return CommonPerConstants.DEFAULT.TETHERING_LIMIT_TRAFFIC_DEFAULT;
        }
        return 209715200;
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mServiceConnected) {
            boolean tetheringLimitEnabled = this.mCommonConfig.getTetheringLimitEnabled();
            this.mTetheringLimitCheckBox.setChecked(tetheringLimitEnabled);
            this.mTetheringLimitTextPreference.setEnabled(tetheringLimitEnabled);
            this.mTetheringOverLimitOptType.setEnabled(tetheringLimitEnabled);
            long tetheringLimitTraffic = this.mCommonConfig.getTetheringLimitTraffic();
            this.mTetheringLimitTextPreference.a(FormatBytesUtil.formatBytes(this.mAppContext, tetheringLimitTraffic));
            this.mLimitValueType = getLimitSelected(tetheringLimitTraffic);
            this.mOverLimitOptType = this.mCommonConfig.getTetheringOverLimitOptType();
            this.mTetheringOverLimitOptType.a(this.mSingleChoiceItemsArray[this.mOverLimitOptType]);
            initTetherLimitArray();
        }
    }

    private void initTetherLimitArray() {
        this.mLimitValueArray = new String[5];
        String mBString = FormatBytesUtil.getMBString(this.mAppContext);
        this.mLimitValueArray[0] = String.format(Locale.getDefault(), "%d%s", new Object[]{50, mBString});
        this.mLimitValueArray[1] = String.format(Locale.getDefault(), "%d%s", new Object[]{100, mBString});
        this.mLimitValueArray[2] = String.format(Locale.getDefault(), "%d%s", new Object[]{150, mBString});
        this.mLimitValueArray[3] = String.format(Locale.getDefault(), "%d%s", new Object[]{200, mBString});
        this.mLimitValueArray[4] = this.mAppContext.getString(R.string.tether_limit_value_custom);
    }

    /* access modifiers changed from: private */
    public void showSetCustomValueDialog() {
        TrafficInputDialog trafficInputDialog = this.mInputDialog;
        if (trafficInputDialog == null) {
            this.mInputDialog = new TrafficInputDialog(this.mActivity, this.mTrafficInputDialogListener);
        } else {
            trafficInputDialog.clearInputText();
        }
        this.mInputDialog.buildInputDialog(getString(R.string.tether_custom_dialog_title), getString(R.string.hints_input_roaming_daily_limit));
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.tethering_stats_setting_preferences;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        this.mSingleChoiceItemsDialog = new SingleChoiceItemsDialog(this.mActivity, this.mChoiceItemsDialogListener);
        this.mSingleChoiceItemsArray = getResources().getStringArray(R.array.tether_limit_warn_type);
        this.mTetheringLimitCheckBox = (CheckBoxPreference) findPreference(PREF_KEY_TETHERING_LIMIT_SWITCH);
        this.mTetheringLimitTextPreference = (TextPreference) findPreference(PREF_KEY_TETHERING_LIMIT_VALUE);
        this.mTetheringOverLimitOptType = (TextPreference) findPreference(PREF_KEY_TETHERING_OVER_LIMIT_OPT);
        this.mTetheringLimitCheckBox.setOnPreferenceChangeListener(this);
        this.mTetheringLimitTextPreference.setOnPreferenceClickListener(this);
        this.mTetheringOverLimitOptType.setOnPreferenceClickListener(this);
        this.mCommonConfig = CommonConfig.getInstance(this.mAppContext);
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        if (this.mServiceConnected && this.mDataChanged) {
            try {
                this.mTrafficManageBinder.forceCheckTethingSettingStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mTetheringLimitCheckBox) {
            this.mCommonConfig.setTetheringLimitEnabled(booleanValue);
            this.mTetheringLimitTextPreference.setEnabled(booleanValue);
            this.mTetheringOverLimitOptType.setEnabled(booleanValue);
            this.mDataChanged = true;
        }
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mTetheringLimitTextPreference) {
            this.mSingleChoiceItemsDialog.buildDialog(getString(R.string.tether_limit_dialog_title), this.mLimitValueArray, this.mLimitValueType, 2);
        } else if (preference == this.mTetheringOverLimitOptType) {
            this.mSingleChoiceItemsDialog.buildDialog(getString(R.string.pref_title_over_traffic_limit_warning), this.mSingleChoiceItemsArray, this.mOverLimitOptType, 1);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.pref_key_tethering_setting;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        postOnUiThread(new b(this) {
            public void runOnUiThread() {
                TetheringStatsSettingFragment.this.initData();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void resetTitle() {
    }
}
