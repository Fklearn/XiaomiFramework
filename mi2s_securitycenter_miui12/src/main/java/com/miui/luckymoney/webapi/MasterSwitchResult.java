package com.miui.luckymoney.webapi;

import android.util.Log;
import b.b.c.c.d;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.CommonPerConstants;
import com.miui.luckymoney.config.Constants;
import org.json.JSONObject;

public class MasterSwitchResult extends RequestResult {
    private static String TAG = "MasterSwitchResult";
    private boolean alarmSwitch;
    private long defaultFrequency;
    private long endTime;
    private boolean floatSwitch;
    private long hotFrequency;
    private boolean masterSwitch;
    private long startTime;

    public MasterSwitchResult(String str) {
        super(str);
    }

    /* access modifiers changed from: protected */
    public void doParseJson(JSONObject jSONObject) {
        super.doParseJson(jSONObject);
        if (isSuccess()) {
            if (this.DEBUG) {
                Log.d(TAG, jSONObject.toString());
            }
            this.masterSwitch = jSONObject.optBoolean("masterSwitch", true);
            this.floatSwitch = jSONObject.optBoolean(Constants.JSON_KEY_FLOAT_SWITCH, true);
            this.alarmSwitch = jSONObject.optBoolean(Constants.JSON_KEY_ALARM_SWITCH, true);
            this.defaultFrequency = jSONObject.optLong(Constants.JSON_KEY_DEFAULT_FREQUENCY, CommonPerConstants.DEFAULT.DEFAULT_UPDATE_FREQUENCY_DEFAULT);
            this.startTime = jSONObject.optLong("startTime", 0);
            this.endTime = jSONObject.optLong("endTime", 1);
            this.hotFrequency = jSONObject.optLong(Constants.JSON_KEY_HOT_FREQUENCY, 21600000);
            saveToLocal();
        }
    }

    public void saveToLocal() {
        if (isSuccess()) {
            CommonConfig instance = CommonConfig.getInstance(d.a());
            instance.setMasterSwitchConfig(getJson());
            boolean z = this.masterSwitch;
            if (!z) {
                instance.setXiaomiLuckyMoneyEnable(z);
            }
            boolean z2 = this.floatSwitch;
            if (!z2) {
                instance.setDesktopFloatWindowEnable(z2);
            }
            boolean z3 = this.alarmSwitch;
            if (!z3) {
                instance.setLuckyAlarmEnable(z3);
            }
            instance.setDefaultUpdateFrequency(Math.min(this.defaultFrequency, 1296000000));
            instance.setHotStartTime(this.startTime);
            instance.setHotEndTime(this.endTime);
            instance.setHotFrequency(Math.min(this.hotFrequency, 1296000000));
        }
    }
}
