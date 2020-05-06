package com.miui.appcompatibility;

import android.os.AsyncTask;
import android.util.Log;
import com.miui.appcompatibility.data.AppCompatibilityData;
import com.miui.appcompatibility.data.PackageData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class b extends AsyncTask<Void, Void, Boolean> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f3074a;

    b(d dVar) {
        this.f3074a = dVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Boolean doInBackground(Void... voidArr) {
        boolean z;
        Log.d("AppCompatManager", "doInBackground()");
        HashMap hashMap = new HashMap();
        JSONArray jSONArray = new JSONArray();
        boolean z2 = true;
        try {
            List a2 = this.f3074a.d(this.f3074a.f3078c);
            if (a2.size() <= 0) {
                return this.f3074a.e >= 5;
            }
            for (int i = 0; i < a2.size(); i++) {
                PackageData packageData = (PackageData) a2.get(i);
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("pkg", packageData.getPkg());
                jSONObject.put("ver", packageData.getVer());
                jSONArray.put(i, jSONObject);
            }
            hashMap.put("pkgs", jSONArray.toString());
            AppCompatibilityData a3 = n.a((Map<String, String>) hashMap);
            if (a3 == null || a3.getPkgs() == null) {
                z = false;
            } else {
                Log.d("AppCompatManager", "-onPostExecute()");
                this.f3074a.a(a3.getPkgs());
                z = true;
            }
            if (!z && this.f3074a.e < 5) {
                z2 = false;
            }
            return Boolean.valueOf(z2);
        } catch (JSONException e) {
            Log.d("AppCompatManager", "JSONException:" + e.getMessage());
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        if (bool.booleanValue()) {
            this.f3074a.j();
        }
    }
}
