package com.miui.luckymoney.webapi;

import b.b.c.g.b;
import org.json.JSONObject;

public class RequestResult implements b {
    private static final String TAG = "RequestResult";
    protected boolean DEBUG = false;
    protected boolean isSuccess;
    protected String mJsonStr;

    public RequestResult() {
    }

    public RequestResult(String str) {
        this.mJsonStr = str;
        parseJson(str);
    }

    /* access modifiers changed from: protected */
    public void doParseJson(JSONObject jSONObject) {
    }

    public String getJson() {
        return this.mJsonStr;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x003d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean parseJson(java.lang.String r5) {
        /*
            r4 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r5)
            r1 = 0
            if (r0 == 0) goto L_0x000a
            r4.isSuccess = r1
            return r1
        L_0x000a:
            r4.mJsonStr = r5
            boolean r0 = r4.DEBUG
            java.lang.String r2 = "RequestResult"
            if (r0 == 0) goto L_0x0015
            android.util.Log.d(r2, r5)
        L_0x0015:
            r0 = 0
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x002f }
            r3.<init>(r5)     // Catch:{ JSONException -> 0x002f }
            java.lang.String r5 = "data"
            org.json.JSONArray r5 = r3.getJSONArray(r5)     // Catch:{ JSONException -> 0x002c }
            org.json.JSONObject r0 = r5.getJSONObject(r1)     // Catch:{ JSONException -> 0x002c }
            java.lang.String r5 = "productData"
            org.json.JSONObject r5 = r0.getJSONObject(r5)     // Catch:{ JSONException -> 0x002f }
            goto L_0x0038
        L_0x002c:
            r5 = move-exception
            r0 = r3
            goto L_0x0030
        L_0x002f:
            r5 = move-exception
        L_0x0030:
            r4.isSuccess = r1
            java.lang.String r3 = "parse json failed :"
            android.util.Log.d(r2, r3, r5)
            r5 = r0
        L_0x0038:
            if (r5 != 0) goto L_0x003d
            r4.isSuccess = r1
            return r1
        L_0x003d:
            r0 = 1
            r4.isSuccess = r0
            r4.doParseJson(r5)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.luckymoney.webapi.RequestResult.parseJson(java.lang.String):boolean");
    }

    public String toJson() {
        return null;
    }
}
