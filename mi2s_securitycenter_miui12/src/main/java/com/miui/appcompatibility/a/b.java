package com.miui.appcompatibility.a;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.appcompatibility.d;
import com.miui.appcompatibility.data.AppCompatibilityData;
import com.miui.appcompatibility.data.PackageData;
import com.miui.appcompatibility.n;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class b extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f3070a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3071b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Context f3072c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ c f3073d;

    b(c cVar, String str, String str2, Context context) {
        this.f3073d = cVar;
        this.f3070a = str;
        this.f3071b = str2;
        this.f3072c = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        PackageData packageData;
        HashMap hashMap = new HashMap();
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("pkg", this.f3070a);
            jSONObject.put("ver", this.f3071b);
            jSONArray.put(0, jSONObject);
            Log.d("AppCompatStateReceiver", "json params=" + jSONArray.toString());
            hashMap.put("pkgs", jSONArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppCompatibilityData b2 = n.b(hashMap);
        if (b2 == null || b2.getPkgs() == null || b2.getPkgs().size() <= 0 || (packageData = b2.getPkgs().get(0)) == null || packageData.getStatus() == 0) {
            return null;
        }
        d.b(this.f3072c).a(packageData.getPkg(), packageData.getVer(), packageData.getStatus());
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        super.onPostExecute(voidR);
    }
}
