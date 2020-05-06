package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.util.Log;
import com.miui.monthreport.g;
import com.miui.permcenter.n;
import com.miui.securitycenter.h;
import com.miui.securityscan.M;
import com.miui.securityscan.i.m;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class j {
    public static JSONObject a(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("CKShowNotification", h.a(context.getContentResolver()));
            jSONObject.put("CKConnectNetworkAllowed", h.i());
            jSONObject.put("CKWhiteList", b(context));
            jSONObject.put("CKMonthReport", g.b());
            jSONObject.put("CKNewsOnlyWlan", M.l());
            jSONObject.put("CKNewsRecommend", M.m());
        } catch (JSONException unused) {
            Log.v("SecurityCenterSettingsCloudBackupHelper", "Save settings to cloud failed. ");
        }
        return jSONObject;
    }

    private static void a(Context context, JSONArray jSONArray) {
        if (jSONArray != null) {
            m.a();
            int length = jSONArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject optJSONObject = jSONArray.optJSONObject(i);
                if (optJSONObject != null) {
                    try {
                        m.a(optJSONObject.getString("WLType"));
                    } catch (JSONException unused) {
                        Log.v("SecurityCenterSettingsCloudBackupHelper", "Load white list item from JSON failed: bad JSON.  ");
                    }
                }
            }
        }
    }

    public static void a(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            if (jSONObject.has("CKShowNotification")) {
                h.f(context, jSONObject.optBoolean("CKShowNotification"));
            }
            if (jSONObject.has("CKConnectNetworkAllowed")) {
                h.b(jSONObject.optBoolean("CKConnectNetworkAllowed"));
                n.d(context);
            }
            if (jSONObject.has("CKWhiteList")) {
                a(context, jSONObject.optJSONArray("CKWhiteList"));
            }
            if (jSONObject.has("CKMonthReport")) {
                g.a(jSONObject.optBoolean("CKMonthReport"));
            }
            if (jSONObject.has("CKNewsOnlyWlan")) {
                M.c(jSONObject.optBoolean("CKNewsOnlyWlan"));
            }
            if (jSONObject.has("CKNewsRecommend")) {
                M.d(jSONObject.optBoolean("CKNewsRecommend"));
            }
        }
    }

    private static JSONArray b(Context context) {
        JSONArray jSONArray = new JSONArray();
        List<String> b2 = m.b();
        if (b2 != null) {
            for (String next : b2) {
                JSONObject jSONObject = new JSONObject();
                jSONArray.put(jSONObject);
                try {
                    jSONObject.put("WLType", next);
                } catch (JSONException unused) {
                    Log.v("SecurityCenterSettingsCloudBackupHelper", "Get white list JSON failed. ");
                }
            }
        }
        return jSONArray;
    }
}
