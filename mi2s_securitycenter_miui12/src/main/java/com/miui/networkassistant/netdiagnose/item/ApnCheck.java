package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.netdiagnose.APNManager;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.securitycenter.R;
import java.util.HashMap;

public class ApnCheck extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NA_ND_ApnCheck";
    private static APNManager mApnManager;
    private Context mContext;
    private String mItemSummary;

    public ApnCheck(Context context) {
        super(context);
        this.mContext = context;
        mApnManager = new APNManager(context);
    }

    private boolean checkAPN() {
        Resources resources;
        int i;
        String str;
        if (TextUtils.equals(mApnManager.getCurrentApn(), "none")) {
            str = "current Apn is empty";
        } else {
            String subscriberId = TelephonyUtil.getSubscriberId(this.mContext, Sim.getCurrentActiveSlotNum());
            if (TextUtils.isEmpty(subscriberId)) {
                str = "current imsi is null";
            } else {
                String substring = subscriberId.substring(0, 3);
                String substring2 = subscriberId.substring(3, 5);
                boolean isExistAPNForCurrentSim = mApnManager.isExistAPNForCurrentSim(substring, substring2);
                Log.d(TAG, " isExistAPNForCurrentSim=" + isExistAPNForCurrentSim);
                if (!isExistAPNForCurrentSim) {
                    return false;
                }
                if (DeviceUtil.isSmartDiagnostics(this.mContext)) {
                    int checkApnResult = mApnManager.getCheckApnResult();
                    if (checkApnResult == 1) {
                        resources = this.mContext.getResources();
                        i = R.string.apn_exception_modify_summary;
                    } else if (checkApnResult != 2) {
                        str = " checkAPN isChina rst=" + checkApnResult;
                    } else {
                        resources = this.mContext.getResources();
                        i = R.string.apn_exception_wap_summary;
                    }
                    this.mItemSummary = resources.getString(i);
                    return false;
                }
                Log.i(TAG, " checkAPN isNotChina");
                this.mItemSummary = this.mContext.getResources().getString(R.string.apn_exception_summary);
                return !mApnManager.currentApnIsModified(substring, substring2);
            }
        }
        Log.i(TAG, str);
        return true;
    }

    public void check() {
        this.mIsStatusNormal = checkAPN();
        if (!this.mIsStatusNormal && !networkChanged()) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("other", "apn");
            AnalyticsHelper.trackNetworkDiagnosticsStep(hashMap);
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        mApnManager.restoreAPN();
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getFixingWaitProgressDlgMsg() {
        return this.mContext.getResources().getString(R.string.repair_loading_text);
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.apn_exception_title);
    }

    public String getItemSolution() {
        return this.mContext.getResources().getString(R.string.repair);
    }

    public String getItemSummary() {
        return this.mItemSummary;
    }
}
