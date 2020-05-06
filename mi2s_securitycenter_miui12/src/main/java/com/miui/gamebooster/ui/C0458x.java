package com.miui.gamebooster.ui;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import b.b.c.h.j;
import com.google.android.exoplayer2.C;
import com.miui.applicationlock.c.K;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.ga;
import com.miui.securityscan.i.k;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/* renamed from: com.miui.gamebooster.ui.x  reason: case insensitive filesystem */
class C0458x implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Boolean f5125a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f5126b;

    C0458x(N n, Boolean bool) {
        this.f5126b = n;
        this.f5125a = bool;
    }

    public void run() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("xiaomiId", K.d(this.f5126b.mAppContext));
            String a2 = b.a("gamebooster_xunyou_cache_user_type", (String) null);
            if (a2 != null) {
                jSONObject.put("userType", ga.b(a2));
                Long valueOf = Long.valueOf(b.a("gamebooster_xunyou_cache_time", -1));
                if (valueOf.longValue() != -1) {
                    jSONObject.put("expireTime", ga.a(valueOf));
                }
                if (this.f5125a != null) {
                    jSONObject.put("isSuccess", this.f5125a.booleanValue() ? "true" : "false");
                }
                HashMap hashMap = new HashMap();
                hashMap.put("param", new String(Base64.encode(jSONObject.toString().getBytes(C.UTF8_NAME), 2), C.UTF8_NAME));
                String a3 = k.a((Map<String, String>) hashMap, "https://api.miui.security.xiaomi.com/game/xunYouReport", k.a.POST, "22bcec80-cb42-4fd3-b220-45630fc37259", new j("gamebooster_postuserinfotoserver"));
                if (!TextUtils.isEmpty(a3) && new JSONObject(a3).optInt("code") == 0) {
                    Log.i(N.f4939a, "report UserInfo success!");
                }
            }
        } catch (Exception e) {
            Log.i(N.f4939a, e.toString());
        }
    }
}
