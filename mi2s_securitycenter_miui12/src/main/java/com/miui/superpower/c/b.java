package com.miui.superpower.c;

import android.util.Log;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class b extends d {
    public b(String str) {
        super(str);
    }

    private ArrayList<String> b(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                if (jSONArray.get(i) != null) {
                    arrayList.add(jSONArray.get(i).toString());
                }
            }
        } catch (JSONException e) {
            Log.d("SuperPowerSaveManager", "jsonArray解析失败" + e);
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public void a(JSONObject jSONObject) {
        super.a(jSONObject);
        if (a()) {
            JSONArray optJSONArray = jSONObject.optJSONArray("hiddenPackageName");
            if (optJSONArray != null) {
                com.miui.common.persistence.b.b("pref_key_superpower_cloud_hidden_packagename", b(optJSONArray.toString()));
            }
            JSONArray optJSONArray2 = jSONObject.optJSONArray("whitePackageName");
            if (optJSONArray2 != null) {
                com.miui.common.persistence.b.b("pref_key_superpower_cloud_white_processname", b(optJSONArray2.toString()));
            }
            JSONArray optJSONArray3 = jSONObject.optJSONArray("blackPackageName");
            if (optJSONArray3 != null) {
                com.miui.common.persistence.b.b("pref_key_superpower_cloud_black_processname", b(optJSONArray3.toString()));
            }
        }
    }
}
