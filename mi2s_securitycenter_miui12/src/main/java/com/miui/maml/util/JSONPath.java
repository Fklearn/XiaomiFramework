package com.miui.maml.util;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONPath {
    private static final String LOG_TAG = "JSONPath";
    private JSONObject mRoot;
    private JSONArray mRootArray;

    public JSONPath(JSONArray jSONArray) {
        this.mRootArray = jSONArray;
    }

    public JSONPath(JSONObject jSONObject) {
        this.mRoot = jSONObject;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0071, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object get(java.lang.String r11) {
        /*
            r10 = this;
            java.lang.String r0 = "JSONPath"
            boolean r1 = android.text.TextUtils.isEmpty(r11)
            r2 = 0
            if (r1 == 0) goto L_0x000a
            return r2
        L_0x000a:
            java.lang.String r1 = "/"
            java.lang.String[] r11 = r11.split(r1)
            org.json.JSONObject r1 = r10.mRoot
            if (r1 == 0) goto L_0x0015
            goto L_0x0017
        L_0x0015:
            org.json.JSONArray r1 = r10.mRootArray
        L_0x0017:
            if (r1 != 0) goto L_0x001a
            return r2
        L_0x001a:
            r3 = 0
            r4 = r1
            r1 = r3
        L_0x001d:
            int r5 = r11.length     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            if (r1 >= r5) goto L_0x0071
            r5 = r11[r1]     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            boolean r6 = android.text.TextUtils.isEmpty(r5)     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            if (r6 == 0) goto L_0x0029
            goto L_0x006d
        L_0x0029:
            java.lang.String r6 = "["
            int r6 = r5.indexOf(r6)     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            r7 = -1
            if (r6 == r7) goto L_0x0047
            int r8 = r6 + 1
            int r9 = r5.length()     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            int r9 = r9 + -1
            java.lang.String r8 = r5.substring(r8, r9)     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            java.lang.String r5 = r5.substring(r3, r6)     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            goto L_0x0048
        L_0x0047:
            r8 = r7
        L_0x0048:
            boolean r6 = r4 instanceof org.json.JSONObject     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            if (r6 == 0) goto L_0x0058
            boolean r6 = android.text.TextUtils.isEmpty(r5)     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            if (r6 != 0) goto L_0x0058
            org.json.JSONObject r4 = (org.json.JSONObject) r4     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            java.lang.Object r4 = r4.get(r5)     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
        L_0x0058:
            boolean r5 = r4 instanceof org.json.JSONArray     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            if (r5 == 0) goto L_0x0066
            r5 = r4
            org.json.JSONArray r5 = (org.json.JSONArray) r5     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            if (r8 != r7) goto L_0x0062
            goto L_0x0071
        L_0x0062:
            java.lang.Object r4 = r5.get(r8)     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
        L_0x0066:
            if (r4 == 0) goto L_0x0070
            java.lang.Object r5 = org.json.JSONObject.NULL     // Catch:{ JSONException -> 0x0078, Exception -> 0x0072 }
            if (r4 != r5) goto L_0x006d
            goto L_0x0070
        L_0x006d:
            int r1 = r1 + 1
            goto L_0x001d
        L_0x0070:
            return r2
        L_0x0071:
            return r4
        L_0x0072:
            r11 = move-exception
            java.lang.String r11 = r11.toString()
            goto L_0x007d
        L_0x0078:
            r11 = move-exception
            java.lang.String r11 = r11.toString()
        L_0x007d:
            android.util.Log.d(r0, r11)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.JSONPath.get(java.lang.String):java.lang.Object");
    }
}
