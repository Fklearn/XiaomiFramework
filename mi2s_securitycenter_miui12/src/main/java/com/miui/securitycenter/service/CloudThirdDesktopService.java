package com.miui.securitycenter.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import b.b.c.h.j;
import b.b.c.j.x;
import com.miui.luckymoney.config.Constants;
import com.miui.securityscan.M;
import com.miui.securityscan.i.k;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class CloudThirdDesktopService extends IntentService {
    public CloudThirdDesktopService() {
        super("CloudTDService");
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(@Nullable Intent intent) {
        String str;
        HashMap hashMap = new HashMap();
        long j = M.j();
        hashMap.put(Constants.JSON_KEY_DATA_VERSION, String.valueOf(j));
        hashMap.put("AuthVersion", String.valueOf(x.e(this, "com.lbe.security.miui")));
        try {
            JSONObject jSONObject = new JSONObject(k.a((Map<String, String>) hashMap, "https://api.sec.miui.com/desktopCtrl/getApps", "5cdd8678-cddf-4269-ab73-48387445bba9", new j("securitycenter_cloudtdservice")));
            long j2 = jSONObject.getLong("d");
            int i = jSONObject.getInt("mode");
            if (j2 > j) {
                M.b(i);
                JSONArray jSONArray = jSONObject.getJSONArray("appList");
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    JSONObject optJSONObject = jSONArray.optJSONObject(i2);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("package_name", optJSONObject.optString("pn"));
                    contentValues.put("application_name", optJSONObject.optString("an"));
                    contentValues.put("type", Integer.valueOf(optJSONObject.optInt("type")));
                    arrayList.add(contentValues);
                }
                if (arrayList.size() == getContentResolver().bulkInsert(Uri.parse("content://com.miui.sec.THIRD_DESKTOP"), (ContentValues[]) arrayList.toArray(new ContentValues[0]))) {
                    Log.i("CloudTDService", "sync success!");
                    M.f(j2);
                    return;
                }
                str = "sync failed!";
            } else {
                str = "no need to update";
            }
            Log.i("CloudTDService", str);
        } catch (Exception e) {
            Log.e("CloudTDService", "resolve cloud data exception!", e);
        }
    }
}
