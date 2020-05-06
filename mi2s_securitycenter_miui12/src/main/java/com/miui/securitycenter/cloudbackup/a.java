package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.util.Log;
import com.miui.appmanager.i;
import org.json.JSONException;
import org.json.JSONObject;

public class a {
    public static JSONObject a(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            i iVar = new i(context);
            jSONObject.put("app_update_notify", iVar.e());
            jSONObject.put("am_recommend_toogle", iVar.b());
        } catch (JSONException e) {
            Log.e("AMAndOMSettingsBackup", "save app update notify error", e);
        }
        return jSONObject;
    }

    public static void a(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            i iVar = new i(context);
            if (jSONObject.has("app_update_notify")) {
                iVar.d(jSONObject.optBoolean("app_update_notify"));
            }
            if (jSONObject.has("am_recommend_toogle")) {
                iVar.a(jSONObject.optBoolean("am_recommend_toogle"));
            }
        }
    }
}
