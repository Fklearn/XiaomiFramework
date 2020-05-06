package com.miui.networkassistant.webapi;

import org.json.JSONObject;

public class PurchaseOnlineResult extends FeatureOnlineResult {
    private String mOrderTips;
    private int mOrderType;
    private String mPurchaseHotActivityId;
    private int mTrafficPassStatus;

    public PurchaseOnlineResult(String str) {
        super(str);
    }

    /* access modifiers changed from: protected */
    public boolean doParseJson(JSONObject jSONObject) {
        super.doParseJson(jSONObject);
        if (isSuccess()) {
            JSONObject optJSONObject = jSONObject.optJSONObject("info");
            if (optJSONObject != null) {
                this.mOrderType = optJSONObject.optInt("orderType");
                this.mOrderTips = optJSONObject.optString("orderTip");
                this.mPurchaseHotActivityId = optJSONObject.optString("onLineActivityId");
            }
            JSONObject optJSONObject2 = jSONObject.optJSONObject("miflow");
            if (optJSONObject2 != null) {
                this.mTrafficPassStatus = optJSONObject2.optInt("status", 1);
            } else {
                this.mTrafficPassStatus = 1;
            }
        }
        return true;
    }

    public int getOrderType() {
        return this.mOrderType;
    }

    public String getPurchaseActivityId() {
        return this.mPurchaseHotActivityId;
    }

    public int getTrafficPassStatus() {
        return this.mTrafficPassStatus;
    }

    public String getmOrderTips() {
        return this.mOrderTips;
    }

    public String toString() {
        if (!isSuccess()) {
            return super.toString();
        }
        return super.toString() + String.format(",mOrderType:%s,mOrderTips:%s,mPurchaseHotActivityId: %s", new Object[]{Integer.valueOf(this.mOrderType), this.mOrderTips, this.mPurchaseHotActivityId});
    }
}
