package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import b.b.o.g.e;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.securitycenter.R;
import miui.telephony.SubscriptionManager;

public class FixMobileCheck extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NA_ND_FixMobileCheck";
    private Context mContext;
    private int mPreferredNw;
    private boolean mResult;
    private TelephonyManager mTelephonyManager;

    public FixMobileCheck(Context context) {
        super(context);
        this.mContext = context;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
    }

    public void check() {
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        int defaultDataSlotId = SubscriptionManager.getDefault().getDefaultDataSlotId();
        int subscriptionIdForSlot = SubscriptionManager.getDefault().getSubscriptionIdForSlot(defaultDataSlotId);
        boolean isCmccSIM = DeviceUtil.isCmccSIM(this.mContext);
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager == null) {
            Log.i(TAG, "TeleMgr is null ,just return!!!");
            return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
        }
        int i = 1;
        try {
            this.mPreferredNw = ((Integer) e.a((Object) telephonyManager, "getPreferredNetworkType", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(subscriptionIdForSlot))).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "FixMobileCheck  phoneId=" + defaultDataSlotId + ",subId=" + subscriptionIdForSlot + ",mPreferredNw=" + this.mPreferredNw);
        if (this.mPreferredNw != -1) {
            if (!isCmccSIM) {
                i = 7;
            }
            try {
                Log.i(TAG, "FixMobileCheck preNw=" + i);
                this.mResult = TelephonyUtil.setPreferredNetworkType(this.mContext, subscriptionIdForSlot, i);
                Log.i(TAG, "FixMobileCheck golbal mResult=" + this.mResult);
                Thread.sleep(500);
                this.mResult = TelephonyUtil.setPreferredNetworkType(this.mContext, subscriptionIdForSlot, this.mPreferredNw);
                Log.i(TAG, "FixMobileCheck restore mResult=" + this.mResult);
                if (!this.mResult) {
                    this.mResult = TelephonyUtil.setPreferredNetworkType(this.mContext, subscriptionIdForSlot, this.mPreferredNw);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        Log.i(TAG, "FixMobileCheck done");
        try {
            Thread.sleep(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } catch (Exception unused) {
        }
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getFixingWaitProgressDlgMsg() {
        return "";
    }

    public String getItemName() {
        return this.mContext.getString(R.string.nd_result_title_optimize);
    }

    public String getItemSolution() {
        return this.mContext.getString(R.string.nd_result_button_optimize);
    }

    public String getItemSummary() {
        return this.mContext.getString(R.string.nd_result_summary_optimize);
    }
}
