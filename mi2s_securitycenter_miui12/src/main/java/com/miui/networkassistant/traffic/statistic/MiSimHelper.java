package com.miui.networkassistant.traffic.statistic;

import android.content.Context;
import miui.provider.ExtraSettings;
import org.json.JSONException;
import org.json.JSONObject;

public class MiSimHelper {
    private static final int UNIT_RATE = 1024;
    private Context mContext;
    private long mCurrentMonthPackage;
    private long mCurrentMonthRemainedFlow;
    private long mLastMonthRemainedFlow;
    private long mMonthUsedFlow;
    private String mSimName;
    private long mTotalMonthFlow;
    private long mTotalRemainedFlow;

    public MiSimHelper(Context context) {
        this.mContext = context;
    }

    public long getCurrentMonthPackage() {
        return this.mCurrentMonthPackage;
    }

    public long getCurrentMonthRemainedFlow() {
        return this.mCurrentMonthRemainedFlow;
    }

    public long getLastMonthRemainedFlow() {
        return this.mLastMonthRemainedFlow;
    }

    public long getMonthUsedFlow() {
        return this.mMonthUsedFlow;
    }

    public String getSimName() {
        return this.mSimName;
    }

    public long getTotalMonthFlow() {
        return this.mTotalMonthFlow;
    }

    public long getTotalRemainedFlow() {
        return this.mTotalRemainedFlow;
    }

    public void refreshMiSimFlowData() {
        try {
            JSONObject jSONObject = new JSONObject(ExtraSettings.System.getString(this.mContext.getContentResolver(), "mm_adjust_flow_result", ""));
            this.mTotalRemainedFlow = jSONObject.optLong("totalFlowBalance") * 1024;
            this.mTotalMonthFlow = jSONObject.optLong("totalFlowSize") * 1024;
            this.mMonthUsedFlow = jSONObject.optLong("alreadyUsedFlow") * 1024;
            this.mLastMonthRemainedFlow = jSONObject.optLong("lastFlowBalance") * 1024;
            this.mCurrentMonthRemainedFlow = jSONObject.optLong("curFlowBalance") * 1024;
            this.mCurrentMonthPackage = jSONObject.optLong("curBasePackageSize") * 1024;
            this.mSimName = jSONObject.optString("appName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "总剩余流量 : " + this.mTotalRemainedFlow + "\n本月总流量 : " + this.mTotalMonthFlow + "\n本月已用流量 : " + this.mMonthUsedFlow + "\n上月剩余流量 : " + this.mLastMonthRemainedFlow + "\n本月剩余流量 : " + this.mCurrentMonthRemainedFlow + "\n本月套餐流量 : " + this.mCurrentMonthPackage + "\nSim卡名称 : " + this.mSimName;
    }
}
