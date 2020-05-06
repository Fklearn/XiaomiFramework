package com.miui.networkassistant.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.TelephonyUtil;

public class NetworkOverLimitActivity extends Activity {
    /* access modifiers changed from: private */
    public Dialog mDialog;
    /* access modifiers changed from: private */
    public boolean mIsIgnore;
    private int mIsOverLimitType;
    private boolean mIsTrafficPurchaseAvailable;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            if (i != 0) {
                NetworkOverLimitActivity.this.mDialog.dismiss();
            }
        }
    };
    private SimUserInfo mSimUserInfo;
    private TelephonyManager mTelephonyManager;

    /* access modifiers changed from: private */
    public void enableMobileDataConnection() {
        Settings.Secure.putInt(getContentResolver(), Constants.System.MOBILE_POLICY, 1);
        TelephonyUtil.setMobileDataState(getApplicationContext(), true);
        NotificationUtil.cancelDataUsageOverLimit(this);
        this.mSimUserInfo.setMobilePolicyEnable(true);
    }

    private void registerPhoneStateListener() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
    }

    /* access modifiers changed from: private */
    public void resetDailyUsedCardSetting() {
        if (this.mIsOverLimitType == 4) {
            this.mSimUserInfo.setDailyUsedCardStopNetworkCount(this.mSimUserInfo.getDailyUsedCardStopNetworkCount() + 1);
            this.mSimUserInfo.setDailyUsedCardStopNetworkTime(0);
        }
    }

    private void unRegisterPhoneStateListener() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        finish();
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0151  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x015f  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x017d  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0189  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x019d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r25) {
        /*
            r24 = this;
            r0 = r24
            super.onCreate(r25)
            java.lang.String r1 = "phone"
            java.lang.Object r1 = r0.getSystemService(r1)
            android.telephony.TelephonyManager r1 = (android.telephony.TelephonyManager) r1
            r0.mTelephonyManager = r1
            android.app.AlertDialog$Builder r1 = new android.app.AlertDialog$Builder
            r1.<init>(r0)
            r2 = 2131755871(0x7f10035f, float:1.9142634E38)
            r1.setTitle(r2)
            boolean r3 = com.miui.networkassistant.utils.DeviceUtil.IS_DUAL_CARD
            r4 = 2
            r5 = 0
            r6 = 1
            if (r3 == 0) goto L_0x006e
            android.content.Intent r3 = r24.getIntent()
            java.lang.String r7 = "sim_slot_num_tag"
            boolean r8 = r3.hasExtra(r7)
            if (r8 == 0) goto L_0x003b
            int r3 = r3.getIntExtra(r7, r5)
            int r7 = com.miui.networkassistant.dual.Sim.getCurrentActiveSlotNum()
            if (r3 == r7) goto L_0x003f
            r24.finish()
            return
        L_0x003b:
            int r3 = com.miui.networkassistant.dual.Sim.getCurrentActiveSlotNum()
        L_0x003f:
            android.content.Context r7 = r24.getApplicationContext()
            com.miui.networkassistant.dual.SimCardHelper r7 = com.miui.networkassistant.dual.SimCardHelper.getInstance(r7)
            boolean r7 = r7.isDualSimInserted()
            if (r7 == 0) goto L_0x006f
            if (r3 != 0) goto L_0x0053
            r7 = 2131756001(0x7f1003e1, float:1.9142897E38)
            goto L_0x0056
        L_0x0053:
            r7 = 2131756002(0x7f1003e2, float:1.91429E38)
        L_0x0056:
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r2 = r0.getString(r2)
            r8[r5] = r2
            java.lang.String r2 = r0.getString(r7)
            r8[r6] = r2
            java.lang.String r2 = "%s-%s"
            java.lang.String r2 = java.lang.String.format(r2, r8)
            r1.setTitle(r2)
            goto L_0x006f
        L_0x006e:
            r3 = r5
        L_0x006f:
            com.miui.networkassistant.config.SimUserInfo r2 = com.miui.networkassistant.config.SimUserInfo.getInstance((android.content.Context) r0, (int) r3)
            r0.mSimUserInfo = r2
            android.content.Context r2 = r24.getApplicationContext()
            com.miui.networkassistant.config.SimUserInfo r7 = r0.mSimUserInfo
            boolean r2 = com.miui.networkassistant.traffic.purchase.CooperationManager.isTrafficPurchaseAvailable(r2, r7, r5)
            r0.mIsTrafficPurchaseAvailable = r2
            r2 = 2131493028(0x7f0c00a4, float:1.8609525E38)
            r7 = 0
            android.view.View r2 = android.view.View.inflate(r0, r2, r7)
            r7 = 2131296713(0x7f0901c9, float:1.821135E38)
            android.view.View r7 = r2.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            com.miui.networkassistant.config.SimUserInfo r8 = r0.mSimUserInfo
            int r8 = r8.getOverDataUsageStopNetworkType()
            r0.mIsOverLimitType = r8
            int r8 = r0.mIsOverLimitType
            r9 = 5
            r10 = 4
            r11 = 3
            if (r8 != 0) goto L_0x00c0
            boolean r8 = r0.mIsTrafficPurchaseAvailable
            if (r8 == 0) goto L_0x00bc
            r8 = 2131758416(0x7f100d50, float:1.9147795E38)
            java.lang.String r8 = r0.getString(r8)
            android.text.Spanned r8 = android.text.Html.fromHtml(r8)
            r7.setText(r8)
            com.miui.networkassistant.ui.activity.NetworkOverLimitActivity$1 r8 = new com.miui.networkassistant.ui.activity.NetworkOverLimitActivity$1
            r8.<init>(r3)
            r7.setOnClickListener(r8)
            goto L_0x0107
        L_0x00bc:
            r3 = 2131755865(0x7f100359, float:1.9142621E38)
            goto L_0x00c5
        L_0x00c0:
            if (r8 != r6) goto L_0x00c9
            r3 = 2131755859(0x7f100353, float:1.914261E38)
        L_0x00c5:
            r7.setText(r3)
            goto L_0x0107
        L_0x00c9:
            if (r8 != r11) goto L_0x00d5
            r3 = 2131756661(0x7f100675, float:1.9144236E38)
            r1.setTitle(r3)
            r3 = 2131756660(0x7f100674, float:1.9144234E38)
            goto L_0x00c5
        L_0x00d5:
            if (r8 != r4) goto L_0x00db
            r3 = 2131757744(0x7f100ab0, float:1.9146432E38)
            goto L_0x00c5
        L_0x00db:
            if (r8 != r10) goto L_0x00fb
            com.miui.networkassistant.config.SimUserInfo r3 = r0.mSimUserInfo
            int r3 = r3.getDailyUsedCardStopNetworkCount()
            android.content.res.Resources r8 = r24.getResources()
            r12 = 2131623960(0x7f0e0018, float:1.8875086E38)
            int r3 = r3 + r6
            java.lang.Object[] r13 = new java.lang.Object[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r3)
            r13[r5] = r14
            java.lang.String r3 = r8.getQuantityString(r12, r3, r13)
            r7.setText(r3)
            goto L_0x0107
        L_0x00fb:
            if (r8 != r9) goto L_0x0107
            r3 = 2131755870(0x7f10035e, float:1.9142631E38)
            r1.setTitle(r3)
            r3 = 2131755875(0x7f100363, float:1.9142642E38)
            goto L_0x00c5
        L_0x0107:
            r1.setView(r2)
            com.miui.networkassistant.config.SimUserInfo r2 = r0.mSimUserInfo
            long r2 = r2.getDataUsageOverLimitStopNetworkTime()
            com.miui.networkassistant.config.SimUserInfo r7 = r0.mSimUserInfo
            long r7 = r7.getDataUsageOverDailyLimitTime()
            com.miui.networkassistant.config.SimUserInfo r12 = r0.mSimUserInfo
            long r12 = r12.getDataUsageOverRoamingDailyLimitTime()
            com.miui.networkassistant.config.SimUserInfo r14 = r0.mSimUserInfo
            long r14 = r14.getLeisureOverLimitStopNetworkTime()
            com.miui.networkassistant.config.SimUserInfo r5 = r0.mSimUserInfo
            long r16 = r5.getDailyUsedCardStopNetworkTime()
            com.miui.networkassistant.config.SimUserInfo r5 = r0.mSimUserInfo
            long r18 = r5.getTrafficProtectedStopNetTime()
            com.miui.networkassistant.config.SimUserInfo r5 = r0.mSimUserInfo
            int r5 = r5.getMonthStart()
            long r20 = com.miui.networkassistant.utils.DateUtil.getThisMonthBeginTimeMillis(r5)
            int r5 = r0.mIsOverLimitType
            if (r5 != 0) goto L_0x0142
            int r2 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1))
            if (r2 >= 0) goto L_0x0142
            r2 = r6
            goto L_0x0143
        L_0x0142:
            r2 = 0
        L_0x0143:
            int r3 = r0.mIsOverLimitType
            if (r3 != r6) goto L_0x0151
            long r22 = com.miui.networkassistant.utils.DateUtil.getTodayTimeMillis()
            int r3 = (r7 > r22 ? 1 : (r7 == r22 ? 0 : -1))
            if (r3 >= 0) goto L_0x0151
            r3 = r6
            goto L_0x0152
        L_0x0151:
            r3 = 0
        L_0x0152:
            r2 = r2 | r3
            int r3 = r0.mIsOverLimitType
            if (r3 != r4) goto L_0x0161
            long r3 = com.miui.networkassistant.utils.DateUtil.getTodayTimeMillis()
            int r3 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0161
            r3 = r6
            goto L_0x0162
        L_0x0161:
            r3 = 0
        L_0x0162:
            r2 = r2 | r3
            int r3 = r0.mIsOverLimitType
            if (r3 != r11) goto L_0x016d
            int r3 = (r14 > r20 ? 1 : (r14 == r20 ? 0 : -1))
            if (r3 >= 0) goto L_0x016d
            r3 = r6
            goto L_0x016e
        L_0x016d:
            r3 = 0
        L_0x016e:
            r2 = r2 | r3
            int r3 = r0.mIsOverLimitType
            if (r3 != r10) goto L_0x017d
            long r3 = com.miui.networkassistant.utils.DateUtil.getTodayTimeMillis()
            int r3 = (r16 > r3 ? 1 : (r16 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x017d
            r3 = r6
            goto L_0x017e
        L_0x017d:
            r3 = 0
        L_0x017e:
            r2 = r2 | r3
            int r3 = r0.mIsOverLimitType
            if (r3 != r9) goto L_0x0189
            int r3 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1))
            if (r3 >= 0) goto L_0x0189
            r3 = r6
            goto L_0x018a
        L_0x0189:
            r3 = 0
        L_0x018a:
            r2 = r2 | r3
            com.miui.networkassistant.config.SimUserInfo r3 = r0.mSimUserInfo
            boolean r3 = r3.isNotLimitCardEnable()
            r2 = r2 | r3
            if (r2 == 0) goto L_0x019d
            r24.enableMobileDataConnection()
            r0.mIsIgnore = r6
            r24.finish()
            return
        L_0x019d:
            r2 = 17039370(0x104000a, float:2.42446E-38)
            com.miui.networkassistant.ui.activity.NetworkOverLimitActivity$2 r3 = new com.miui.networkassistant.ui.activity.NetworkOverLimitActivity$2
            r3.<init>()
            r1.setPositiveButton(r2, r3)
            r2 = 2131755866(0x7f10035a, float:1.9142623E38)
            com.miui.networkassistant.ui.activity.NetworkOverLimitActivity$3 r3 = new com.miui.networkassistant.ui.activity.NetworkOverLimitActivity$3
            r3.<init>()
            r1.setNegativeButton(r2, r3)
            android.app.AlertDialog r1 = r1.create()
            r0.mDialog = r1
            android.app.Dialog r1 = r0.mDialog
            com.miui.networkassistant.ui.activity.NetworkOverLimitActivity$4 r2 = new com.miui.networkassistant.ui.activity.NetworkOverLimitActivity$4
            r2.<init>()
            r1.setOnDismissListener(r2)
            android.app.Dialog r1 = r0.mDialog
            android.view.Window r1 = r1.getWindow()
            r2 = 2003(0x7d3, float:2.807E-42)
            r1.setType(r2)
            android.app.Dialog r1 = r0.mDialog
            r1.show()
            android.content.Context r1 = r24.getApplicationContext()
            com.miui.networkassistant.utils.AnalyticsHelper.trackActiveNetworkAssistant(r1)
            r24.registerPhoneStateListener()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.activity.NetworkOverLimitActivity.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (!this.mIsIgnore) {
            int i = 0;
            if (this.mIsOverLimitType == 4) {
                i = this.mSimUserInfo.getDailyUsedCardStopNetworkCount() + 1;
            }
            NotificationUtil.sendDataUsageOverLimit(this, this.mIsOverLimitType, i);
        }
        unRegisterPhoneStateListener();
    }
}
