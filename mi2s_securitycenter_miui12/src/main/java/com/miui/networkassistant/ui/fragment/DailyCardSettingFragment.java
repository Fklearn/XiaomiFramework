package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.c.b.g;
import com.miui.networkassistant.config.DailyCardBrandConfig;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.DailyCardBrandInfo;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.ui.base.TrafficRelatedPreFragment;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.ui.dialog.TrafficInputDialog;
import com.miui.networkassistant.utils.CollectionUtils;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.TrafficUpdateUtil;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class DailyCardSettingFragment extends TrafficRelatedPreFragment implements TrafficInputDialog.TrafficInputDialogListener, Preference.c, View.OnClickListener, SingleChoiceItemsDialog.SingleChoiceItemsDialogListener, Preference.b {
    private static final int ACTION_FLAG_DAILY_BRAND = 1;
    private static final int ACTION_FLAG_DAILY_PACKAGE = 3;
    private static final int ACTION_FLAG_MONTH_PACKAGE = 2;
    private static final String CATEGORY_KEY_BRAND = "category_key_brand";
    private static final String PREF_KEY_BRAND = "pref_key_brand";
    private static final String PREF_KEY_BRAND_OLD = "pref_key_brand_old";
    private static final String PREF_KEY_DAILY_PACKAGE = "pref_key_daily_package";
    private static final String PREF_KEY_IGNORE_APPS = "pref_key_ignore_apps";
    private static final String PREF_KEY_MONTH_PACKAGE = "pref_key_month_package";
    /* access modifiers changed from: private */
    public static final String TAG = "DailyCardSettingFragment";
    /* access modifiers changed from: private */
    public List<String> mAllNetworkAccessedApps;
    private String mDailyBrand;
    private DropDownPreference mDailyBrandPreference;
    private TextPreference mDailyBrandPreferenceOld;
    private long mDailyPackage = -1;
    private TextPreference mDailyPackagePreference;
    /* access modifiers changed from: private */
    public List<String> mIgnoreApps;
    private TextPreference mIgnoreAppsPreference;
    /* access modifiers changed from: private */
    public boolean mIsAppListInited = false;
    private SingleChoiceItemsDialog mItemsDialog;
    /* access modifiers changed from: private */
    public AppMonitorWrapper mMonitorCenter;
    private AppMonitorWrapper.AppMonitorListener mMonitorCenterListener = new AppMonitorWrapper.AppMonitorListener() {
        public void onAppListUpdated() {
            Log.i(DailyCardSettingFragment.TAG, "onAppListUpdated");
            ArrayList<AppInfo> networkAccessedAppList = DailyCardSettingFragment.this.mMonitorCenter.getNetworkAccessedAppList();
            if (networkAccessedAppList != null) {
                List unused = DailyCardSettingFragment.this.mAllNetworkAccessedApps = new ArrayList();
                List unused2 = DailyCardSettingFragment.this.mIgnoreApps = new ArrayList();
                for (AppInfo appInfo : networkAccessedAppList) {
                    String charSequence = appInfo.packageName.toString();
                    DailyCardSettingFragment.this.mAllNetworkAccessedApps.add(charSequence);
                    try {
                        if (DailyCardSettingFragment.this.mServiceConnected && DailyCardSettingFragment.this.mTrafficManageBinder.isDataUsageIgnore(charSequence, DailyCardSettingFragment.this.mSlotNum)) {
                            DailyCardSettingFragment.this.mIgnoreApps.add(charSequence);
                        }
                    } catch (RemoteException e) {
                        Log.i(DailyCardSettingFragment.TAG, "isDataUsageIgnore", e);
                    }
                }
                boolean unused3 = DailyCardSettingFragment.this.mIsAppListInited = true;
            }
        }
    };
    private long mMonthPackage = -1;
    private TextPreference mMonthPackagePreference;
    private Button mNextButton;

    private void addSaveButton() {
        try {
            View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.na_item_next_button, (ViewGroup) null);
            this.mNextButton = (Button) inflate.findViewById(R.id.btNext);
            this.mNextButton.setEnabled(this.mDailyPackage != -1);
            this.mNextButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    DailyCardSettingFragment.this.finish();
                }
            });
            ((ViewGroup) getView()).addView(inflate);
        } catch (Exception unused) {
            Log.e(TAG, "addSaveButton Exception");
        }
    }

    private void onSelectDailyCardBrand(int i) {
        if (i >= 0) {
            DailyCardBrandInfo dailyCardBrandInfo = DailyCardBrandConfig.getInstance(this.mAppContext).getBrandList().get(i);
            this.mDailyBrand = dailyCardBrandInfo.brandName;
            setDailyBrandText(this.mDailyBrand);
            this.mMonthPackage = dailyCardBrandInfo.monthPackage;
            this.mMonthPackagePreference.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mMonthPackage, 2));
            this.mDailyPackage = dailyCardBrandInfo.dailyPackage;
            this.mDailyPackagePreference.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mDailyPackage, 2));
            setIgnoreApps(dailyCardBrandInfo.ignoreApps);
            Button button = this.mNextButton;
            if (button != null) {
                button.setEnabled(this.mDailyPackage != -1);
            }
        }
    }

    private void registerMonitorCenter() {
        this.mMonitorCenter = AppMonitorWrapper.getInstance(this.mAppContext);
        this.mMonitorCenter.registerLisener(this.mMonitorCenterListener);
    }

    private void setDailyBrandText(String str) {
        if (DeviceUtil.IS_MIUI12) {
            this.mDailyBrandPreference.b(str);
        } else {
            this.mDailyBrandPreferenceOld.a(str);
        }
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

    private void startCorrection() {
        if (this.mServiceConnected) {
            try {
                boolean isDailyUsedCardEnable = this.mSimUserInfos[this.mSlotNum].isDailyUsedCardEnable();
                int i = isDailyUsedCardEnable ? 2 : 7;
                if (this.mSimUserInfos[this.mSlotNum].isDataUsageAutoCorrectionEffective()) {
                    this.mTrafficManageBinder.toggleDataUsageAutoCorrection(!isDailyUsedCardEnable, this.mSlotNum);
                }
                this.mTrafficManageBinder.startCorrection(false, this.mSlotNum, true, i);
            } catch (RemoteException e) {
                Log.i(TAG, "stat Correction exception", e);
            }
        }
    }

    private void unRegisterMonitorCenter() {
        AppMonitorWrapper appMonitorWrapper = this.mMonitorCenter;
        if (appMonitorWrapper != null) {
            appMonitorWrapper.unRegisterLisener(this.mMonitorCenterListener);
        }
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.daily_card_setting_preference;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        String string = this.mAppContext.getString(R.string.traffic_setting_fragment_default);
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(CATEGORY_KEY_BRAND);
        this.mDailyBrandPreferenceOld = (TextPreference) findPreference(PREF_KEY_BRAND_OLD);
        this.mDailyBrandPreference = (DropDownPreference) findPreference(PREF_KEY_BRAND);
        this.mDailyPackagePreference = (TextPreference) findPreference(PREF_KEY_DAILY_PACKAGE);
        this.mMonthPackagePreference = (TextPreference) findPreference(PREF_KEY_MONTH_PACKAGE);
        this.mIgnoreAppsPreference = (TextPreference) findPreference(PREF_KEY_IGNORE_APPS);
        preferenceCategory.d(DeviceUtil.IS_MIUI12 ? this.mDailyBrandPreferenceOld : this.mDailyBrandPreference);
        addSaveButton();
        if (DeviceUtil.IS_MIUI12) {
            this.mDailyBrandPreference.setOnPreferenceChangeListener(this);
        } else {
            this.mDailyBrandPreferenceOld.setOnPreferenceClickListener(this);
        }
        this.mDailyPackagePreference.setOnPreferenceClickListener(this);
        this.mMonthPackagePreference.setOnPreferenceClickListener(this);
        this.mIgnoreAppsPreference.setOnPreferenceClickListener(this);
        this.mDailyBrand = DailyCardBrandConfig.getInstance(this.mAppContext).getBrandList().get(0).brandName;
        setDailyBrandText(this.mDailyBrand);
        this.mDailyPackagePreference.a(string);
        this.mMonthPackagePreference.a(string);
        this.mItemsDialog = new SingleChoiceItemsDialog(this.mActivity, this);
        registerMonitorCenter();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        startCorrection();
        if (this.mSimUserInfos[this.mSlotNum].isNATipsEnable()) {
            TrafficUpdateUtil.broadCastTrafficUpdated(this.mAppContext);
        }
        unRegisterMonitorCenter();
    }

    public void onPause() {
        this.mSimUserInfos[this.mSlotNum].setDailyUsedCardBrand(this.mDailyBrand);
        this.mSimUserInfos[this.mSlotNum].saveDataUsageTotal(this.mMonthPackage);
        this.mSimUserInfos[this.mSlotNum].setDailyUsedCardPackage(this.mDailyPackage);
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        DropDownPreference dropDownPreference = this.mDailyBrandPreference;
        if (preference != dropDownPreference) {
            return false;
        }
        onSelectDailyCardBrand(CollectionUtils.getArrayIndex(dropDownPreference.b(), String.valueOf(obj)));
        return false;
    }

    public boolean onPreferenceClick(Preference preference) {
        TrafficInputDialog trafficInputDialog;
        String string;
        String string2;
        int i;
        if (preference == this.mDailyBrandPreferenceOld) {
            String string3 = this.mAppContext.getString(R.string.daily_card_setting_fragment_daily_card_brand);
            List<String> brandNameList = DailyCardBrandConfig.getInstance(this.mAppContext).getBrandNameList();
            String[] strArr = (String[]) brandNameList.toArray(new String[brandNameList.size()]);
            int indexOf = brandNameList.indexOf(this.mDailyBrand);
            SingleChoiceItemsDialog singleChoiceItemsDialog = this.mItemsDialog;
            if (indexOf < 0) {
                indexOf = 0;
            }
            singleChoiceItemsDialog.buildDialog(string3, strArr, indexOf, 1);
        } else {
            if (preference == this.mDailyPackagePreference) {
                trafficInputDialog = new TrafficInputDialog(this.mActivity, this);
                string = this.mActivity.getString(R.string.daily_card_setting_fragment_daily_package);
                string2 = this.mActivity.getString(R.string.input_aviable_traffic);
                i = 3;
            } else if (preference == this.mMonthPackagePreference) {
                trafficInputDialog = new TrafficInputDialog(this.mActivity, this);
                string = this.mActivity.getString(R.string.daily_card_setting_fragment_month_package);
                string2 = this.mActivity.getString(R.string.input_aviable_traffic);
                i = 2;
            } else if (preference == this.mIgnoreAppsPreference) {
                g.startWithFragment(this.mActivity, DataUsageIgnoreAppListFragment.class);
            }
            trafficInputDialog.buildInputDialog(string, string2, i);
            trafficInputDialog.clearInputText();
        }
        return true;
    }

    public void onSelectItemUpdate(int i, int i2) {
        if (i2 == 1) {
            onSelectDailyCardBrand(i);
        }
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.traffic_setting_fragment_title;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        Log.i(TAG, "onTrafficManageServiceConnected");
        if (this.mIsAppListInited) {
            for (String next : this.mAllNetworkAccessedApps) {
                try {
                    if (this.mTrafficManageBinder.isDataUsageIgnore(next, this.mSlotNum)) {
                        this.mIgnoreApps.add(next);
                    }
                } catch (RemoteException e) {
                    Log.i(TAG, "isDataUsageIgnore", e);
                }
            }
        }
    }

    public void onTrafficUpdated(long j, int i) {
        if (i == 2) {
            this.mMonthPackage = j;
            this.mMonthPackagePreference.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mMonthPackage, 2));
        } else if (i == 3) {
            this.mDailyPackage = j;
            this.mDailyPackagePreference.a(FormatBytesUtil.formatBytes(this.mAppContext, this.mDailyPackage, 2));
            Button button = this.mNextButton;
            if (button != null) {
                button.setEnabled(this.mDailyPackage != -1);
            }
        }
    }
}
