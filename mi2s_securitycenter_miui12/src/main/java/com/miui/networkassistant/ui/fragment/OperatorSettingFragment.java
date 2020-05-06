package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.c.a.b;
import b.b.c.c.b.g;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.ui.NetworkAssistantActivity;
import com.miui.networkassistant.ui.base.TrafficRelatedPreFragment;
import com.miui.networkassistant.ui.dialog.CommonDialog;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.PrivacyDeclareAndAllowNetworkUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.TrafficUpdateUtil;
import com.miui.networkassistant.webapi.DataUsageResult;
import com.miui.networkassistant.webapi.WebApiAccessHelper;
import com.miui.securitycenter.R;
import java.util.Map;
import java.util.TreeMap;
import miuix.preference.DropDownPreference;
import miuix.preference.RadioButtonPreference;
import miuix.preference.TextPreference;

public class OperatorSettingFragment extends TrafficRelatedPreFragment implements Preference.b, Preference.c, SingleChoiceItemsDialog.SingleChoiceItemsDialogListener {
    private static final int ACTION_FLAG_CITY = 2;
    private static final int ACTION_FLAG_OPERATOR = 3;
    private static final int ACTION_FLAG_PROVINCE = 1;
    public static final String BUNDLE_KEY_FROM_NOTIFICATION = "bundle_key_from_other_task";
    public static final String BUNDLE_KEY_NEED_BACK = "key_back";
    public static final String BUNDLE_KEY_TRAFFIC_GUIDE = "traffic_guide";
    private static final String CATEGORY_KEY_ADJUST_TRAFFIC = "category_key_adjust_traffic";
    private static final String CATEGORY_KEY_OPERATOR_SETTING = "category_key_operator_setting";
    private static final String CATEGORY_KEY_PACKAGE_TYPE = "category_key_package_type";
    private static final String CATEGORY_KEY_TIP_TEXT = "category_key_tip_text";
    private static final String PREF_ADJUST_DATA_USAGE = "pref_adjust_data_usage";
    private static final String PREF_AUTO_ADJUST_TRAFFIC = "pref_auto_adjust_traffic";
    private static final String PREF_DAILY_CARD = "pref_daily_card";
    private static final String PREF_KEY_CITY = "pref_key_city";
    private static final String PREF_KEY_CITY_OLD = "pref_key_city_old";
    private static final String PREF_KEY_OPERATOR = "pref_key_operator";
    private static final String PREF_KEY_OPERATOR_OLD = "pref_key_operator_old";
    private static final String PREF_KEY_PROVINCE = "pref_key_province";
    private static final String PREF_KEY_PROVINCE_OLD = "pref_key_province_old";
    private static final String PREF_NORMAL_CARD = "pref_normal_card";
    private static final String PREF_NO_LIMIT_CARD = "pref_no_limit_card";
    private static final String PREF_TC_SMS_REPORT = "pref_tc_sms_report";
    /* access modifiers changed from: private */
    public static final String TAG = "OperatorSettingFragment";
    public static final String UPDATE_OPERATOR_FROM_NOTIFICATION = "update_operator";
    private TextPreference mAdjustDataPreference;
    private PreferenceCategory mAdjustTrafficCategory;
    private CheckBoxPreference mAutoAdjustPreference;
    private boolean mAutoCorrectionEnable;
    /* access modifiers changed from: private */
    public int mBrand = -1;
    /* access modifiers changed from: private */
    public int mCity = -1;
    /* access modifiers changed from: private */
    public int mCityCode = -1;
    private Map<Integer, String> mCityMap;
    private DropDownPreference mCityPreference;
    private TextPreference mCityPreferenceOld;
    private CommonDialog mCommonDialog;
    private RadioButtonPreference mDailyCardPreference;
    private boolean mIsTrafficGuide;
    private SingleChoiceItemsDialog mItemsDialog;
    private Button mNextButton;
    private RadioButtonPreference mNormalPreference;
    private RadioButtonPreference mNotLimitPreference;
    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                OperatorSettingFragment operatorSettingFragment = OperatorSettingFragment.this;
                int unused = operatorSettingFragment.mProvince = operatorSettingFragment.mProvinceCode;
                OperatorSettingFragment operatorSettingFragment2 = OperatorSettingFragment.this;
                int unused2 = operatorSettingFragment2.mCity = operatorSettingFragment2.mCityCode;
                OperatorSettingFragment operatorSettingFragment3 = OperatorSettingFragment.this;
                operatorSettingFragment3.selectProvince(operatorSettingFragment3.mProvince);
            } else if (i == -2) {
                OperatorSettingFragment.this.mSimUserInfos[OperatorSettingFragment.this.mSlotNum].setSimLocationAlertIgnore(true);
            }
        }
    };
    /* access modifiers changed from: private */
    public String mOperator = "";
    private Map<String, String> mOperatorMap;
    private DropDownPreference mOperatorPreference;
    private TextPreference mOperatorPreferenceOld;
    private PreferenceCategory mPackageTypeCategory;
    private Preference mPackageTypePreference;
    /* access modifiers changed from: private */
    public int mProvince = -1;
    /* access modifiers changed from: private */
    public int mProvinceCode = -1;
    private Map<Integer, String> mProvinceMap;
    private DropDownPreference mProvincePreference;
    private TextPreference mProvincePreferenceOld;
    private TextPreference mReportPreference;
    private PreferenceCategory mTipTextCategory;
    private GetVirtualOperatorTask mVirtualOperatorTask;

    private class GetVirtualOperatorTask extends AsyncTask<Void, Void, DataUsageResult> {
        private Context mContext;
        private String mPhoneNumber;
        private SimUserInfo mSimUserInfo;

        public GetVirtualOperatorTask(Context context, SimUserInfo simUserInfo) {
            this.mContext = context.getApplicationContext();
            this.mSimUserInfo = simUserInfo;
        }

        /* access modifiers changed from: protected */
        public DataUsageResult doInBackground(Void... voidArr) {
            this.mPhoneNumber = TelephonyUtil.getPhoneNumber(this.mContext, this.mSimUserInfo.getSlotNum());
            if (!PrivacyDeclareAndAllowNetworkUtil.isAllowNetwork()) {
                return null;
            }
            long j = 0;
            try {
                if (OperatorSettingFragment.this.mTrafficManageBinder != null) {
                    j = OperatorSettingFragment.this.mTrafficManageBinder.getCorrectedNormalMonthDataUsageUsed(OperatorSettingFragment.this.mSlotNum);
                }
            } catch (RemoteException e) {
                Log.i(OperatorSettingFragment.TAG, "get month used exception", e);
            }
            return WebApiAccessHelper.queryDataUsage(this.mSimUserInfo.getImsi(), String.valueOf(this.mSimUserInfo.getCity()), this.mPhoneNumber, this.mSimUserInfo.getOperator(), j, this.mSimUserInfo.getIccid());
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(DataUsageResult dataUsageResult) {
            super.onPostExecute(dataUsageResult);
            if (dataUsageResult != null && dataUsageResult.isSuccess()) {
                Log.i(OperatorSettingFragment.TAG, dataUsageResult.toString());
                String unused = OperatorSettingFragment.this.mOperator = dataUsageResult.getOperator();
            } else if (dataUsageResult != null) {
                String access$1700 = OperatorSettingFragment.TAG;
                Log.i(access$1700, "failed result : " + dataUsageResult.toString());
            }
            OperatorSettingFragment.this.initOperator(this.mPhoneNumber);
        }
    }

    private void addSaveButton() {
        try {
            if (this.mIsTrafficGuide) {
                View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.na_item_next_button, (ViewGroup) null);
                this.mNextButton = (Button) inflate.findViewById(R.id.btNext);
                this.mNextButton.setEnabled((this.mBrand == -1 || this.mCity == -1 || TextUtils.isEmpty(this.mOperator)) ? false : true);
                this.mNextButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (OperatorSettingFragment.this.mBrand == 1) {
                            g.startWithFragment(OperatorSettingFragment.this.mActivity, DailyCardSettingFragment.class, OperatorSettingFragment.this.getArguments());
                        }
                        OperatorSettingFragment.this.finish();
                    }
                });
                ((ViewGroup) getView()).addView(inflate);
            }
        } catch (Exception unused) {
            Log.e(TAG, "addSaveButton Exception");
        }
    }

    private <T> Map<T, String> addTipsTitleToMap(T t, Map<T, String> map) {
        TreeMap treeMap = new TreeMap();
        treeMap.put(t, "请选择");
        if (map != null) {
            treeMap.putAll(map);
        }
        return treeMap;
    }

    private void buildLocationAlertDialog(String str, String str2) {
        this.mCommonDialog = new CommonDialog(this.mActivity, this.mOnClickListener);
        this.mCommonDialog.setNagetiveText(this.mAppContext.getString(R.string.traffic_setting_fragment_loacation_alert_nomore));
        this.mCommonDialog.setTitle(str);
        this.mCommonDialog.setMessage(str2);
        this.mCommonDialog.show();
    }

    private void cancelVirtualOperatorTask() {
        GetVirtualOperatorTask getVirtualOperatorTask = this.mVirtualOperatorTask;
        if (getVirtualOperatorTask != null && getVirtualOperatorTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mVirtualOperatorTask.cancel(true);
        }
    }

    private void exeGetVirtualOperatorTask() {
        this.mVirtualOperatorTask = new GetVirtualOperatorTask(this.mAppContext, this.mSimUserInfos[this.mSlotNum]);
        this.mVirtualOperatorTask.execute(new Void[0]);
    }

    private Map<Integer, String> getCityMapByProvinceId(int i) {
        Map map;
        if (i > 0) {
            try {
                map = this.mTrafficCornBinders[this.mSlotNum].getCities(i);
            } catch (RemoteException e) {
                Log.i(TAG, "getCityMapByProvinceId", e);
            }
            return addTipsTitleToMap(-1, map);
        }
        map = null;
        return addTipsTitleToMap(-1, map);
    }

    private void getOperatorMap() {
        try {
            this.mOperatorMap = this.mTrafficCornBinders[this.mSlotNum].getOperators();
        } catch (RemoteException e) {
            Log.i(TAG, "get operator map failed", e);
        }
        this.mOperatorMap = addTipsTitleToMap("", this.mOperatorMap);
    }

    private <T> int getPosByTag(T t, Map<T, String> map) {
        if (t == null || map == null) {
            return -1;
        }
        int i = 0;
        for (T equals : map.keySet()) {
            if (equals.equals(t)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    private void getProvinceMap() {
        try {
            this.mProvinceMap = this.mTrafficCornBinders[this.mSlotNum].getProvinces();
        } catch (RemoteException e) {
            Log.i(TAG, "get province map failed", e);
        }
        this.mProvinceMap = addTipsTitleToMap(-1, this.mProvinceMap);
    }

    private String getSimCardLocation(int i, int i2) {
        String str = this.mProvinceMap.get(Integer.valueOf(i));
        String str2 = TAG;
        Log.i(str2, "location : " + i);
        Map<Integer, String> cityMapByProvinceId = getCityMapByProvinceId(i);
        if (cityMapByProvinceId == null) {
            return "";
        }
        String str3 = cityMapByProvinceId.get(Integer.valueOf(i2));
        if (TextUtils.equals(str, str3)) {
            return str;
        }
        return String.format("%s%s", new Object[]{str, str3});
    }

    private void initCardStuff() {
        Bundle arguments = getArguments();
        this.mSlotNum = (arguments == null || !arguments.containsKey(Sim.SIM_SLOT_NUM_TAG)) ? Sim.getCurrentOptSlotNum() : arguments.getInt(Sim.SIM_SLOT_NUM_TAG, 0);
        Sim.operateOnSlotNum(this.mSlotNum);
        NotificationUtil.cancelNormalTotalPackageNotSetted(this.mActivity);
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mServiceConnected) {
            initCardStuff();
            if (this.mSimUserInfos[this.mSlotNum].isOversea()) {
                finish();
                return;
            }
            initProvinceAndOperatorData();
            String phoneNumber = this.mSimUserInfos[this.mSlotNum].getPhoneNumber();
            if (TextUtils.isEmpty(phoneNumber)) {
                TelephonyUtil.getPhoneNumber(this.mAppContext, this.mSlotNum, new Handler(Looper.getMainLooper()), new TelephonyUtil.PhoneNumberLoadedListener() {
                    public void onPhoneNumberLoaded(String str) {
                        OperatorSettingFragment.this.mSimUserInfos[OperatorSettingFragment.this.mSlotNum].setPhoneNumber(str);
                        OperatorSettingFragment.this.initSimLocation(str);
                    }
                });
            } else {
                initSimLocation(phoneNumber);
            }
            this.mSimUserInfos[this.mSlotNum].setDailyCardSettingGuideEnable(false);
            SimUserInfo simUserInfo = this.mSimUserInfos[1 - this.mSlotNum];
            if (simUserInfo != null) {
                simUserInfo.setDailyCardSettingGuideEnable(false);
            }
            getPreferenceScreen().d(this.mIsTrafficGuide ? this.mAdjustTrafficCategory : this.mPackageTypeCategory);
            if (!this.mIsTrafficGuide) {
                getPreferenceScreen().d(this.mTipTextCategory);
            }
        }
    }

    /* access modifiers changed from: private */
    public void initOperator(String str) {
        if (TextUtils.isEmpty(this.mOperator)) {
            this.mOperator = TelephonyUtil.getOperatorStr(this.mSimUserInfos[this.mSlotNum].getImsi(), str, this.mSimUserInfos[this.mSlotNum].getSlotNum());
        }
        if (!TextUtils.isEmpty(this.mOperator)) {
            onOperatorSelected(this.mOperator);
        }
    }

    private void initProvinceAndOperatorData() {
        getProvinceMap();
        getOperatorMap();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:44:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initSimLocation(java.lang.String r5) {
        /*
            r4 = this;
            com.miui.networkassistant.config.SimUserInfo[] r0 = r4.mSimUserInfos
            int r1 = r4.mSlotNum
            r0 = r0[r1]
            int r0 = r0.getProvince()
            r4.mProvince = r0
            com.miui.networkassistant.config.SimUserInfo[] r0 = r4.mSimUserInfos
            int r1 = r4.mSlotNum
            r0 = r0[r1]
            int r0 = r0.getCity()
            r4.mCity = r0
            com.miui.networkassistant.config.SimUserInfo[] r0 = r4.mSimUserInfos
            int r1 = r4.mSlotNum
            r0 = r0[r1]
            java.lang.String r0 = r0.getOperator()
            r4.mOperator = r0
            com.miui.networkassistant.config.SimUserInfo[] r0 = r4.mSimUserInfos
            int r1 = r4.mSlotNum
            r0 = r0[r1]
            int r0 = r0.getBrand()
            r4.mBrand = r0
            int r0 = r4.mBrand
            r1 = -1
            if (r0 != r1) goto L_0x0038
            r0 = 0
            r4.mBrand = r0
        L_0x0038:
            r4.refreshCorrectionView()
            boolean r0 = android.text.TextUtils.isEmpty(r5)
            if (r0 != 0) goto L_0x0070
            android.content.Context r0 = r4.mAppContext
            java.lang.String r0 = b.b.o.g.b.b(r0, r5)
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            if (r2 != 0) goto L_0x0070
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            r4.mCityCode = r0     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            com.miui.networkassistant.service.ITrafficCornBinder[] r0 = r4.mTrafficCornBinders     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            int r2 = r4.mSlotNum     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            r0 = r0[r2]     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            int r2 = r4.mCityCode     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            int r0 = r0.getProvinceCodeByCityCode(r2)     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            r4.mProvinceCode = r0     // Catch:{ NumberFormatException -> 0x0068, RemoteException -> 0x0062 }
            goto L_0x0070
        L_0x0062:
            r0 = move-exception
            java.lang.String r2 = TAG
            java.lang.String r3 = "get area location failed"
            goto L_0x006d
        L_0x0068:
            r0 = move-exception
            java.lang.String r2 = TAG
            java.lang.String r3 = "parse city code exception"
        L_0x006d:
            android.util.Log.i(r2, r3, r0)
        L_0x0070:
            int r0 = r4.mProvince
            if (r0 >= 0) goto L_0x007a
            int r0 = r4.mProvinceCode
            if (r0 <= 0) goto L_0x007a
            r4.mProvince = r0
        L_0x007a:
            int r0 = r4.mCity
            if (r0 >= 0) goto L_0x0084
            int r0 = r4.mCityCode
            if (r0 <= 0) goto L_0x0084
            r4.mCity = r0
        L_0x0084:
            int r0 = r4.mProvince
            r4.selectProvince(r0)
            int r0 = r4.mBrand
            r4.selectPackageType(r0)
            r4.refreshButtonView()
            android.content.Context r0 = r4.mAppContext
            boolean r0 = b.b.c.h.f.j(r0)
            if (r0 == 0) goto L_0x00a9
            com.miui.networkassistant.config.SimUserInfo[] r0 = r4.mSimUserInfos
            int r2 = r4.mSlotNum
            r0 = r0[r2]
            boolean r0 = r0.isMiMobileOperatorModify()
            if (r0 != 0) goto L_0x00a9
            r4.exeGetVirtualOperatorTask()
            goto L_0x00ac
        L_0x00a9:
            r4.initOperator(r5)
        L_0x00ac:
            int r5 = r4.mProvinceCode
            if (r5 <= r1) goto L_0x00bf
            int r0 = r4.mCityCode
            if (r0 <= r1) goto L_0x00bf
            int r1 = r4.mProvince
            if (r5 == r1) goto L_0x00bf
            int r5 = r4.mCity
            if (r0 == r5) goto L_0x00bf
            r4.showSimLocationErrorDialog()
        L_0x00bf:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.OperatorSettingFragment.initSimLocation(java.lang.String):void");
    }

    private void navigateToMainActivity() {
        Bundle arguments = getArguments();
        if (!arguments.getBoolean(BUNDLE_KEY_NEED_BACK, false)) {
            if (this.mIsTrafficGuide) {
                Intent intent = new Intent(this.mActivity, NetworkAssistantActivity.class);
                intent.putExtra(Sim.SIM_SLOT_NUM_TAG, this.mSlotNum);
                intent.setFlags(arguments.getBoolean(BUNDLE_KEY_FROM_NOTIFICATION) ? 268468224 : 67108864);
                startActivity(intent);
            }
            if (this.mSimUserInfos[this.mSlotNum].isNATipsEnable()) {
                TrafficUpdateUtil.broadCastTrafficUpdated(this.mAppContext);
            }
        }
    }

    private void onOperatorSelected(String str) {
        setOperatorText(this.mOperatorMap.get(this.mOperator));
        refreshCorrectionView();
        refreshButtonView();
    }

    private void onSelectCity(int i) {
        Map<Integer, String> map;
        if (i >= 0 && (map = this.mCityMap) != null) {
            this.mCity = ((Integer) map.keySet().toArray()[i]).intValue();
            setCityText(this.mCityMap.get(Integer.valueOf(this.mCity)));
            refreshButtonView();
        }
    }

    private void onSelectOperator(int i) {
        Map<String, String> map;
        if (i >= 0 && (map = this.mOperatorMap) != null) {
            String str = (String) map.keySet().toArray()[i];
            if (!TextUtils.equals(this.mOperator, str)) {
                this.mOperator = str;
                onOperatorSelected(str);
            }
        }
    }

    private void onSelectProvince(int i) {
        Map<Integer, String> map;
        if (i >= 0 && (map = this.mProvinceMap) != null) {
            int intValue = ((Integer) map.keySet().toArray()[i]).intValue();
            if (this.mProvince != intValue) {
                this.mProvince = intValue;
                this.mCity = -1;
                selectProvince(intValue);
            }
            refreshButtonView();
        }
    }

    private void refreshButtonView() {
        Button button = this.mNextButton;
        if (button != null) {
            button.setEnabled((this.mBrand == -1 || this.mCity == -1 || TextUtils.isEmpty(this.mOperator)) ? false : true);
        }
    }

    private void refreshCorrectionView() {
        boolean isSupportCorrection = this.mSimUserInfos[this.mSlotNum].isSupportCorrection(this.mOperator);
        this.mAutoCorrectionEnable = isSupportCorrection && this.mSimUserInfos[this.mSlotNum].isDataUsageAutoCorrectionOn();
        this.mAutoAdjustPreference.setChecked(this.mAutoCorrectionEnable);
        this.mAutoAdjustPreference.setEnabled(isSupportCorrection);
    }

    private void saveTrafficCorrectionInfo() {
        if (this.mServiceConnected && this.mCity != -1 && !TextUtils.isEmpty(this.mOperator)) {
            setSimLocationAlertIgnore();
            this.mSimUserInfos[this.mSlotNum].saveProvince(this.mProvince);
            this.mSimUserInfos[this.mSlotNum].saveCity(this.mCity);
            this.mSimUserInfos[this.mSlotNum].saveOperator(this.mOperator);
            this.mSimUserInfos[this.mSlotNum].setMiMobileOperatorModify(true);
            if (!this.mSimUserInfos[this.mSlotNum].isTotalDataUsageSetted()) {
                this.mSimUserInfos[this.mSlotNum].saveDataUsageTotal(-2);
                this.mSimUserInfos[this.mSlotNum].saveDataUsageOverLimitStopNetworkTime(System.currentTimeMillis());
            }
            if (this.mIsTrafficGuide) {
                this.mSimUserInfos[this.mSlotNum].saveBrand(this.mBrand);
            }
            this.mSimUserInfos[this.mSlotNum].setTrafficTcResultCode(0);
            this.mSimUserInfos[this.mSlotNum].setBillTcResultCode(0);
            this.mSimUserInfos[this.mSlotNum].setTrafficSmsDetail("");
            this.mSimUserInfos[this.mSlotNum].setBillSmsDetail("");
            try {
                this.mTrafficManageBinder.toggleDataUsageAutoCorrection(this.mAutoCorrectionEnable, this.mSlotNum);
            } catch (RemoteException e) {
                Log.e(TAG, "toggleDataUsageAutoCorrection", e);
            }
            if (this.mIsTrafficGuide) {
                int i = this.mBrand;
                if (i == 0 || i == 2) {
                    startCorrection();
                    navigateToMainActivity();
                }
            }
        }
    }

    private void selectPackageType(int i) {
        RadioButtonPreference radioButtonPreference;
        if (i == 0) {
            radioButtonPreference = this.mNormalPreference;
        } else if (i == 1) {
            radioButtonPreference = this.mDailyCardPreference;
        } else if (i == 2) {
            radioButtonPreference = this.mNotLimitPreference;
        } else {
            return;
        }
        radioButtonPreference.setChecked(true);
    }

    /* access modifiers changed from: private */
    public void selectProvince(int i) {
        setProvinceText(this.mProvinceMap.get(Integer.valueOf(this.mProvince)));
        this.mCityMap = getCityMapByProvinceId(i);
        if (this.mCityMap.size() == 2) {
            this.mCity = ((Integer) this.mCityMap.keySet().toArray()[1]).intValue();
        }
        setCityText(this.mCityMap.get(Integer.valueOf(this.mCity)));
    }

    private void setButtonText(int i) {
        Button button = this.mNextButton;
        if (button != null) {
            button.setText(i);
        }
    }

    private void setCityText(String str) {
        this.mCityPreferenceOld.a(str);
    }

    private void setOperatorText(String str) {
        this.mOperatorPreferenceOld.a(str);
    }

    private void setProvinceText(String str) {
        this.mProvincePreferenceOld.a(str);
    }

    private void setSimLocationAlertIgnore() {
        if (this.mProvince != this.mSimUserInfos[this.mSlotNum].getProvince() || this.mCity != this.mSimUserInfos[this.mSlotNum].getCity()) {
            this.mSimUserInfos[this.mSlotNum].setSimLocationAlertIgnore(false);
        }
    }

    private void showSimLocationErrorDialog() {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.getBoolean(UPDATE_OPERATOR_FROM_NOTIFICATION)) {
            String string = this.mAppContext.getResources().getString(R.string.sim_location_error_notify_title);
            String simCardLocation = getSimCardLocation(this.mProvince, this.mCity);
            String str = TAG;
            Log.i(str, "mProvinceCode: " + this.mProvinceCode + ",mCityCode: " + this.mCityCode);
            String simCardLocation2 = getSimCardLocation(this.mProvinceCode, this.mCityCode);
            String str2 = null;
            if (!TextUtils.isEmpty(simCardLocation) && !TextUtils.isEmpty(simCardLocation2)) {
                str2 = String.format(this.mAppContext.getString(R.string.sim_location_error_dialog_message), new Object[]{simCardLocation2, simCardLocation, simCardLocation2});
            }
            buildLocationAlertDialog(string, str2);
        }
    }

    private void startCorrection() {
        if (this.mServiceConnected) {
            try {
                this.mTrafficManageBinder.startCorrection(false, this.mSlotNum, true, this.mSimUserInfos[this.mSlotNum].isNormalCardEnable() ? 7 : 2);
            } catch (RemoteException e) {
                Log.i(TAG, "stat Correction exception", e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.operator_settings_preferences;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        this.mProvincePreferenceOld = (TextPreference) findPreference(PREF_KEY_PROVINCE_OLD);
        this.mCityPreferenceOld = (TextPreference) findPreference(PREF_KEY_CITY_OLD);
        this.mOperatorPreferenceOld = (TextPreference) findPreference(PREF_KEY_OPERATOR_OLD);
        this.mProvincePreference = (DropDownPreference) findPreference(PREF_KEY_PROVINCE);
        this.mCityPreference = (DropDownPreference) findPreference(PREF_KEY_CITY);
        this.mOperatorPreference = (DropDownPreference) findPreference(PREF_KEY_OPERATOR);
        this.mProvincePreferenceOld.setOnPreferenceClickListener(this);
        this.mCityPreferenceOld.setOnPreferenceClickListener(this);
        this.mOperatorPreferenceOld.setOnPreferenceClickListener(this);
        this.mTipTextCategory = (PreferenceCategory) findPreference(CATEGORY_KEY_TIP_TEXT);
        this.mPackageTypeCategory = (PreferenceCategory) findPreference(CATEGORY_KEY_PACKAGE_TYPE);
        this.mNormalPreference = (RadioButtonPreference) findPreference(PREF_NORMAL_CARD);
        this.mDailyCardPreference = (RadioButtonPreference) findPreference(PREF_DAILY_CARD);
        this.mNotLimitPreference = (RadioButtonPreference) findPreference(PREF_NO_LIMIT_CARD);
        this.mNormalPreference.setOnPreferenceClickListener(this);
        this.mDailyCardPreference.setOnPreferenceClickListener(this);
        this.mNotLimitPreference.setOnPreferenceClickListener(this);
        this.mAdjustTrafficCategory = (PreferenceCategory) findPreference(CATEGORY_KEY_ADJUST_TRAFFIC);
        this.mAutoAdjustPreference = (CheckBoxPreference) findPreference(PREF_AUTO_ADJUST_TRAFFIC);
        this.mAdjustDataPreference = (TextPreference) findPreference(PREF_ADJUST_DATA_USAGE);
        this.mReportPreference = (TextPreference) findPreference(PREF_TC_SMS_REPORT);
        this.mAutoAdjustPreference.setOnPreferenceChangeListener(this);
        this.mAdjustDataPreference.setOnPreferenceClickListener(this);
        this.mReportPreference.setOnPreferenceClickListener(this);
        this.mItemsDialog = new SingleChoiceItemsDialog(this.mActivity, this);
        String string = this.mAppContext.getString(R.string.traffic_setting_fragment_default);
        setProvinceText(string);
        setCityText(string);
        setOperatorText(string);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Bundle arguments = getArguments();
        this.mIsTrafficGuide = arguments != null && (arguments.getBoolean(BUNDLE_KEY_TRAFFIC_GUIDE) || arguments.getBoolean(BUNDLE_KEY_FROM_NOTIFICATION));
        addSaveButton();
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        cancelVirtualOperatorTask();
    }

    public void onPause() {
        saveTrafficCorrectionInfo();
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!this.mServiceConnected) {
            return true;
        }
        if (preference != this.mAutoAdjustPreference) {
            return false;
        }
        this.mAutoCorrectionEnable = Boolean.valueOf(obj.toString()).booleanValue();
        this.mAutoAdjustPreference.setChecked(this.mAutoCorrectionEnable);
        return false;
    }

    public boolean onPreferenceClick(Preference preference) {
        Map<Integer, String> map;
        Map<Integer, String> map2;
        Map<String, String> map3;
        SingleChoiceItemsDialog singleChoiceItemsDialog;
        int i;
        String[] strArr;
        int posByTag;
        if (!(!this.mServiceConnected || (map = this.mProvinceMap) == null || (map2 = this.mCityMap) == null || (map3 = this.mOperatorMap) == null)) {
            if (preference == this.mProvincePreferenceOld) {
                this.mItemsDialog.buildDialog((int) R.string.traffic_setting_fragment_province, (String[]) map.values().toArray(new String[0]), getPosByTag(Integer.valueOf(this.mProvince), this.mProvinceMap), 1);
            } else {
                int i2 = 2;
                if (preference == this.mCityPreferenceOld) {
                    singleChoiceItemsDialog = this.mItemsDialog;
                    i = R.string.traffic_setting_fragment_city;
                    strArr = (String[]) map2.values().toArray(new String[0]);
                    posByTag = getPosByTag(Integer.valueOf(this.mCity), this.mCityMap);
                } else if (preference == this.mOperatorPreferenceOld) {
                    singleChoiceItemsDialog = this.mItemsDialog;
                    i = R.string.traffic_setting_fragment_operator;
                    strArr = (String[]) map3.values().toArray(new String[0]);
                    posByTag = getPosByTag(this.mOperator, this.mOperatorMap);
                    i2 = 3;
                } else if (preference == this.mAdjustDataPreference) {
                    g.startWithFragment(this.mActivity, TemplateSettingFragment.class);
                } else if (preference == this.mReportPreference) {
                    Bundle bundle = new Bundle();
                    bundle.putString("view_from", OperatorSettingFragment.class.getSimpleName());
                    g.startWithFragment(this.mActivity, TcSmsReportFragment.class, bundle);
                } else {
                    if (preference == this.mNormalPreference) {
                        this.mBrand = 0;
                    } else if (preference == this.mDailyCardPreference) {
                        this.mBrand = 1;
                        setButtonText(R.string.traffic_setting_fragment_next);
                        refreshButtonView();
                    } else if (preference == this.mNotLimitPreference) {
                        this.mBrand = 2;
                    }
                    setButtonText(R.string.traffic_setting_fragment_button2_text);
                    refreshButtonView();
                }
                singleChoiceItemsDialog.buildDialog(i, strArr, posByTag, i2);
            }
        }
        return true;
    }

    public void onSelectItemUpdate(int i, int i2) {
        if (this.mServiceConnected) {
            if (i2 == 1) {
                onSelectProvince(i);
            } else if (i2 == 2) {
                onSelectCity(i);
            } else if (i2 == 3) {
                onSelectOperator(i);
            }
        }
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.traffic_setting_fragment_title;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        postOnUiThread(new b(this) {
            public void runOnUiThread() {
                OperatorSettingFragment.this.initData();
            }
        });
    }
}
