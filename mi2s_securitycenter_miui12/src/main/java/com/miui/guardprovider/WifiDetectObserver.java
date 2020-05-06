package com.miui.guardprovider;

import android.content.Context;
import b.b.b.d.o;
import com.miui.guardprovider.aidl.IWifiDetectObserver;
import org.json.JSONObject;

public class WifiDetectObserver extends IWifiDetectObserver.Stub {

    /* renamed from: a  reason: collision with root package name */
    private static int f5462a = 5;

    /* renamed from: b  reason: collision with root package name */
    private o f5463b;

    /* renamed from: c  reason: collision with root package name */
    private JSONObject f5464c = new JSONObject();

    /* renamed from: d  reason: collision with root package name */
    private Context f5465d;

    public WifiDetectObserver(Context context) {
        this.f5465d = context;
    }

    public void a(o oVar) {
        this.f5463b = oVar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0096 A[Catch:{ JSONException -> 0x0074 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void o(int r9) {
        /*
            r8 = this;
            r0 = -3
            java.lang.String r1 = "WifiDetectObserver"
            java.lang.String r2 = "wifi_item_dns"
            java.lang.String r3 = "wifi_item_fake"
            r4 = 0
            if (r9 == r0) goto L_0x0082
            r0 = -2
            java.lang.String r5 = "wifi_item_encryption"
            if (r9 == r0) goto L_0x007c
            java.lang.String r0 = "wifi_item_connection"
            r6 = 1
            if (r9 == r6) goto L_0x0076
            r7 = 2
            if (r9 == r7) goto L_0x006e
            r7 = 3
            if (r9 == r7) goto L_0x0067
            r0 = 261(0x105, float:3.66E-43)
            java.lang.String r7 = "wifi_item_arp"
            if (r9 == r0) goto L_0x0061
            r0 = 262(0x106, float:3.67E-43)
            if (r9 == r0) goto L_0x005b
            switch(r9) {
                case 16: goto L_0x0082;
                case 17: goto L_0x0082;
                case 18: goto L_0x0050;
                case 19: goto L_0x0045;
                default: goto L_0x0027;
            }
        L_0x0027:
            switch(r9) {
                case 256: goto L_0x003f;
                case 257: goto L_0x007c;
                case 258: goto L_0x007c;
                case 259: goto L_0x007c;
                default: goto L_0x002a;
            }
        L_0x002a:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0074 }
            r0.<init>()     // Catch:{ JSONException -> 0x0074 }
            java.lang.String r2 = "wifi result = "
            r0.append(r2)     // Catch:{ JSONException -> 0x0074 }
            r0.append(r9)     // Catch:{ JSONException -> 0x0074 }
            java.lang.String r9 = r0.toString()     // Catch:{ JSONException -> 0x0074 }
            android.util.Log.i(r1, r9)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x003f:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r5, r6)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x0045:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r2, r4)     // Catch:{ JSONException -> 0x0074 }
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r3, r6)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x0050:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r2, r6)     // Catch:{ JSONException -> 0x0074 }
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r3, r4)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x005b:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r7, r6)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x0061:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r7, r4)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x0067:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            java.lang.String r2 = "wifi_type_approve"
            r9.put(r2, r6)     // Catch:{ JSONException -> 0x0074 }
        L_0x006e:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r0, r4)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x0074:
            r9 = move-exception
            goto L_0x00c3
        L_0x0076:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r0, r6)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x007c:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r5, r4)     // Catch:{ JSONException -> 0x0074 }
            goto L_0x008c
        L_0x0082:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r3, r4)     // Catch:{ JSONException -> 0x0074 }
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            r9.put(r2, r4)     // Catch:{ JSONException -> 0x0074 }
        L_0x008c:
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            int r9 = r9.length()     // Catch:{ JSONException -> 0x0074 }
            int r0 = f5462a     // Catch:{ JSONException -> 0x0074 }
            if (r9 < r0) goto L_0x00c6
            org.json.JSONObject r9 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            java.lang.String r9 = r9.toString()     // Catch:{ JSONException -> 0x0074 }
            b.b.b.p.c((java.lang.String) r9)     // Catch:{ JSONException -> 0x0074 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0074 }
            r9.<init>()     // Catch:{ JSONException -> 0x0074 }
            java.lang.String r0 = "update wifi result = "
            r9.append(r0)     // Catch:{ JSONException -> 0x0074 }
            org.json.JSONObject r0 = r8.f5464c     // Catch:{ JSONException -> 0x0074 }
            java.lang.String r0 = r0.toString()     // Catch:{ JSONException -> 0x0074 }
            r9.append(r0)     // Catch:{ JSONException -> 0x0074 }
            java.lang.String r9 = r9.toString()     // Catch:{ JSONException -> 0x0074 }
            android.util.Log.i(r1, r9)     // Catch:{ JSONException -> 0x0074 }
            android.content.Context r9 = r8.f5465d     // Catch:{ JSONException -> 0x0074 }
            com.miui.guardprovider.b r9 = com.miui.guardprovider.b.a((android.content.Context) r9)     // Catch:{ JSONException -> 0x0074 }
            r9.a()     // Catch:{ JSONException -> 0x0074 }
            goto L_0x00c6
        L_0x00c3:
            r9.printStackTrace()
        L_0x00c6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.guardprovider.WifiDetectObserver.o(int):void");
    }
}
