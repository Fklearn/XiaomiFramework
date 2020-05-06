package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.miui.gamebooster.m.C0384o;
import com.miui.powercenter.autotask.C0489s;
import com.miui.powercenter.y;
import org.json.JSONObject;

public class h {
    public static JSONObject a(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("CKMemoryCleanTime", y.l());
            jSONObject.put("CKDisableMobileDataTime", y.i());
            jSONObject.put("CKOnTimeBootEnabled", y.m());
            jSONObject.put("CKOnTimeBootTime", y.o());
            jSONObject.put("CKOnTimeBootRepeatType", y.n());
            jSONObject.put("CKOnTimeBootTimeSaved", y.p());
            jSONObject.put("CKOnTimeShutdownEnabled", y.r());
            jSONObject.put("CKOnTimeShutdownTime", y.t());
            jSONObject.put("CKOnTimeShutdownRepeatType", y.s());
            jSONObject.put("CKOnTimeShutdownTimeSaved", y.q());
            jSONObject.put("CKBatteryOverHeatTemperature", y.e());
            jSONObject.put("CKBatteryConsumeAbnormal", y.x());
            jSONObject.put("CKPowerSaveAlarmEnabled", y.u());
            jSONObject.put("CKPowerSaveOpenTime", y.w());
            jSONObject.put("CKPowerSaveCloseTime", y.v());
            jSONObject.put("CKPowerSaveAutoTask", C0489s.b(context));
        } catch (Exception e) {
            Log.v("PowerSettingsCloudBackupHelper", "Save settings to cloud failed. " + e);
        }
        try {
            jSONObject.put("CKPowerSettingsStatusBarStyle", Settings.System.getInt(context.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$System", "BATTERY_INDICATOR_STYLE")));
        } catch (Exception unused) {
            Log.v("PowerSettingsCloudBackupHelper", "Save settings to cloud failed because SettingNotFoundException.");
        }
        return jSONObject;
    }

    public static void a(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            if (jSONObject.has("CKPowerSettingsStatusBarStyle")) {
                Settings.System.putInt(context.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$System", "BATTERY_INDICATOR_STYLE"), jSONObject.optInt("CKPowerSettingsStatusBarStyle"));
            }
            if (jSONObject.has("CKMemoryCleanTime")) {
                y.d(jSONObject.optInt("CKMemoryCleanTime"));
            }
            if (jSONObject.has("CKDisableMobileDataTime")) {
                y.c(jSONObject.optInt("CKDisableMobileDataTime"));
            }
            if (jSONObject.has("CKOnTimeBootEnabled")) {
                y.d(jSONObject.optBoolean("CKOnTimeBootEnabled"));
            }
            if (jSONObject.has("CKOnTimeBootTime")) {
                y.f(jSONObject.optInt("CKOnTimeBootTime"));
            }
            if (jSONObject.has("CKOnTimeBootRepeatType")) {
                y.e(jSONObject.optInt("CKOnTimeBootRepeatType"));
            }
            if (jSONObject.has("CKOnTimeBootTimeSaved")) {
                y.f(jSONObject.optLong("CKOnTimeBootTimeSaved"));
            }
            if (jSONObject.has("CKOnTimeShutdownEnabled")) {
                y.e(jSONObject.optBoolean("CKOnTimeShutdownEnabled"));
            }
            if (jSONObject.has("CKOnTimeShutdownTime")) {
                y.h(jSONObject.optInt("CKOnTimeShutdownTime"));
            }
            if (jSONObject.has("CKOnTimeShutdownRepeatType")) {
                y.g(jSONObject.optInt("CKOnTimeShutdownRepeatType"));
            }
            if (jSONObject.has("CKOnTimeShutdownTimeSaved")) {
                y.g(jSONObject.optLong("CKOnTimeShutdownTimeSaved"));
            }
            if (jSONObject.has("CKBatteryOverHeatTemperature")) {
                y.b(jSONObject.optInt("CKBatteryOverHeatTemperature"));
            }
            if (jSONObject.has("CKBatteryConsumeAbnormal")) {
                y.g(jSONObject.optBoolean("CKBatteryConsumeAbnormal"));
            }
            if (jSONObject.has("CKPowerSaveAlarmEnabled")) {
                y.f(jSONObject.optBoolean("CKPowerSaveAlarmEnabled"));
            }
            if (jSONObject.has("CKPowerSaveOpenTime")) {
                y.j(jSONObject.optInt("CKPowerSaveOpenTime"));
            }
            if (jSONObject.has("CKPowerSaveCloseTime")) {
                y.i(jSONObject.optInt("CKPowerSaveCloseTime"));
            }
            if (jSONObject.has("CKPowerSaveAutoTask")) {
                C0489s.a(context, jSONObject.optJSONArray("CKPowerSaveAutoTask"));
            }
        }
    }
}
