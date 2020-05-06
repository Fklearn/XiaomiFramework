package com.miui.powercenter.utils;

import org.json.JSONObject;

public class ChargeInfo {
    private static final String JSON_KEY_CHARGED = "charged";
    private static final String JSON_KEY_CHARGING = "charging";
    private static final String JSON_KEY_DURATION = "duration";
    public long charged;
    public long charging;
    public long duration;

    public ChargeInfo() {
    }

    public ChargeInfo(long j, long j2, long j3) {
        this.charging = j;
        this.charged = j2;
        this.duration = j3;
    }

    public static ChargeInfo from(JSONObject jSONObject) {
        return new ChargeInfo(jSONObject.optLong(JSON_KEY_CHARGING), jSONObject.optLong(JSON_KEY_CHARGED), jSONObject.optLong(JSON_KEY_DURATION));
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(JSON_KEY_CHARGING, this.charging);
            jSONObject.put(JSON_KEY_CHARGED, this.charged);
            jSONObject.put(JSON_KEY_DURATION, this.duration);
            return jSONObject;
        } catch (Exception unused) {
            return null;
        }
    }
}
