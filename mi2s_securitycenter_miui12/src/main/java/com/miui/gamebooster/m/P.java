package com.miui.gamebooster.m;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.j;
import b.b.c.j.f;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.gamebooster.d.a;
import com.miui.gamebooster.e.b;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.k;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class P {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4458a = "com.miui.gamebooster.m.P";

    private static ArrayList<String> a(List<Object> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (!(list == null || list.size() == 0)) {
            try {
                for (Object obj : list) {
                    String obj2 = obj.toString();
                    if (!TextUtils.isEmpty(obj2)) {
                        JSONArray optJSONArray = new JSONObject(obj2).optJSONArray("pkgName");
                        if (optJSONArray != null) {
                            if (optJSONArray.length() != 0) {
                                int length = optJSONArray.length();
                                for (int i = 0; i < length; i++) {
                                    arrayList.add(String.valueOf(optJSONArray.get(i)));
                                }
                            }
                        }
                        return arrayList;
                    }
                }
            } catch (Exception e) {
                Log.i(f4458a, e.toString());
            }
        }
        return arrayList;
    }

    public static void a(Context context) {
        try {
            if (!h.i() || !f.c(context)) {
                b.a(context);
                return;
            }
            String a2 = k.a(a.f4248c, new j("gamebooster_networkutils_pullpopgame"));
            Log.d(f4458a, a2);
            ArrayList arrayList = new ArrayList();
            JSONArray jSONArray = new JSONObject(a2).getJSONArray("packageNames");
            int length = jSONArray.length();
            if (length > 0) {
                com.miui.gamebooster.provider.a.a(context);
            }
            for (int i = 0; i < length; i++) {
                String string = jSONArray.getString(i);
                arrayList.add(string);
                com.miui.gamebooster.provider.a.b(context, string);
            }
            C0382m.a("gamebooster", "gblist", (ArrayList<String>) arrayList, context);
            b.a(context, arrayList);
        } catch (Exception e) {
            Log.e(f4458a, e.toString());
        }
    }

    public static void b(Context context) {
        List list;
        try {
            list = (List) e.a(Class.forName("android.provider.MiuiSettings$SettingsCloudData"), List.class, "getCloudDataList", (Class<?>[]) new Class[]{ContentResolver.class, String.class}, context.getContentResolver(), "freeform_apps");
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            list = null;
        }
        ArrayList<String> a2 = a((List<Object>) list);
        if (!a2.isEmpty()) {
            C0382m.a("gamebooster", "freeformlist", a2, context);
        }
    }

    public static void c(Context context) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            try {
                if (h.i() && f.c(context)) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("sdkVersion", String.valueOf(x.e(context, "com.miui.vpnsdkmanager")));
                    String a2 = k.a((Map<String, String>) hashMap, "https://api.miui.security.xiaomi.com/game/queryXunYouGameList", new j("gamebooster_pullxunyousupportgame"));
                    Log.d(f4458a, a2);
                    ArrayList arrayList = new ArrayList();
                    JSONArray jSONArray = new JSONObject(a2).getJSONArray("gameList");
                    int length = jSONArray.length();
                    for (int i = 0; i < length; i++) {
                        arrayList.add(jSONArray.getString(i));
                    }
                    C0382m.a("gamebooster", "xunyousupportlist", (ArrayList<String>) arrayList, context);
                }
            } catch (Exception e) {
                Log.e(f4458a, e.toString());
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x00a1 A[Catch:{ Exception -> 0x00aa }] */
    /* JADX WARNING: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void d(android.content.Context r6) {
        /*
            java.lang.String r0 = "UTF-8"
            boolean r1 = com.miui.applicationlock.c.K.c(r6)
            if (r1 != 0) goto L_0x0009
            return
        L_0x0009:
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x00aa }
            r1.<init>()     // Catch:{ Exception -> 0x00aa }
            java.lang.String r2 = "xiaomiId"
            java.lang.String r3 = com.miui.applicationlock.c.K.d(r6)     // Catch:{ Exception -> 0x00aa }
            org.json.JSONObject r1 = r1.put(r2, r3)     // Catch:{ Exception -> 0x00aa }
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x00aa }
            r2.<init>()     // Catch:{ Exception -> 0x00aa }
            java.lang.String r3 = "param"
            java.lang.String r4 = new java.lang.String     // Catch:{ Exception -> 0x00aa }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00aa }
            byte[] r1 = r1.getBytes(r0)     // Catch:{ Exception -> 0x00aa }
            r5 = 2
            byte[] r1 = android.util.Base64.encode(r1, r5)     // Catch:{ Exception -> 0x00aa }
            r4.<init>(r1, r0)     // Catch:{ Exception -> 0x00aa }
            r2.put(r3, r4)     // Catch:{ Exception -> 0x00aa }
            java.lang.String r0 = "https://pre-api.miui.security.xiaomi.com/game/queryXunYouUserInfo"
            com.miui.securityscan.i.k$a r1 = com.miui.securityscan.i.k.a.POST     // Catch:{ Exception -> 0x00aa }
            java.lang.String r3 = "22bcec80-cb42-4fd3-b220-45630fc37259"
            b.b.c.h.j r4 = new b.b.c.h.j     // Catch:{ Exception -> 0x00aa }
            java.lang.String r5 = "gamebooster_pullxunyouuserstatus"
            r4.<init>(r5)     // Catch:{ Exception -> 0x00aa }
            java.lang.String r0 = com.miui.securityscan.i.k.a((java.util.Map<java.lang.String, java.lang.String>) r2, (java.lang.String) r0, (com.miui.securityscan.i.k.a) r1, (java.lang.String) r3, (b.b.c.h.j) r4)     // Catch:{ Exception -> 0x00aa }
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x00aa }
            r2 = 0
            r3 = 1
            java.lang.String r4 = "gamebooster_xunyou_first_user"
            if (r1 != 0) goto L_0x0068
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x00aa }
            r1.<init>(r0)     // Catch:{ Exception -> 0x00aa }
            java.lang.String r0 = "code"
            int r0 = r1.optInt(r0)     // Catch:{ Exception -> 0x00aa }
            java.lang.String r5 = "status"
            int r1 = r1.optInt(r5)     // Catch:{ Exception -> 0x00aa }
            if (r0 != 0) goto L_0x0068
            if (r1 != 0) goto L_0x0068
            com.miui.common.persistence.b.b((java.lang.String) r4, (boolean) r2)     // Catch:{ Exception -> 0x00aa }
            goto L_0x006b
        L_0x0068:
            com.miui.common.persistence.b.b((java.lang.String) r4, (boolean) r3)     // Catch:{ Exception -> 0x00aa }
        L_0x006b:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x00aa }
            r0.<init>()     // Catch:{ Exception -> 0x00aa }
            java.lang.String r1 = "com.miui.gamebooster.action.RESET_USERSTATUS"
            r0.setAction(r1)     // Catch:{ Exception -> 0x00aa }
            r6.sendBroadcast(r0)     // Catch:{ Exception -> 0x00aa }
            java.lang.String r0 = f4458a     // Catch:{ Exception -> 0x00aa }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00aa }
            r1.<init>()     // Catch:{ Exception -> 0x00aa }
            java.lang.String r3 = "gamebooster_xunyou_first_user "
            r1.append(r3)     // Catch:{ Exception -> 0x00aa }
            boolean r2 = com.miui.common.persistence.b.a((java.lang.String) r4, (boolean) r2)     // Catch:{ Exception -> 0x00aa }
            r1.append(r2)     // Catch:{ Exception -> 0x00aa }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00aa }
            android.util.Log.e(r0, r1)     // Catch:{ Exception -> 0x00aa }
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ Exception -> 0x00aa }
            r0.<init>()     // Catch:{ Exception -> 0x00aa }
            java.lang.String r6 = com.miui.gamebooster.model.E.a((java.util.Map<java.lang.String, java.lang.String>) r0, (android.content.Context) r6)     // Catch:{ Exception -> 0x00aa }
            boolean r0 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x00aa }
            if (r0 != 0) goto L_0x00b4
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x00aa }
            r0.<init>(r6)     // Catch:{ Exception -> 0x00aa }
            com.miui.gamebooster.model.E.a((org.json.JSONObject) r0)     // Catch:{ Exception -> 0x00aa }
            goto L_0x00b4
        L_0x00aa:
            r6 = move-exception
            java.lang.String r0 = f4458a
            java.lang.String r6 = r6.toString()
            android.util.Log.i(r0, r6)
        L_0x00b4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.P.d(android.content.Context):void");
    }
}
