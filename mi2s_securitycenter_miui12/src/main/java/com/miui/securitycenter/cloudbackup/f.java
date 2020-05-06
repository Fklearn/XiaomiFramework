package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.util.Log;
import com.miui.gamebooster.c.a;
import org.json.JSONException;
import org.json.JSONObject;

public class f {
    public static JSONObject a(Context context) {
        JSONObject jSONObject = new JSONObject();
        a.a(context);
        try {
            jSONObject.put("CKGameBox", a.a(true));
            jSONObject.put("CKSlipBox", a.w(true));
            jSONObject.put("CKPerformanceBooster", a.e(true));
            jSONObject.put("CKNetBooster", a.o(true));
            jSONObject.put("CKHandsFree", a.l(true));
            jSONObject.put("CKFunctionShield", a.i(true));
            jSONObject.put("CKShieldKeyboard", a.d(true));
            jSONObject.put("CKShieldAutoBright", a.r(false));
            jSONObject.put("CKShieldEyeShield", a.s(false));
            jSONObject.put("CKShieldThreeFinger", a.u(false));
            jSONObject.put("CKShieldPullNotificationBar", a.t(false));
            jSONObject.put("CKShieldDisableVoiceTrigger", a.g(false));
            jSONObject.put("CKShieldNum", a.a(0));
        } catch (JSONException unused) {
            Log.v("GameBoosterSettingsCloudBackupHelper", "Save settings to cloud failed. ");
        }
        return jSONObject;
    }

    public static void a(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            a.a(context);
            if (jSONObject.has("CKGameBox")) {
                a.M(jSONObject.optBoolean("CKGameBox"));
            }
            if (jSONObject.has("CKSlipBox")) {
                a.ca(jSONObject.optBoolean("CKSlipBox"));
            }
            if (jSONObject.has("CKPerformanceBooster")) {
                a.D(jSONObject.optBoolean("CKPerformanceBooster"));
            }
            if (jSONObject.has("CKNetBooster")) {
                a.T(jSONObject.optBoolean("CKNetBooster"));
            }
            if (jSONObject.has("CKHandsFree")) {
                a.N(jSONObject.optBoolean("CKHandsFree"));
            }
            if (jSONObject.has("CKFunctionShield")) {
                a.J(jSONObject.optBoolean("CKFunctionShield"));
            }
            if (jSONObject.has("CKShieldKeyboard")) {
                a.B(jSONObject.optBoolean("CKShieldKeyboard"));
            }
            if (jSONObject.has("CKShieldAutoBright")) {
                a.X(jSONObject.optBoolean("CKShieldAutoBright"));
            }
            if (jSONObject.has("CKShieldEyeShield")) {
                a.Y(jSONObject.optBoolean("CKShieldEyeShield"));
            }
            if (jSONObject.has("CKShieldThreeFinger")) {
                a.aa(jSONObject.optBoolean("CKShieldThreeFinger"));
            }
            if (jSONObject.has("CKShieldPullNotificationBar")) {
                a.Z(jSONObject.optBoolean("CKShieldPullNotificationBar"));
            }
            if (jSONObject.has("CKShieldDisableVoiceTrigger")) {
                a.F(jSONObject.optBoolean("CKShieldDisableVoiceTrigger"));
            }
            if (jSONObject.has("CKShieldNum")) {
                a.b(jSONObject.optInt("CKShieldNum"));
            }
        }
    }
}
