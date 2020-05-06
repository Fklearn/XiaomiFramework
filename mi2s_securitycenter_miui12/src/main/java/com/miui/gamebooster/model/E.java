package com.miui.gamebooster.model;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import b.b.c.h.j;
import com.google.android.exoplayer2.C;
import com.miui.applicationlock.c.K;
import com.miui.common.persistence.b;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.securityscan.i.k;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class E implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private List<D> f4547a = new ArrayList();

    public static E a(JSONObject jSONObject) {
        E e = new E();
        JSONArray jSONArray = jSONObject.getJSONArray("activities");
        if (jSONArray.length() == 0) {
            b.b("gb_notification_business_period", 9999);
            return null;
        }
        a(jSONArray, e);
        return e;
    }

    public static String a(Map<String, String> map, Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(UserConfigure.Columns.USER_ID, K.d(context));
            if (map == null) {
                map = new HashMap<>();
            }
            map.put("param", new String(Base64.encode(jSONObject.toString().getBytes(C.UTF8_NAME), 2), C.UTF8_NAME));
        } catch (Exception e) {
            Log.i("NetUtils", e.toString());
        }
        return k.a(map, "https://api.miui.security.xiaomi.com/game/queryXunYouActivityList", k.a.POST, "22bcec80-cb42-4fd3-b220-45630fc37259", new j("gamebooster_xunyoumodel_post"));
    }

    private void a(D d2) {
        if (d2 != null) {
            this.f4547a.add(d2);
        }
    }

    private static void a(JSONArray jSONArray, E e) {
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            e.a(new D(jSONArray.getJSONObject(i)));
        }
    }

    public List<D> a() {
        return this.f4547a;
    }
}
