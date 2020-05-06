package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.c.b.g;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.DailyCardBrandConfig;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.DailyCardBrandInfo;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.ui.base.TrafficRelatedPreFragment;
import com.miui.networkassistant.ui.dialog.DateShowDialog;
import com.miui.networkassistant.ui.dialog.MessageDialog;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.ui.dialog.SeekBarDialog;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.ui.dialog.TrafficInputDialog;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.CollectionUtils;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.TextPrepareUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class PackageSettingFragment extends TrafficRelatedPreFragment implements Preference.b, Preference.c, TrafficInputDialog.TrafficInputDialogListener, DateShowDialog.DateDialogListener, SeekBarDialog.SeekBarChangeListener, SingleChoiceItemsDialog.SingleChoiceItemsDialogListener {
    private static final int ACTION_DAILY_PACKAGE = 4;
    private static final int ACTION_FLAG_DAILY_BRAND = 7;
    private static final int ACTION_FLAG_MANUAL_LEISURE_TRAFFIC = 6;
    private static final int ACTION_FLAG_NORMAL_MONTH_TOTAL = 1;
    private static final int ACTION_FLAG_NOT_LIMIT_TOTAL = 8;
    private static final int ACTION_SELECT_BRAND = 3;
    private static final int ACTION_USAGE_PACKAGE = 5;
    private static final int MSG_TRAFFIC_MANAGE_SERVICE_CONNECTED = 1;
    private static final int OVER_NORMAL_TRAFFIC_LIMIT = 2;
    private static final String PER_KEY_NORMAL_PACKAGE_SETTING = "pref_normal_package_setting";
    private static final String PREF_KEY_ADJUST_USAGE = "pref_daily_adjust_traffic";
    private static final String PREF_KEY_AUTO_MODIFY_PACKAGE = "pref_key_auto_modify_package";
    private static final String PREF_KEY_DAILY_CARD_BRAND = "pref_daily_card_brand";
    private static final String PREF_KEY_DAILY_CARD_BRAND_OLD = "pref_daily_card_brand_old";
    private static final String PREF_KEY_DAILY_CARD_PACKAGE = "pref_daily_card_package";
    private static final String PREF_KEY_LEISURE_ADJUST_USAGE = "pref_leisure_adjust_traffic";
    private static final String PREF_KEY_MONTHLY_PACKAGE = "pref_key_monthly_package";
    private static final String PREF_KEY_PACKAGE_TYPE = "pref_key_package_type";
    private static final String PREF_KEY_PACKAGE_TYPE_CATEGORY = "pref_key_package_type_category";
    private static final String PREF_KEY_PACKAGE_TYPE_OLD = "pref_key_package_type_old";
    private static final String PREF_KEY_SPECIAL_PACKAGE_SETTING = "pref_key_special_package_setting";
    private static final String PREF_MONTH_WARNING = "pref_month_warning";
    private static final String PREF_MORE_SETTINGS = "pref_more_settings";
    private static final String PREF_NORMAL_TRAFFIC_LIMIT = "pref_normal_traffic_limit";
    private static final String PREF_NORMAL_TRAFFIC_LIMIT_OLD = "pref_normal_traffic_limit_old";
    private static final String PREF_NOT_LIMIT_TRAFFIC_LIMIT = "pref_not_limit_traffic_limit";
    private static final String PREF_PACKAGE_BEGIN_DATE = "pref_package_begin_date";
    /* access modifiers changed from: private */
    public static final String TAG = "PackageSettingFragment";
    private static final int TITLE_FILED = 2131757179;
    private Button mActionBarTipButton;
    private TextPreference mAdjustUsagePreference;
    /* access modifiers changed from: private */
    public List<String> mAllNetworkAccessedApps;
    private CheckBoxPreference mAutoModifyBoxPreference;
    private boolean mBrandChange;
    private boolean mChanged;
    private DropDownPreference mDailyCardBrandPreference;
    private TextPreference mDailyCardBrandPreferenceOld;
    private TextPreference mDailyCardPackagePreference;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                PackageSettingFragment.this.initData();
            }
        }
    };
    private TrafficInputDialog mInputDialog;
    /* access modifiers changed from: private */
    public boolean mIsAppListInited = false;
    private TextPreference mLeisureAdjustUsagePreference;
    /* access modifiers changed from: private */
    public AppMonitorWrapper mMonitorCenter;
    private AppMonitorWrapper.AppMonitorListener mMonitorCenterListener = new AppMonitorWrapper.AppMonitorListener() {
        public void onAppListUpdated() {
            Log.i(PackageSettingFragment.TAG, "onAppListUpdated");
            ArrayList<AppInfo> networkAccessedAppList = PackageSettingFragment.this.mMonitorCenter.getNetworkAccessedAppList();
            if (networkAccessedAppList != null) {
                List unused = PackageSettingFragment.this.mAllNetworkAccessedApps = new ArrayList();
                for (AppInfo appInfo : networkAccessedAppList) {
                    PackageSettingFragment.this.mAllNetworkAccessedApps.add(appInfo.packageName.toString());
                }
                boolean unused2 = PackageSettingFragment.this.mIsAppListInited = true;
            }
        }
    };
    private TextPreference mMonthCycleDate;
    private TextPreference mMonthWarningPreference;
    private PreferenceCategory mMonthlyPackageCategory;
    private PreferenceCategory mMorePreferenceCategory;
    private DropDownPreference mNormalTrafficLimit;
    private TextPreference mNormalTrafficLimitOld;
    private TextPreference mNotLimitedTrafficLimit;
    private String[] mOverLimitOperatorType;
    private int mOverNormalLimitSelected;
    private PreferenceCategory mPackageTypeCategory;
    private DropDownPreference mPackageTypePreference;
    private TextPreference mPackageTypePreferenceOld;
    private TextPreference mPreNormalMonthPackage;
    /* access modifiers changed from: private */
    public SingleChoiceItemsDialog mSingleChoiceItemsDialog;
    private TextPreference mSpecialPackageSetting;
    private boolean mTrafficLimitChanged;
    /* access modifiers changed from: private */
    public String[] mTrafficPackageType;
    /* access modifiers changed from: private */
    public int mTrafficPackageTypeSelected;

    private static class TrafficOptionDialogListener implements OptionTipDialog.OptionDialogListener {
        private WeakReference<Activity> mActivityRef;

        public TrafficOptionDialogListener(Activity activity) {
            this.mActivityRef = new WeakReference<>(activity);
        }

        public void onOptionUpdated(boolean z) {
            Activity activity = (Activity) this.mActivityRef.get();
            if (activity != null) {
                Settings.System.putInt(activity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, z ? 1 : 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mServiceConnected && this.mSimUserInfos[this.mSlotNum] != null) {
            updatePreference();
            if (this.mSimUserInfos[this.mSlotNum].isOversea()) {
                this.mActionBarTipButton.setVisibility(8);
            }
            if (this.mSimUserInfos[this.mSlotNum].isSupportCorrection()) {
                this.mMorePreferenceCategory.b((Preference) this.mAutoModifyBoxPreference);
                this.mAutoModifyBoxPreference.setChecked(this.mSimUserInfos[this.mSlotNum].isTrafficCorrectionAutoModify());
            } else {
                this.mMorePreferenceCategory.d(this.mAutoModifyBoxPreference);
            }
            long dataUsageTotal = this.mSimUserInfos[this.mSlotNum].getDataUsageTotal();
            if (dataUsageTotal >= 0) {
                this.mPreNormalMonthPackage.a(FormatBytesUtil.formatBytes(this.mAppContext, dataUsageTotal, 2));
            } else {
                this.mPreNormalMonthPackage.a((int) R.string.pref_data_usage_not_set);
            }
            setMonthWarningPreferenceValue(this.mSimUserInfos[this.mSlotNum].getDataUsageWarning());
            setMonthCycleDate(this.mSimUserInfos[this.mSlotNum].getMonthStart());
            this.mOverNormalLimitSelected = this.mSimUserInfos[this.mSlotNum].isDataUsageOverLimitStopNetwork() ? 1 : 0;
            if (DeviceUtil.IS_MIUI12) {
                this.mNormalTrafficLimit.b(this.mOverLimitOperatorType[this.mOverNormalLimitSelected]);
            } else {
                this.mNormalTrafficLimitOld.a(this.mOverLimitOperatorType[this.mOverNormalLimitSelected]);
            }
            this.mDailyCardBrandPreference.b(this.mSimUserInfos[this.mSlotNum].getDailyUsedCardBrand());
            this.mDailyCardBrandPreferenceOld.a(this.mSimUserInfos[this.mSlotNum].getDailyUsedCardBrand());
            this.mDailyCardPackagePreference.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mSimUserInfos[this.mSlotNum].getDailyUsedCardPackage(), 2));
            this.mNotLimitedTrafficLimit.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mSimUserInfos[this.mSlotNum].getNotLimitedCardPackage(), 2));
            this.mTrafficPackageTypeSelected = this.mSimUserInfos[this.mSlotNum].getBrand();
            int i = this.mTrafficPackageTypeSelected;
            if (i < 0) {
                i = 0;
            }
            this.mTrafficPackageTypeSelected = i;
            if (DeviceUtil.IS_MIUI12) {
                this.mPackageTypePreference.b(this.mTrafficPackageType[this.mTrafficPackageTypeSelected]);
            } else {
                this.mPackageTypePreferenceOld.a(this.mTrafficPackageType[this.mTrafficPackageTypeSelected]);
            }
            if (this.mSimUserInfos[this.mSlotNum].isLeisureDataUsageEffective()) {
                this.mMonthlyPackageCategory.b((Preference) this.mLeisureAdjustUsagePreference);
            } else {
                this.mMonthlyPackageCategory.d(this.mLeisureAdjustUsagePreference);
            }
        }
    }

    private void onSelectDailyBrand(DailyCardBrandInfo dailyCardBrandInfo) {
        if (dailyCardBrandInfo != null) {
            this.mSimUserInfos[this.mSlotNum].setDailyUsedCardBrand(dailyCardBrandInfo.brandName);
            this.mDailyCardBrandPreference.b(dailyCardBrandInfo.brandName);
            this.mSimUserInfos[this.mSlotNum].saveDataUsageTotal(dailyCardBrandInfo.monthPackage);
            this.mPreNormalMonthPackage.a(FormatBytesUtil.formatBytes(this.mAppContext, dailyCardBrandInfo.monthPackage, 2));
            this.mSimUserInfos[this.mSlotNum].setDailyUsedCardPackage(dailyCardBrandInfo.dailyPackage);
            this.mDailyCardPackagePreference.a(FormatBytesUtil.formatBytes(this.mAppContext, dailyCardBrandInfo.dailyPackage, 2));
            setIgnoreApps(dailyCardBrandInfo.ignoreApps);
            AnalyticsHelper.trackDailyBrandSelect(dailyCardBrandInfo.brandName);
        }
    }

    private void onSelectNormalTrafficLimit(int i) {
        if (i >= 0) {
            this.mOverNormalLimitSelected = i;
            if (DeviceUtil.IS_MIUI12) {
                this.mNormalTrafficLimit.b(this.mOverLimitOperatorType[i]);
            } else {
                this.mNormalTrafficLimitOld.a(this.mOverLimitOperatorType[i]);
            }
            boolean z = false;
            this.mSimUserInfos[this.mSlotNum].toggleDataUsageOverLimitStopNetwork(i == 1);
            SimUserInfo simUserInfo = this.mSimUserInfos[this.mSlotNum];
            if (i == 1) {
                z = true;
            }
            simUserInfo.setDailyUsedCardStopNetworkOn(z);
            this.mTrafficLimitChanged = true;
        }
    }

    private void onSelectPackageType(int i) {
        if (this.mServiceConnected && this.mSimUserInfos[this.mSlotNum].getBrand() != i && i != -1) {
            this.mTrafficPackageTypeSelected = i;
            if (DeviceUtil.IS_MIUI12) {
                this.mPackageTypePreference.b(this.mTrafficPackageType[i]);
            } else {
                this.mPackageTypePreferenceOld.a(this.mTrafficPackageType[i]);
            }
            this.mSimUserInfos[this.mSlotNum].saveBrand(i);
            AnalyticsHelper.trackPackageSelect(i);
            updatePreference();
            if (this.mServiceConnected) {
                try {
                    this.mTrafficManageBinder.clearDataUsageIgnore(this.mSlotNum);
                } catch (RemoteException unused) {
                    Log.e(TAG, "isDataUsageIgnore RemoteException");
                }
            }
            if (i == 1) {
                setIgnoreApps(DailyCardBrandConfig.getInstance(this.mAppContext).getBrandInfo(this.mSimUserInfos[this.mSlotNum].getDailyUsedCardBrand()).ignoreApps);
            }
        }
    }

    private void registerMonitorCenter() {
        this.mMonitorCenter = AppMonitorWrapper.getInstance(this.mAppContext);
        this.mMonitorCenter.registerLisener(this.mMonitorCenterListener);
    }

    private void setIgnoreApps(List<String> list) {
        if (!this.mServiceConnected || !this.mIsAppListInited) {
            String str = TAG;
            Log.i(str, "setIgnoreApps fail:" + this.mServiceConnected + ", " + this.mIsAppListInited);
            return;
        }
        ArrayList arrayList = new ArrayList(this.mAllNetworkAccessedApps);
        ArrayList arrayList2 = new ArrayList(list);
        arrayList2.retainAll(this.mAllNetworkAccessedApps);
        arrayList.removeAll(arrayList2);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            try {
                this.mTrafficManageBinder.setDataUsageIgnore((String) it.next(), false, this.mSlotNum);
            } catch (RemoteException e) {
                Log.i(TAG, "isDataUsageIgnore", e);
            }
        }
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            try {
                this.mTrafficManageBinder.setDataUsageIgnore((String) it2.next(), true, this.mSlotNum);
            } catch (RemoteException e2) {
                Log.i(TAG, "isDataUsageIgnore", e2);
            }
        }
    }

    private void setMonthCycleDate(int i) {
        this.mMonthCycleDate.a(this.mAppContext.getResources().getString(R.string.text_cycle_day, new Object[]{String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)})}));
    }

    private void setMonthWarningPreferenceValue(float f) {
        this.mMonthWarningPreference.a(NumberFormat.getPercentInstance().format((double) f));
    }

    private void showChangePackageTypeDialog() {
        new AlertDialog.Builder(this.mActivity).setTitle(R.string.dialog_change_package_type_title).setMessage(R.string.dialog_change_package_type_summary).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PackageSettingFragment.this.mSingleChoiceItemsDialog.buildDialog(PackageSettingFragment.this.getString(R.string.traffic_setting_fragment_packege_type), PackageSettingFragment.this.mTrafficPackageType, PackageSettingFragment.this.mTrafficPackageTypeSelected, 3);
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create().show();
    }

    private void showPermanentNotificationStatusBar(Context context) {
        if (Settings.System.getInt(context.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, 0) == 0) {
            String string = context.getResources().getString(R.string.show_traffic_dialog_title);
            String string2 = context.getResources().getString(R.string.show_traffic_dialog_message);
            if (!this.mSimUserInfos[this.mSlotNum].isTotalDataUsageSetted()) {
                Activity activity = this.mActivity;
                new OptionTipDialog(activity, new TrafficOptionDialogListener(activity)).buildShowDialog(string, string2);
            }
        }
    }

    private void startCorrection() {
        try {
            if (this.mBrandChange && this.mSimUserInfos[this.mSlotNum].isCorrectionEffective()) {
                this.mTrafficManageBinder.startCorrection(false, this.mSlotNum, false, 7);
            }
        } catch (RemoteException e) {
            Log.i(TAG, "update failed onDestroy ", e);
        }
    }

    private void unRegisterMonitorCenter() {
        AppMonitorWrapper appMonitorWrapper = this.mMonitorCenter;
        if (appMonitorWrapper != null) {
            appMonitorWrapper.unRegisterLisener(this.mMonitorCenterListener);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:43:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updatePreference() {
        /*
            r2 = this;
            androidx.preference.PreferenceCategory r0 = r2.mPackageTypeCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 == 0) goto L_0x0009
            miuix.preference.DropDownPreference r1 = r2.mPackageTypePreference
            goto L_0x000b
        L_0x0009:
            miuix.preference.TextPreference r1 = r2.mPackageTypePreferenceOld
        L_0x000b:
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mPackageTypeCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 != 0) goto L_0x0017
            miuix.preference.DropDownPreference r1 = r2.mPackageTypePreference
            goto L_0x0019
        L_0x0017:
            miuix.preference.TextPreference r1 = r2.mPackageTypePreferenceOld
        L_0x0019:
            r0.d(r1)
            com.miui.networkassistant.config.SimUserInfo[] r0 = r2.mSimUserInfos
            int r1 = r2.mSlotNum
            r0 = r0[r1]
            int r0 = r0.getBrand()
            if (r0 != 0) goto L_0x008f
            androidx.preference.PreferenceScreen r0 = r2.getPreferenceScreen()
            androidx.preference.PreferenceCategory r1 = r2.mMonthlyPackageCategory
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mAdjustUsagePreference
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            r1 = 2131757478(0x7f1009a6, float:1.9145893E38)
            r0.setTitle((int) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mDailyCardPackagePreference
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.DropDownPreference r1 = r2.mDailyCardBrandPreference
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mDailyCardBrandPreferenceOld
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mNotLimitedTrafficLimit
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 == 0) goto L_0x0065
            miuix.preference.DropDownPreference r1 = r2.mNormalTrafficLimit
            goto L_0x0067
        L_0x0065:
            miuix.preference.TextPreference r1 = r2.mNormalTrafficLimitOld
        L_0x0067:
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 != 0) goto L_0x0073
            miuix.preference.DropDownPreference r1 = r2.mNormalTrafficLimit
            goto L_0x0075
        L_0x0073:
            miuix.preference.TextPreference r1 = r2.mNormalTrafficLimitOld
        L_0x0075:
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mPreNormalMonthPackage
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mSpecialPackageSetting
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMorePreferenceCategory
            miuix.preference.TextPreference r1 = r2.mMonthWarningPreference
        L_0x008a:
            r0.b((androidx.preference.Preference) r1)
            goto L_0x015a
        L_0x008f:
            r1 = 1
            if (r0 != r1) goto L_0x0103
            androidx.preference.PreferenceScreen r0 = r2.getPreferenceScreen()
            androidx.preference.PreferenceCategory r1 = r2.mMonthlyPackageCategory
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mAdjustUsagePreference
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            r1 = 2131757475(0x7f1009a3, float:1.9145887E38)
            r0.setTitle((int) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mDailyCardPackagePreference
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 == 0) goto L_0x00ba
            miuix.preference.DropDownPreference r1 = r2.mNormalTrafficLimit
            goto L_0x00bc
        L_0x00ba:
            miuix.preference.TextPreference r1 = r2.mNormalTrafficLimitOld
        L_0x00bc:
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 != 0) goto L_0x00c8
            miuix.preference.DropDownPreference r1 = r2.mNormalTrafficLimit
            goto L_0x00ca
        L_0x00c8:
            miuix.preference.TextPreference r1 = r2.mNormalTrafficLimitOld
        L_0x00ca:
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mPreNormalMonthPackage
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mSpecialPackageSetting
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mNotLimitedTrafficLimit
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMorePreferenceCategory
            miuix.preference.TextPreference r1 = r2.mMonthWarningPreference
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 == 0) goto L_0x00f2
            miuix.preference.TextPreference r1 = r2.mDailyCardBrandPreferenceOld
            goto L_0x00f4
        L_0x00f2:
            miuix.preference.DropDownPreference r1 = r2.mDailyCardBrandPreference
        L_0x00f4:
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_MIUI12
            if (r1 != 0) goto L_0x0100
            miuix.preference.TextPreference r1 = r2.mDailyCardBrandPreferenceOld
            goto L_0x008a
        L_0x0100:
            miuix.preference.DropDownPreference r1 = r2.mDailyCardBrandPreference
            goto L_0x008a
        L_0x0103:
            androidx.preference.PreferenceScreen r0 = r2.getPreferenceScreen()
            androidx.preference.PreferenceCategory r1 = r2.mMonthlyPackageCategory
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            r1 = 2131758432(0x7f100d60, float:1.9147828E38)
            r0.setTitle((int) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mDailyCardPackagePreference
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.DropDownPreference r1 = r2.mDailyCardBrandPreference
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mDailyCardBrandPreferenceOld
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mNormalTrafficLimitOld
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.DropDownPreference r1 = r2.mNormalTrafficLimit
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mPreNormalMonthPackage
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mSpecialPackageSetting
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mNotLimitedTrafficLimit
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mAdjustUsagePreference
            r0.b((androidx.preference.Preference) r1)
            androidx.preference.PreferenceCategory r0 = r2.mMorePreferenceCategory
            miuix.preference.TextPreference r1 = r2.mMonthWarningPreference
            r0.d(r1)
        L_0x015a:
            boolean r0 = com.miui.networkassistant.utils.DeviceUtil.IS_INTERNATIONAL_BUILD
            if (r0 == 0) goto L_0x0175
            androidx.preference.PreferenceScreen r0 = r2.getPreferenceScreen()
            androidx.preference.PreferenceCategory r1 = r2.mPackageTypeCategory
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mSpecialPackageSetting
            r0.d(r1)
            androidx.preference.PreferenceCategory r0 = r2.mMonthlyPackageCategory
            miuix.preference.TextPreference r1 = r2.mAdjustUsagePreference
            r0.d(r1)
        L_0x0175:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.PackageSettingFragment.updatePreference():void");
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.per_month_traffic_settings_preferences;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        this.mMonthlyPackageCategory = (PreferenceCategory) findPreference(PREF_KEY_MONTHLY_PACKAGE);
        this.mPackageTypeCategory = (PreferenceCategory) findPreference(PREF_KEY_PACKAGE_TYPE_CATEGORY);
        this.mPackageTypePreference = (DropDownPreference) findPreference(PREF_KEY_PACKAGE_TYPE);
        this.mPackageTypePreferenceOld = (TextPreference) findPreference(PREF_KEY_PACKAGE_TYPE_OLD);
        this.mOverLimitOperatorType = getResources().getStringArray(R.array.over_limit_traffic_waring_style);
        this.mSingleChoiceItemsDialog = new SingleChoiceItemsDialog(this.mActivity, this);
        this.mInputDialog = new TrafficInputDialog(this.mActivity, this);
        this.mTrafficPackageType = getResources().getStringArray(R.array.traffic_package_type);
        this.mPreNormalMonthPackage = (TextPreference) findPreference(PER_KEY_NORMAL_PACKAGE_SETTING);
        this.mPreNormalMonthPackage.setOnPreferenceClickListener(this);
        this.mSpecialPackageSetting = (TextPreference) findPreference(PREF_KEY_SPECIAL_PACKAGE_SETTING);
        this.mSpecialPackageSetting.setOnPreferenceClickListener(this);
        this.mMonthWarningPreference = (TextPreference) findPreference(PREF_MONTH_WARNING);
        this.mMonthWarningPreference.setOnPreferenceClickListener(this);
        this.mMonthCycleDate = (TextPreference) findPreference(PREF_PACKAGE_BEGIN_DATE);
        this.mMonthCycleDate.setOnPreferenceClickListener(this);
        this.mDailyCardPackagePreference = (TextPreference) findPreference(PREF_KEY_DAILY_CARD_PACKAGE);
        this.mDailyCardPackagePreference.setOnPreferenceClickListener(this);
        this.mDailyCardBrandPreference = (DropDownPreference) findPreference(PREF_KEY_DAILY_CARD_BRAND);
        this.mDailyCardBrandPreferenceOld = (TextPreference) findPreference(PREF_KEY_DAILY_CARD_BRAND_OLD);
        this.mAdjustUsagePreference = (TextPreference) findPreference(PREF_KEY_ADJUST_USAGE);
        this.mAdjustUsagePreference.setOnPreferenceClickListener(this);
        this.mLeisureAdjustUsagePreference = (TextPreference) findPreference(PREF_KEY_LEISURE_ADJUST_USAGE);
        this.mLeisureAdjustUsagePreference.setOnPreferenceClickListener(this);
        this.mAutoModifyBoxPreference = (CheckBoxPreference) findPreference(PREF_KEY_AUTO_MODIFY_PACKAGE);
        this.mAutoModifyBoxPreference.setOnPreferenceChangeListener(this);
        this.mMorePreferenceCategory = (PreferenceCategory) findPreference(PREF_MORE_SETTINGS);
        this.mNormalTrafficLimitOld = (TextPreference) findPreference(PREF_NORMAL_TRAFFIC_LIMIT_OLD);
        this.mNormalTrafficLimit = (DropDownPreference) findPreference(PREF_NORMAL_TRAFFIC_LIMIT);
        this.mNotLimitedTrafficLimit = (TextPreference) findPreference(PREF_NOT_LIMIT_TRAFFIC_LIMIT);
        this.mNotLimitedTrafficLimit.setOnPreferenceClickListener(this);
        if (DeviceUtil.IS_MIUI12) {
            this.mPackageTypePreference.setOnPreferenceChangeListener(this);
            this.mDailyCardBrandPreference.setOnPreferenceChangeListener(this);
            this.mNormalTrafficLimit.setOnPreferenceChangeListener(this);
        } else {
            this.mPackageTypePreferenceOld.setOnPreferenceClickListener(this);
            this.mDailyCardBrandPreferenceOld.setOnPreferenceClickListener(this);
            this.mNormalTrafficLimitOld.setOnPreferenceClickListener(this);
        }
        registerMonitorCenter();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            this.mChanged = true;
        }
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        this.mActionBarTipButton = new Button(this.mActivity);
        int i = miui.R.drawable.icon_info_light;
        this.mActionBarTipButton.setContentDescription(this.mAppContext.getString(R.string.tips_dialog_title));
        this.mActionBarTipButton.setBackgroundResource(i);
        this.mActionBarTipButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PackageSettingFragment.this.mServiceConnected) {
                    new MessageDialog(PackageSettingFragment.this.mActivity).buildShowDialog(PackageSettingFragment.this.mActivity.getString(R.string.tips_dialog_title), TextPrepareUtil.getOperatorTips(PackageSettingFragment.this.mActivity, PackageSettingFragment.this.mSimUserInfos[PackageSettingFragment.this.mSlotNum].getImsi(), PackageSettingFragment.this.mSlotNum));
                }
            }
        });
        if (!(actionBar instanceof miui.app.ActionBar)) {
            return 0;
        }
        ((miui.app.ActionBar) actionBar).setEndView(this.mActionBarTipButton);
        return 0;
    }

    public void onDateUpdated(int i) {
        setMonthCycleDate(i);
        this.mSimUserInfos[this.mSlotNum].saveMonthStart(i);
        this.mChanged = true;
    }

    public void onDestroy() {
        super.onDestroy();
        startCorrection();
        unRegisterMonitorCenter();
    }

    public void onPause() {
        super.onPause();
        try {
            if (this.mChanged && this.mServiceConnected) {
                this.mTrafficManageBinder.updateTrafficStatusMonitor(this.mSlotNum);
                if (this.mSimUserInfos[this.mSlotNum].isDataUsageAutoCorrectionEffective()) {
                    this.mTrafficManageBinder.toggleDataUsageAutoCorrection(true, this.mSlotNum);
                }
                this.mSimUserInfos[this.mSlotNum].setTrafficTcResultCode(0);
                this.mSimUserInfos[this.mSlotNum].setBillTcResultCode(0);
                this.mSimUserInfos[this.mSlotNum].setTrafficSmsDetail("");
                this.mSimUserInfos[this.mSlotNum].setBillSmsDetail("");
            }
            if (this.mServiceConnected && this.mTrafficLimitChanged) {
                this.mTrafficManageBinder.updateTrafficStatusMonitor(this.mSlotNum);
            }
            this.mChanged = false;
        } catch (RemoteException e) {
            Log.i(TAG, "update failed onDestroy ", e);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mAutoModifyBoxPreference) {
            this.mSimUserInfos[this.mSlotNum].saveTrafficCorrectionAutoModify(((Boolean) obj).booleanValue());
            return true;
        } else if (preference == this.mDailyCardBrandPreference) {
            for (DailyCardBrandInfo next : DailyCardBrandConfig.getInstance(this.mAppContext).getBrandList()) {
                if (TextUtils.equals(next.brandName, String.valueOf(obj))) {
                    onSelectDailyBrand(next);
                }
            }
            return true;
        } else {
            DropDownPreference dropDownPreference = this.mPackageTypePreference;
            if (preference == dropDownPreference) {
                onSelectPackageType(CollectionUtils.getArrayIndex(dropDownPreference.b(), String.valueOf(obj)));
                return true;
            }
            DropDownPreference dropDownPreference2 = this.mNormalTrafficLimit;
            if (preference != dropDownPreference2) {
                return true;
            }
            onSelectNormalTrafficLimit(CollectionUtils.getArrayIndex(dropDownPreference2.b(), String.valueOf(obj)));
            return true;
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        TrafficInputDialog trafficInputDialog;
        String string;
        String string2;
        int i;
        if (preference == this.mPreNormalMonthPackage) {
            this.mInputDialog.buildInputDialog(this.mActivity.getString(R.string.daily_pkg_traffic), this.mActivity.getString(R.string.daily_pkg_traffic), 1);
        } else {
            if (preference == this.mSpecialPackageSetting) {
                g.startWithFragment(this.mActivity, SpecialTrafficSettingsFragment.class);
            } else if (preference == this.mMonthWarningPreference) {
                SeekBarDialog seekBarDialog = new SeekBarDialog(this.mActivity, this);
                seekBarDialog.buildDateDialog(this.mAppContext.getString(R.string.pref_warning_values));
                try {
                    long currentMonthTotalPackage = this.mTrafficManageBinder.getCurrentMonthTotalPackage(this.mSlotNum);
                    float dataUsageWarning = this.mSimUserInfos[this.mSlotNum].getDataUsageWarning();
                    if (currentMonthTotalPackage < 0) {
                        currentMonthTotalPackage = 0;
                    }
                    seekBarDialog.setData(currentMonthTotalPackage, dataUsageWarning);
                } catch (RemoteException e) {
                    Log.i(TAG, "get current package", e);
                }
            } else if (preference == this.mMonthCycleDate) {
                new DateShowDialog(this.mActivity, this).buildDateDialog(this.mActivity.getString(R.string.begin_date), this.mSimUserInfos[this.mSlotNum].getMonthStart());
            } else if (preference == this.mPackageTypePreferenceOld) {
                showChangePackageTypeDialog();
            } else if (preference == this.mNormalTrafficLimitOld) {
                this.mSingleChoiceItemsDialog.buildDialog(getString(R.string.pref_title_over_traffic_limit_warning), this.mOverLimitOperatorType, this.mOverNormalLimitSelected, 2);
            } else {
                int i2 = 0;
                if (preference == this.mDailyCardBrandPreferenceOld) {
                    String string3 = this.mAppContext.getString(R.string.pref_traffic_daily_package_brand);
                    List<String> brandNameList = DailyCardBrandConfig.getInstance(this.mAppContext).getBrandNameList();
                    int indexOf = brandNameList.indexOf(this.mSimUserInfos[this.mSlotNum].getDailyUsedCardBrand());
                    String[] strArr = (String[]) brandNameList.toArray(new String[brandNameList.size()]);
                    SingleChoiceItemsDialog singleChoiceItemsDialog = this.mSingleChoiceItemsDialog;
                    if (indexOf >= 0) {
                        i2 = indexOf;
                    }
                    singleChoiceItemsDialog.buildDialog(string3, strArr, i2, 7);
                } else {
                    if (preference == this.mDailyCardPackagePreference) {
                        trafficInputDialog = this.mInputDialog;
                        string = this.mActivity.getString(R.string.pref_traffic_daily_package);
                        string2 = this.mActivity.getString(R.string.pref_traffic_daily_package);
                        i = 4;
                    } else if (preference == this.mAdjustUsagePreference) {
                        trafficInputDialog = this.mInputDialog;
                        string = this.mActivity.getString(R.string.manual_input_traffic);
                        string2 = this.mActivity.getString(R.string.input_used_hint);
                        i = 5;
                    } else if (preference == this.mLeisureAdjustUsagePreference) {
                        long leisureDataUsageTotal = this.mSimUserInfos[this.mSlotNum].getLeisureDataUsageTotal();
                        this.mInputDialog.buildInputDialog(getString(R.string.manual_input_free_traffic), String.format(!DeviceUtil.isLargeScaleMode() ? getString(R.string.input_used_max_hint) : "", new Object[]{FormatBytesUtil.formatBytes(this.mAppContext, leisureDataUsageTotal, 0)}), 6);
                        this.mInputDialog.setMaxValue(leisureDataUsageTotal);
                    } else if (preference == this.mNotLimitedTrafficLimit) {
                        trafficInputDialog = this.mInputDialog;
                        string = this.mActivity.getString(R.string.traffic_usage_warning_title);
                        string2 = this.mActivity.getString(R.string.input_aviable_traffic);
                        i = 8;
                    }
                    trafficInputDialog.buildInputDialog(string, string2, i);
                }
            }
            return true;
        }
        this.mInputDialog.clearInputText();
        return true;
    }

    public void onResume() {
        super.onResume();
        initData();
    }

    public void onSeekBarChanged(float f) {
        setMonthWarningPreferenceValue(f);
        this.mSimUserInfos[this.mSlotNum].saveDataUsageWarning(f);
        this.mChanged = true;
    }

    public void onSelectItemUpdate(int i, int i2) {
        if (i2 == 2) {
            this.mOverNormalLimitSelected = i;
            onSelectNormalTrafficLimit(i);
        } else if (i2 == 3) {
            onSelectPackageType(i);
        } else if (i2 == 7) {
            onSelectDailyBrand(DailyCardBrandConfig.getInstance(this.mAppContext).getBrandList().get(i));
        }
        this.mBrandChange = true;
        this.mChanged = true;
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.per_month_pkg_traffic_settings;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        if (this.mServiceConnected) {
            this.mHandler.sendEmptyMessage(1);
        }
    }

    public void onTrafficUpdated(long j, int i) {
        TextPreference textPreference;
        String str;
        String str2;
        if (i != 1) {
            if (i == 8) {
                this.mSimUserInfos[this.mSlotNum].setNotLimitedCardPackage(j);
                textPreference = this.mNotLimitedTrafficLimit;
            } else if (i == 4) {
                this.mSimUserInfos[this.mSlotNum].setDailyUsedCardPackage(j);
                textPreference = this.mDailyCardPackagePreference;
            } else if (i != 5) {
                if (i == 6 && this.mServiceConnected) {
                    try {
                        this.mTrafficManageBinder.manualCorrectLeisureDataUsage(j, this.mSlotNum);
                    } catch (RemoteException e) {
                        e = e;
                        str2 = TAG;
                        str = "manual leisure traffic";
                    }
                }
            } else if (this.mServiceConnected) {
                try {
                    this.mTrafficManageBinder.manualCorrectNormalDataUsage(j, this.mSlotNum);
                    this.mTrafficManageBinder.updateGlobleDataUsage(this.mSlotNum);
                } catch (RemoteException e2) {
                    e = e2;
                    str2 = TAG;
                    str = "manual normal traffic";
                }
            }
            textPreference.a(FormatBytesUtil.formatBytes(this.mAppContext, j, 2));
        } else {
            showPermanentNotificationStatusBar(this.mActivity);
            this.mPreNormalMonthPackage.a(FormatBytesUtil.formatBytes(this.mAppContext, j, 2));
            this.mSimUserInfos[this.mSlotNum].saveDataUsageTotal(j);
            try {
                this.mTrafficManageBinder.updateGlobleDataUsage(this.mSlotNum);
            } catch (RemoteException e3) {
                e3.printStackTrace();
            }
            SimUserInfo[] simUserInfoArr = this.mSimUserInfos;
            int i2 = this.mSlotNum;
            simUserInfoArr[i2].saveMonthStart(simUserInfoArr[i2].getMonthStart());
            this.mSimUserInfos[this.mSlotNum].saveTrafficCorrectionAutoModify(false);
            NotificationUtil.cancelDataUsageOverLimit(this.mActivity);
            NotificationUtil.cancelNormalTotalPackageNotSetted(this.mActivity);
        }
        this.mChanged = true;
        Log.i(str2, str, e);
        this.mChanged = true;
    }
}
