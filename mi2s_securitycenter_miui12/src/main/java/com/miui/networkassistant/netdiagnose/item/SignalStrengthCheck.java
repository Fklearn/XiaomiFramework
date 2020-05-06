package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.c;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsManager;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.securitycenter.R;
import java.util.HashMap;

public class SignalStrengthCheck extends AbstractNetworkDiagoneItem {
    private static final int SIGNAL_BIN_LEVEL = 2;
    private static final String TAG = "NA_ND_SignalCheck";
    private Context mContext;
    private String mItemSummary;
    private NetworkDiagnosticsManager mNetworkDiagnosticsManager;
    private int mNetworkType;
    private int mPhoneType;
    private SimCardHelper mSimCardHelper = null;
    private String mSolution;

    public SignalStrengthCheck(Context context) {
        super(context);
        this.mContext = context;
        this.mNetworkType = 0;
        this.mPhoneType = 0;
        this.mSimCardHelper = SimCardHelper.getInstance(context);
        this.mNetworkDiagnosticsManager = NetworkDiagnosticsManager.getInstance(this.mContext);
    }

    private String getNetworkOperatorNameForSlot(int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getNetworkOperatorNameForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.f();
    }

    private int getPhoneTypeForSlot(int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getPhoneTypeForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.c();
    }

    public void check() {
        String str;
        Resources resources;
        int i;
        this.mSolution = "";
        boolean z = false;
        this.mIsStatusNormal = false;
        int currentMobileSlotNum = this.mSimCardHelper.getCurrentMobileSlotNum();
        if (!this.mSimCardHelper.isSimCardReady(currentMobileSlotNum)) {
            this.mItemSummary = this.mContext.getResources().getString(R.string.sim_state_exception_summary);
            return;
        }
        this.mNetworkType = TelephonyUtil.getNetworkTypeForSlot(this.mContext, currentMobileSlotNum);
        this.mPhoneType = getPhoneTypeForSlot(currentMobileSlotNum);
        String simImsi = this.mSimCardHelper.getSimImsi(currentMobileSlotNum);
        String networkOperatorNameForSlot = getNetworkOperatorNameForSlot(currentMobileSlotNum);
        if (TextUtils.isEmpty(simImsi)) {
            this.mItemSummary = this.mContext.getResources().getString(R.string.network_operator_exception_summary);
            this.mSolution = this.mContext.getResources().getString(R.string.see_detail);
            Log.e(TAG, "check() imsi is null");
        } else if (this.mPhoneType == 2 || !TextUtils.isEmpty(networkOperatorNameForSlot)) {
            if (DeviceUtil.isSmartDiagnostics(this.mContext)) {
                int currentSignalStrength = this.mNetworkDiagnosticsManager.getCurrentSignalStrength();
                int currentLteRsrq = this.mNetworkDiagnosticsManager.getCurrentLteRsrq();
                int currentDataRat = this.mNetworkDiagnosticsManager.getCurrentDataRat();
                Log.d(TAG, "check() bin=" + currentSignalStrength + ",rsrq=" + currentLteRsrq + ",dataRat=" + currentDataRat);
                if (currentSignalStrength < 2) {
                    Log.d(TAG, "signal-");
                } else {
                    if (NetworkDiagnosticsUtils.isNonEutran(currentDataRat)) {
                        Log.d(TAG, "non-4g");
                        this.mIsStatusNormal = false;
                        resources = this.mContext.getResources();
                        i = R.string.signal_exception_none_4g_5g_network;
                    } else if (NetworkDiagnosticsUtils.isPoorLteRsrq(currentLteRsrq)) {
                        this.mIsStatusNormal = false;
                        Log.d(TAG, "bad rsrq");
                        resources = this.mContext.getResources();
                        i = R.string.signal_exception_4g_bad_rsrq;
                    } else {
                        this.mIsStatusNormal = true;
                        Log.d(TAG, "mIsStatusNormal=" + this.mIsStatusNormal + ",mItemSummary=" + this.mItemSummary);
                    }
                    str = resources.getString(i);
                    this.mItemSummary = str;
                }
            } else if (this.mNetworkDiagnosticsManager.getCurrentSignalStrength() >= 2) {
                z = true;
            }
            this.mIsStatusNormal = z;
            str = this.mContext.getResources().getString(R.string.signal_exception_summary);
            this.mItemSummary = str;
        } else {
            this.mItemSummary = this.mContext.getResources().getString(R.string.network_operator_exception_summary);
            this.mSolution = this.mContext.getResources().getString(R.string.see_detail);
            Log.i(TAG, "check() networkOperatorName is null. netowrkType =" + this.mNetworkType);
        }
        if (!this.mIsStatusNormal) {
            SimUserInfo instance = SimUserInfo.getInstance(this.mContext, currentMobileSlotNum);
            if (instance != null) {
                Log.d(TAG, "check() simUserInfo=" + instance.toString() + " phoneType:" + this.mPhoneType + "networkType:" + this.mNetworkType);
            }
            if (!networkChanged()) {
                HashMap hashMap = new HashMap(1);
                hashMap.put("other", "signal");
                AnalyticsHelper.trackNetworkDiagnosticsStep(hashMap);
            }
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        Intent intent = new Intent(this.mContext, NetworkDiagnosticsTipActivity.class);
        intent.setFlags(276824064);
        intent.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, getItemName());
        intent.putExtra(NetworkDiagnosticsTipActivity.DETAIL_KEY_NAME, R.string.network_sim_exception_detail);
        this.mContext.startActivity(intent);
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.signal_exception_title);
    }

    public String getItemSolution() {
        return this.mSolution;
    }

    public String getItemSummary() {
        return this.mItemSummary;
    }
}
