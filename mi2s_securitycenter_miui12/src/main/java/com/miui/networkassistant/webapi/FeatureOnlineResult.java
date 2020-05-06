package com.miui.networkassistant.webapi;

import b.b.c.g.d;
import com.miui.activityutil.o;
import org.json.JSONObject;

public class FeatureOnlineResult extends d {
    private String mStatus;

    public FeatureOnlineResult(String str) {
        super(str);
    }

    /* access modifiers changed from: protected */
    public boolean doParseJson(JSONObject jSONObject) {
        JSONObject optJSONObject;
        super.doParseJson(jSONObject);
        if (!isSuccess() || (optJSONObject = jSONObject.optJSONObject("info")) == null) {
            return true;
        }
        this.mDesc = optJSONObject.optString("desc");
        this.mStatus = optJSONObject.optString("status");
        return true;
    }

    public String getStatus() {
        return this.mStatus;
    }

    public boolean isOnline() {
        return o.f2309a.equals(this.mStatus);
    }

    public void setStatus(String str) {
        this.mStatus = str;
    }

    public String toString() {
        if (!isSuccess()) {
            return super.toString();
        }
        return super.toString() + String.format("mStatus:%s", new Object[]{this.mStatus});
    }
}
