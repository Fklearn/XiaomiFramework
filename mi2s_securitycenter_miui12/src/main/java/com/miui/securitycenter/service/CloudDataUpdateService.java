package com.miui.securitycenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import b.b.c.h.j;
import com.miui.luckymoney.config.Constants;
import com.miui.powercenter.provider.PowerSaveService;
import com.miui.securitycenter.h;
import com.miui.securityscan.M;
import com.miui.securityscan.d.c;
import com.miui.securityscan.i.k;
import java.util.HashMap;
import java.util.Map;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CloudDataUpdateService extends IntentService {

    /* renamed from: a  reason: collision with root package name */
    private static final String f7515a = "CloudDataUpdateService";

    public CloudDataUpdateService() {
        super(f7515a);
    }

    private boolean a() {
        Log.i(f7515a, "updateNoKillPkgList start");
        if (!h.i()) {
            return false;
        }
        long g = M.g();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("ver", g);
            jSONObject.put(Constants.JSON_KEY_IS_DIFF, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap hashMap = new HashMap();
        hashMap.put("param", Base64.encodeToString(jSONObject.toString().getBytes(), 0));
        StringBuilder sb = new StringBuilder();
        sb.append(Build.IS_INTERNATIONAL_BUILD ? "https://api.sec.intl.miui.com/" : "https://api.sec.miui.com/");
        sb.append("trashCleaner/screenLock");
        String a2 = k.a((Map<String, String>) hashMap, sb.toString(), "5cdd8678-cddf-4269-ab73-48387445bba6", new j("securitycenter_updatenokillpkglist"));
        String str = f7515a;
        Log.i(str, "response: " + a2);
        if (TextUtils.isEmpty(a2)) {
            return false;
        }
        try {
            JSONObject jSONObject2 = new JSONObject(a2);
            if (jSONObject2.has("code")) {
                return false;
            }
            long optLong = jSONObject2.optLong("version");
            M.e(optLong);
            String str2 = f7515a;
            Log.i(str2, "new ver: " + optLong);
            JSONArray optJSONArray = jSONObject2.optJSONArray("items");
            c a3 = c.a(getApplicationContext());
            int length = optJSONArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                String optString = optJSONObject.optString("package");
                int optInt = optJSONObject.optInt("status");
                if (optInt == 1) {
                    a3.b(optString);
                } else if (optInt == 0) {
                    a3.a(optString);
                }
            }
            a3.d();
            return true;
        } catch (Exception e2) {
            Log.e(f7515a, "exception when update cloud data:", e2);
        }
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        if (a()) {
            Log.i(f7515a, "send updated intent");
            Intent intent2 = new Intent(this, PowerSaveService.class);
            intent2.setAction("com.miui.powercenter.action.CLEAN_CLOUD_WHITE_LIST_UPDATED");
            startService(intent2);
        }
    }
}
