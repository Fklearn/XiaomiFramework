package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.b.b;
import b.b.b.d.a;
import b.b.b.o;
import b.b.b.p;
import com.miui.antivirus.model.e;
import com.miui.antivirus.whitelist.j;
import com.miui.guardprovider.b;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class d {
    public static JSONObject a(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("CKVirusEngine", p.a());
            jSONObject.put("CKVirusAutoUpdateTencent", p.a("key_database_auto_update_enabled_TENCENT"));
            jSONObject.put("CKVirusAutoUpdateAvast", p.a("key_database_auto_update_enabled_Avast"));
            jSONObject.put("CKVirusAutoUpdateAVL", p.a("key_database_auto_update_enabled_AVL"));
            j a2 = j.a(context.getApplicationContext());
            JSONArray jSONArray = new JSONArray();
            List<j.c> b2 = a2.b();
            if (b2 != null) {
                if (!b2.isEmpty()) {
                    for (j.c next : b2) {
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("scanItemType", next.f3055a);
                        jSONObject2.put("scanVirusType", next.f3056b);
                        jSONObject2.put("appLabel", next.f3057c);
                        jSONObject2.put("desc", next.f3058d);
                        jSONObject2.put("dirPath", next.e);
                        jSONObject2.put("pkgName", next.f);
                        jSONObject2.put("virusName", next.g);
                        jSONObject2.put("md5", next.h);
                        jSONArray.put(jSONObject2);
                    }
                }
            }
            jSONObject.put("CKVirusWhiteListTrojan", jSONArray);
            JSONArray jSONArray2 = new JSONArray();
            List<j.b> a3 = a2.a();
            if (a3 != null && !a3.isEmpty()) {
                for (j.b next2 : a3) {
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put("scanItemType", next2.f3051a);
                    jSONObject3.put("scanVirusType", next2.f3052b);
                    jSONObject3.put("appLabel", next2.f3053c);
                    jSONObject3.put("desc", next2.f3054d);
                    jSONObject3.put("dirPath", next2.e);
                    jSONObject3.put("pkgName", next2.f);
                    jSONObject3.put("virusName", next2.g);
                    jSONObject3.put("md5", next2.h);
                    jSONArray2.put(jSONObject3);
                }
            }
            jSONObject.put("CKVirusWhiteListRisk", jSONArray2);
            ArrayList arrayList = new ArrayList(p.g());
            JSONArray jSONArray3 = new JSONArray();
            if (!arrayList.isEmpty()) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    String str = (String) it.next();
                    if (!TextUtils.isEmpty(str)) {
                        jSONArray3.put(str);
                    }
                }
            }
            jSONObject.put("CKVirusOfficialList", jSONArray3);
            jSONObject.put("CKVirusCloudScan", p.n());
            jSONObject.put("CKInstallMonitor", a.a(context.getApplicationContext()));
        } catch (JSONException unused) {
            Log.v("AntivirusBackupHelper", "Save settings to cloud failed. ");
        }
        return jSONObject;
    }

    public static void a(Context context, JSONObject jSONObject) {
        JSONArray optJSONArray;
        JSONArray optJSONArray2;
        JSONArray optJSONArray3;
        if (jSONObject != null) {
            if (jSONObject.has("CKVirusCloudScan")) {
                p.g(jSONObject.optBoolean("CKVirusCloudScan"));
            }
            if (jSONObject.has("CKInstallMonitor")) {
                a.a(context.getApplicationContext(), jSONObject.optBoolean("CKInstallMonitor"));
            }
            if (jSONObject.has("CKVirusEngine")) {
                String optString = jSONObject.optString("CKVirusEngine");
                p.b(jSONObject.optString("CKVirusEngine"));
                List<b.a> d2 = b.a(context).d();
                com.miui.guardprovider.b a2 = com.miui.guardprovider.b.a(context);
                a2.a((b.a) new c(d2, optString, a2));
            }
            if (jSONObject.has("CKVirusAutoUpdateTencent")) {
                p.a("key_database_auto_update_enabled_TENCENT", jSONObject.optBoolean("CKVirusAutoUpdateTencent"));
            }
            if (jSONObject.has("CKVirusAutoUpdateAVL")) {
                p.a("key_database_auto_update_enabled_AVL", jSONObject.optBoolean("CKVirusAutoUpdateAVL"));
            }
            if (jSONObject.has("CKVirusAutoUpdateAvast")) {
                p.a("key_database_auto_update_enabled_Avast", jSONObject.optBoolean("CKVirusAutoUpdateAvast"));
            }
            j a3 = j.a(context.getApplicationContext());
            if (jSONObject.has("CKVirusWhiteListTrojan") && (optJSONArray3 = jSONObject.optJSONArray("CKVirusWhiteListTrojan")) != null) {
                for (int i = 0; i < optJSONArray3.length(); i++) {
                    JSONObject optJSONObject = optJSONArray3.optJSONObject(i);
                    if (optJSONObject != null) {
                        e eVar = new e();
                        eVar.a(o.g.VIRUS);
                        eVar.a("INSTALLED_APP".equals(optJSONObject.optString("scanItemType")) ? o.f.INSTALLED_APP : o.f.UNINSTALLED_APK);
                        eVar.b(optJSONObject.optString("appLabel"));
                        eVar.i(optJSONObject.optString("desc"));
                        eVar.g(optJSONObject.optString("dirPath"));
                        eVar.f(optJSONObject.optString("pkgName"));
                        eVar.j(optJSONObject.optString("virusName"));
                        eVar.e(optJSONObject.optString("md5"));
                        a3.b(eVar);
                    }
                }
            }
            if (jSONObject.has("CKVirusWhiteListRisk") && (optJSONArray2 = jSONObject.optJSONArray("CKVirusWhiteListRisk")) != null) {
                for (int i2 = 0; i2 < optJSONArray2.length(); i2++) {
                    JSONObject optJSONObject2 = optJSONArray2.optJSONObject(i2);
                    if (optJSONObject2 != null) {
                        e eVar2 = new e();
                        eVar2.a(o.g.RISK);
                        eVar2.a("INSTALLED_APP".equals(optJSONObject2.optString("scanItemType")) ? o.f.INSTALLED_APP : o.f.UNINSTALLED_APK);
                        eVar2.b(optJSONObject2.optString("appLabel"));
                        eVar2.i(optJSONObject2.optString("desc"));
                        eVar2.g(optJSONObject2.optString("dirPath"));
                        eVar2.f(optJSONObject2.optString("pkgName"));
                        eVar2.j(optJSONObject2.optString("virusName"));
                        eVar2.e(optJSONObject2.optString("md5"));
                        a3.b(eVar2);
                    }
                }
            }
            if (jSONObject.has("CKVirusOfficialList") && (optJSONArray = jSONObject.optJSONArray("CKVirusOfficialList")) != null) {
                ArrayList arrayList = new ArrayList(p.g());
                for (int i3 = 0; i3 < optJSONArray.length(); i3++) {
                    String optString2 = optJSONArray.optString(i3);
                    if (!TextUtils.isEmpty(optString2) && !arrayList.contains(optString2)) {
                        arrayList.add(optString2);
                    }
                }
                p.c((ArrayList<String>) arrayList);
            }
        }
    }
}
