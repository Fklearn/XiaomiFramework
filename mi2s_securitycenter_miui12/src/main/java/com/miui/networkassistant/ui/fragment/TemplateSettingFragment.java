package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.os.Handler;
import android.os.Message;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.networkassistant.service.ITrafficCornBinder;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.ui.base.TrafficRelatedPreFragment;
import com.miui.networkassistant.ui.dialog.TextInputDialog;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.securitycenter.R;
import miuix.preference.TextPreference;

public class TemplateSettingFragment extends TrafficRelatedPreFragment implements Preference.b, Preference.c {
    private static final int ACTION_SMS_CONTENT = 2;
    private static final int ACTION_SMS_NUM = 1;
    private static final int MSG_TRAFFIC_MANAGE_SERVICE_CONNECTED = 1;
    private static final String PREF_CORRECTION_SETTING_SWITCH = "pref_correction_setting_switch";
    private static final String PREF_CORRECTION_SMS_CONTENT = "pref_correction_sms_content";
    private static final String PREF_CORRECTION_SMS_NUMBER = "pref_correction_sms_number";
    private static final String TAG = "TemplateSettingFragment";
    private static final int TITLE_FILED = 2131757414;
    /* access modifiers changed from: private */
    public boolean mChanged = false;
    private CheckBoxPreference mCustomizeTemplateSwitch;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                TemplateSettingFragment.this.initData();
            }
        }
    };
    private TextInputDialog mInputDialog;
    private boolean mIsCustomizedSms;
    /* access modifiers changed from: private */
    public String mSmsContent;
    /* access modifiers changed from: private */
    public TextPreference mSmsContentSetting;
    /* access modifiers changed from: private */
    public String mSmsNum;
    /* access modifiers changed from: private */
    public TextPreference mSmsNumberSetting;
    private TextInputDialog.TextInputDialogListener mTextInputDialogListener = new TextInputDialog.TextInputDialogListener() {
        public void onTextSetted(String str, int i) {
            TextPreference textPreference;
            if (i != 1) {
                if (i == 2) {
                    String unused = TemplateSettingFragment.this.mSmsContent = str;
                    textPreference = TemplateSettingFragment.this.mSmsContentSetting;
                }
                boolean unused2 = TemplateSettingFragment.this.mChanged = true;
            }
            String unused3 = TemplateSettingFragment.this.mSmsNum = str;
            textPreference = TemplateSettingFragment.this.mSmsNumberSetting;
            textPreference.a(str);
            boolean unused4 = TemplateSettingFragment.this.mChanged = true;
        }
    };

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mServiceConnected) {
            boolean isCustomizedSms = this.mSimUserInfos[this.mSlotNum].isCustomizedSms();
            this.mCustomizeTemplateSwitch.setChecked(isCustomizedSms);
            setCustomizedSmsEnable(isCustomizedSms);
            initInstruction();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x004e, code lost:
        r1 = (java.lang.String) r0.getKey();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initInstruction() {
        /*
            r6 = this;
            com.miui.networkassistant.config.SimUserInfo[] r0 = r6.mSimUserInfos
            int r1 = r6.mSlotNum
            r0 = r0[r1]
            java.lang.String r0 = r0.getCustomizedSmsNum()
            r6.mSmsNum = r0
            com.miui.networkassistant.config.SimUserInfo[] r0 = r6.mSimUserInfos
            int r1 = r6.mSlotNum
            r0 = r0[r1]
            java.lang.String r0 = r0.getCustomizedSmsContent()
            r6.mSmsContent = r0
            com.miui.networkassistant.service.ITrafficCornBinder[] r0 = r6.mTrafficCornBinders     // Catch:{ RemoteException -> 0x0024 }
            int r1 = r6.mSlotNum     // Catch:{ RemoteException -> 0x0024 }
            r0 = r0[r1]     // Catch:{ RemoteException -> 0x0024 }
            r1 = 1
            java.util.Map r0 = r0.getInstructions(r1)     // Catch:{ RemoteException -> 0x0024 }
            goto L_0x002d
        L_0x0024:
            r0 = move-exception
            java.lang.String r1 = "TemplateSettingFragment"
            java.lang.String r2 = "get instructions failed"
            android.util.Log.i(r1, r2, r0)
            r0 = 0
        L_0x002d:
            if (r0 == 0) goto L_0x0092
            int r1 = r0.size()
            if (r1 <= 0) goto L_0x0092
            java.util.Set r0 = r0.entrySet()
            java.util.Iterator r0 = r0.iterator()
            java.lang.Object r0 = r0.next()
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0
            java.lang.String r1 = r6.mSmsNum
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            r2 = 0
            java.lang.String r3 = "#"
            if (r1 == 0) goto L_0x007c
            java.lang.Object r1 = r0.getKey()
            java.lang.String r1 = (java.lang.String) r1
            int r4 = r1.indexOf(r3)
            if (r4 <= 0) goto L_0x007c
            java.lang.String r1 = r1.substring(r2, r4)
            r6.mSmsNum = r1
            java.lang.String r1 = r6.mSmsNum
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x007c
            android.content.Context r1 = r6.mAppContext
            com.miui.networkassistant.config.SimUserInfo[] r4 = r6.mSimUserInfos
            int r5 = r6.mSlotNum
            r4 = r4[r5]
            java.lang.String r4 = r4.getImsi()
            int r5 = r6.mSlotNum
            java.lang.String r1 = com.miui.networkassistant.utils.TextPrepareUtil.getOperatorNumber(r1, r4, r5)
            r6.mSmsNum = r1
        L_0x007c:
            java.lang.String r1 = r6.mSmsContent
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0092
            java.lang.Object r0 = r0.getValue()
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String[] r0 = r0.split(r3)
            r0 = r0[r2]
            r6.mSmsContent = r0
        L_0x0092:
            miuix.preference.TextPreference r0 = r6.mSmsContentSetting
            java.lang.String r1 = r6.mSmsContent
            r0.a((java.lang.String) r1)
            miuix.preference.TextPreference r0 = r6.mSmsNumberSetting
            java.lang.String r1 = r6.mSmsNum
            r0.a((java.lang.String) r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.TemplateSettingFragment.initInstruction():void");
    }

    private void saveCustomizedData() {
        this.mSimUserInfos[this.mSlotNum].saveCustomizedSmsNum(this.mSmsNum);
        this.mSimUserInfos[this.mSlotNum].saveCustomizedSmsContent(this.mSmsContent);
    }

    private void setCustomizedSmsEnable(boolean z) {
        this.mSmsContentSetting.setEnabled(z);
        this.mSmsNumberSetting.setEnabled(z);
    }

    private void trackCustomizedSms() {
        if (this.mIsCustomizedSms) {
            AnalyticsHelper.trackCustomizedSms(new ITrafficCorrection.TrafficConfig(String.valueOf(this.mSimUserInfos[this.mSlotNum].getProvince()), String.valueOf(this.mSimUserInfos[this.mSlotNum].getCity()), String.valueOf(this.mSimUserInfos[this.mSlotNum].getBrand())), this.mSmsNum, this.mSmsContent);
        }
    }

    /* access modifiers changed from: protected */
    public int getXmlPreference() {
        return R.xml.template_setting_preferences;
    }

    /* access modifiers changed from: protected */
    public void initPreferenceView() {
        this.mCustomizeTemplateSwitch = (CheckBoxPreference) findPreference(PREF_CORRECTION_SETTING_SWITCH);
        this.mCustomizeTemplateSwitch.setOnPreferenceChangeListener(this);
        this.mSmsNumberSetting = (TextPreference) findPreference(PREF_CORRECTION_SMS_NUMBER);
        this.mSmsNumberSetting.setOnPreferenceClickListener(this);
        this.mSmsContentSetting = (TextPreference) findPreference(PREF_CORRECTION_SMS_CONTENT);
        this.mSmsContentSetting.setOnPreferenceClickListener(this);
        this.mInputDialog = new TextInputDialog(this.mActivity, this.mTextInputDialogListener);
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        trackCustomizedSms();
        if (this.mChanged && this.mServiceConnected) {
            saveCustomizedData();
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mCustomizeTemplateSwitch) {
            this.mIsCustomizedSms = booleanValue;
            this.mSimUserInfos[this.mSlotNum].toggleCustomizedSms(booleanValue);
            setCustomizedSmsEnable(this.mIsCustomizedSms);
            this.mChanged = true;
        }
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mSmsContentSetting) {
            this.mInputDialog.setNumberText(false);
            this.mInputDialog.buildInputDialog((int) R.string.traffic_setting_fragment_sms_content, (int) R.string.traffic_setting_fragment_sms_content_tips, 2);
        } else if (preference == this.mSmsNumberSetting) {
            this.mInputDialog.setNumberText(true);
            this.mInputDialog.buildInputDialog((int) R.string.traffic_setting_fragment_send_num, (int) R.string.traffic_setting_fragment_send_num_tips, 1);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.pref_adjust_data_usage;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        ITrafficCornBinder[] iTrafficCornBinderArr = this.mTrafficCornBinders;
        int i = this.mSlotNum;
        if (iTrafficCornBinderArr[i] != null && this.mSimUserInfos[i] != null) {
            this.mHandler.sendEmptyMessage(1);
        }
    }
}
