package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.miui.networkassistant.config.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public class g {
    public static JSONObject a(Context context) {
        String str;
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("CKStatusBarShowSpeed", Settings.System.getInt(context.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED));
            jSONObject.put("CKStatusBarShowTraffic", Settings.System.getInt(context.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT));
        } catch (JSONException unused) {
            str = "Save settings to cloud failed. ";
        } catch (Settings.SettingNotFoundException unused2) {
            str = "Save settings to cloud failed because SettingNotFoundException. ";
        }
        return jSONObject;
        Log.v("NACloudBackupHelper", str);
        return jSONObject;
    }

    public static void a(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            if (jSONObject.has("CKStatusBarShowSpeed")) {
                Settings.System.putInt(context.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, jSONObject.optInt("CKStatusBarShowSpeed"));
            }
            if (jSONObject.has("CKStatusBarShowTraffic")) {
                Settings.System.putInt(context.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, jSONObject.optInt("CKStatusBarShowTraffic"));
            }
        }
    }
}
