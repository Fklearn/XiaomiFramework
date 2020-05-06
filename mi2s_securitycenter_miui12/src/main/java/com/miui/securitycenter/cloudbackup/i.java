package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.b.p;
import com.miui.antivirus.service.GuardService;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private static final String f7475a = "i";

    public static JSONObject a(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("CKSafePaySettingsMonitor", p.j());
            jSONObject.put("CKSafePaySettingsInputMethod", p.l());
            jSONObject.put("CKSafePaySettingsCheckItemWifi", p.p());
            jSONObject.put("CKSafePaySettingsCheckItemRoot", p.k());
            jSONObject.put("CKSafePaySettingsCheckItemUpdate", p.m());
        } catch (JSONException e) {
            Log.e(f7475a, "exception when saveToCloud ", e);
        }
        return jSONObject;
    }

    public static void a(Context context, JSONObject jSONObject) {
        boolean optBoolean;
        if (jSONObject != null) {
            if (jSONObject.has("CKSafePaySettingsMonitor") && (optBoolean = jSONObject.optBoolean("CKSafePaySettingsMonitor")) != p.j() && !Build.IS_INTERNATIONAL_BUILD) {
                Intent intent = new Intent(context, GuardService.class);
                intent.setAction(optBoolean ? "action_register_foreground_notification" : "action_unregister_foreground_notification");
                context.startService(intent);
            }
            if (jSONObject.has("CKSafePaySettingsInputMethod")) {
                p.e(jSONObject.optBoolean("CKSafePaySettingsInputMethod"));
            }
            if (jSONObject.has("CKSafePaySettingsCheckItemWifi")) {
                p.i(jSONObject.optBoolean("CKSafePaySettingsCheckItemWifi"));
            }
            if (jSONObject.has("CKSafePaySettingsCheckItemRoot")) {
                p.d(jSONObject.optBoolean("CKSafePaySettingsCheckItemRoot"));
            }
            if (jSONObject.has("CKSafePaySettingsCheckItemUpdate")) {
                p.f(jSONObject.optBoolean("CKSafePaySettingsCheckItemUpdate"));
            }
        }
    }
}
