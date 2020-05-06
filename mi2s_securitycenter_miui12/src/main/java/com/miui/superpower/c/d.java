package com.miui.superpower.c;

import org.json.JSONObject;

public class d implements c {

    /* renamed from: a  reason: collision with root package name */
    protected String f8097a;

    /* renamed from: b  reason: collision with root package name */
    protected boolean f8098b;

    public d(String str) {
        this.f8097a = str;
        a(str);
    }

    /* access modifiers changed from: protected */
    public void a(JSONObject jSONObject) {
    }

    public boolean a() {
        return this.f8098b;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0060  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(java.lang.String r6) {
        /*
            r5 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r6)
            r1 = 0
            if (r0 == 0) goto L_0x000a
            r5.f8098b = r1
            return r1
        L_0x000a:
            r5.f8097a = r6
            r0 = 0
            r2 = 1
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0041 }
            r3.<init>(r6)     // Catch:{ JSONException -> 0x0041 }
            java.lang.String r6 = "data"
            org.json.JSONArray r6 = r3.getJSONArray(r6)     // Catch:{ JSONException -> 0x003e }
            org.json.JSONObject r0 = r6.getJSONObject(r1)     // Catch:{ JSONException -> 0x003e }
            int r3 = r6.length()     // Catch:{ JSONException -> 0x0041 }
            r4 = 2
            if (r3 != r4) goto L_0x0037
            java.lang.String r3 = "versionType"
            java.lang.String r3 = r0.optString(r3)     // Catch:{ JSONException -> 0x0041 }
            java.lang.String r4 = "development"
            boolean r3 = r3.equals(r4)     // Catch:{ JSONException -> 0x0041 }
            if (r3 != 0) goto L_0x0037
            org.json.JSONObject r6 = r6.getJSONObject(r2)     // Catch:{ JSONException -> 0x0041 }
            r0 = r6
        L_0x0037:
            java.lang.String r6 = "productData"
            org.json.JSONObject r6 = r0.getJSONObject(r6)     // Catch:{ JSONException -> 0x0041 }
            goto L_0x005b
        L_0x003e:
            r6 = move-exception
            r0 = r3
            goto L_0x0042
        L_0x0041:
            r6 = move-exception
        L_0x0042:
            r5.f8098b = r1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "parse json failed :"
            r3.append(r4)
            r3.append(r6)
            java.lang.String r6 = r3.toString()
            java.lang.String r3 = "SuperPowerSaveManager"
            android.util.Log.d(r3, r6)
            r6 = r0
        L_0x005b:
            if (r6 != 0) goto L_0x0060
            r5.f8098b = r1
            return r1
        L_0x0060:
            r5.f8098b = r2
            r5.a((org.json.JSONObject) r6)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.superpower.c.d.a(java.lang.String):boolean");
    }
}
