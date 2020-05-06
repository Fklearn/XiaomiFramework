package com.miui.guardprovider;

import android.content.Context;
import android.net.wifi.WifiInfo;
import b.b.b.d.o;
import com.miui.guardprovider.aidl.IWifiDetectObserver;
import org.json.JSONObject;

public class WifiCheckObserver extends IWifiDetectObserver.Stub {

    /* renamed from: a  reason: collision with root package name */
    private o f5458a;

    /* renamed from: b  reason: collision with root package name */
    private WifiInfo f5459b;

    /* renamed from: c  reason: collision with root package name */
    private JSONObject f5460c = new JSONObject();

    /* renamed from: d  reason: collision with root package name */
    private Context f5461d;

    public WifiCheckObserver(Context context) {
        this.f5461d = context;
    }

    public void a(WifiInfo wifiInfo) {
        this.f5459b = wifiInfo;
    }

    public void a(o oVar) {
        this.f5458a = oVar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0076 A[Catch:{ JSONException -> 0x00d5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void o(int r9) {
        /*
            r8 = this;
            r0 = -3
            java.lang.String r1 = "WifiCheckObserver"
            java.lang.String r2 = "wifi_item_connection"
            java.lang.String r3 = "wifi_item_encryption"
            java.lang.String r4 = "wifi_item_dns"
            java.lang.String r5 = "wifi_item_fake"
            r6 = 0
            if (r9 == r0) goto L_0x0063
            r0 = -2
            if (r9 == r0) goto L_0x005d
            r0 = 1
            if (r9 == r0) goto L_0x0057
            r7 = 2
            if (r9 == r7) goto L_0x0051
            r7 = 3
            if (r9 == r7) goto L_0x0051
            switch(r9) {
                case 16: goto L_0x0063;
                case 17: goto L_0x0063;
                case 18: goto L_0x0046;
                case 19: goto L_0x003b;
                default: goto L_0x001d;
            }
        L_0x001d:
            switch(r9) {
                case 256: goto L_0x0035;
                case 257: goto L_0x005d;
                case 258: goto L_0x005d;
                case 259: goto L_0x005d;
                default: goto L_0x0020;
            }
        L_0x0020:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x00d5 }
            r0.<init>()     // Catch:{ JSONException -> 0x00d5 }
            java.lang.String r6 = "wifi result = "
            r0.append(r6)     // Catch:{ JSONException -> 0x00d5 }
            r0.append(r9)     // Catch:{ JSONException -> 0x00d5 }
            java.lang.String r9 = r0.toString()     // Catch:{ JSONException -> 0x00d5 }
            android.util.Log.i(r1, r9)     // Catch:{ JSONException -> 0x00d5 }
            goto L_0x006d
        L_0x0035:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r3, r0)     // Catch:{ JSONException -> 0x00d5 }
            goto L_0x006d
        L_0x003b:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r4, r6)     // Catch:{ JSONException -> 0x00d5 }
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r5, r0)     // Catch:{ JSONException -> 0x00d5 }
            goto L_0x006d
        L_0x0046:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r4, r0)     // Catch:{ JSONException -> 0x00d5 }
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r5, r6)     // Catch:{ JSONException -> 0x00d5 }
            goto L_0x006d
        L_0x0051:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r2, r6)     // Catch:{ JSONException -> 0x00d5 }
            goto L_0x006d
        L_0x0057:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r2, r0)     // Catch:{ JSONException -> 0x00d5 }
            goto L_0x006d
        L_0x005d:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r3, r6)     // Catch:{ JSONException -> 0x00d5 }
            goto L_0x006d
        L_0x0063:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r5, r6)     // Catch:{ JSONException -> 0x00d5 }
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            r9.put(r4, r6)     // Catch:{ JSONException -> 0x00d5 }
        L_0x006d:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            int r9 = r9.length()     // Catch:{ JSONException -> 0x00d5 }
            r0 = 4
            if (r9 < r0) goto L_0x00d9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x00d5 }
            r9.<init>()     // Catch:{ JSONException -> 0x00d5 }
            java.lang.String r0 = "update wifi result = "
            r9.append(r0)     // Catch:{ JSONException -> 0x00d5 }
            org.json.JSONObject r0 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            java.lang.String r0 = r0.toString()     // Catch:{ JSONException -> 0x00d5 }
            r9.append(r0)     // Catch:{ JSONException -> 0x00d5 }
            java.lang.String r9 = r9.toString()     // Catch:{ JSONException -> 0x00d5 }
            android.util.Log.i(r1, r9)     // Catch:{ JSONException -> 0x00d5 }
            android.content.Context r9 = r8.f5461d     // Catch:{ JSONException -> 0x00d5 }
            com.miui.guardprovider.b r9 = com.miui.guardprovider.b.a((android.content.Context) r9)     // Catch:{ JSONException -> 0x00d5 }
            r9.a()     // Catch:{ JSONException -> 0x00d5 }
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            boolean r9 = r9.getBoolean(r2)     // Catch:{ JSONException -> 0x00d5 }
            if (r9 != 0) goto L_0x00a2
            return
        L_0x00a2:
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            boolean r9 = r9.getBoolean(r3)     // Catch:{ JSONException -> 0x00d5 }
            if (r9 != 0) goto L_0x00ba
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            boolean r9 = r9.getBoolean(r5)     // Catch:{ JSONException -> 0x00d5 }
            if (r9 != 0) goto L_0x00ba
            org.json.JSONObject r9 = r8.f5460c     // Catch:{ JSONException -> 0x00d5 }
            boolean r9 = r9.getBoolean(r4)     // Catch:{ JSONException -> 0x00d5 }
            if (r9 == 0) goto L_0x00d9
        L_0x00ba:
            android.content.Intent r9 = new android.content.Intent     // Catch:{ JSONException -> 0x00d5 }
            android.content.Context r0 = r8.f5461d     // Catch:{ JSONException -> 0x00d5 }
            java.lang.Class<com.miui.antivirus.service.DialogService> r1 = com.miui.antivirus.service.DialogService.class
            r9.<init>(r0, r1)     // Catch:{ JSONException -> 0x00d5 }
            java.lang.String r0 = "com.miui.safepay.SHOW_WIFI_WARNING_DIALOG"
            r9.setAction(r0)     // Catch:{ JSONException -> 0x00d5 }
            java.lang.String r0 = "extra_wifi_info"
            android.net.wifi.WifiInfo r1 = r8.f5459b     // Catch:{ JSONException -> 0x00d5 }
            r9.putExtra(r0, r1)     // Catch:{ JSONException -> 0x00d5 }
            android.content.Context r0 = r8.f5461d     // Catch:{ JSONException -> 0x00d5 }
            r0.startService(r9)     // Catch:{ JSONException -> 0x00d5 }
            goto L_0x00d9
        L_0x00d5:
            r9 = move-exception
            r9.printStackTrace()
        L_0x00d9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.guardprovider.WifiCheckObserver.o(int):void");
    }
}
