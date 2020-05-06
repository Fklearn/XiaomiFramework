package com.miui.gamebooster.ui;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import b.b.c.h.j;
import com.google.android.exoplayer2.C;
import com.miui.applicationlock.c.K;
import com.miui.securityscan.i.k;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/* renamed from: com.miui.gamebooster.ui.w  reason: case insensitive filesystem */
class C0456w implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f5123a;

    C0456w(N n) {
        this.f5123a = n;
    }

    public void run() {
        N n;
        try {
            JSONObject put = new JSONObject().put("xiaomiId", K.d(this.f5123a.mAppContext));
            HashMap hashMap = new HashMap();
            hashMap.put("param", new String(Base64.encode(put.toString().getBytes(C.UTF8_NAME), 2), C.UTF8_NAME));
            String a2 = k.a((Map<String, String>) hashMap, "https://pre-api.miui.security.xiaomi.com/game/queryXunYouUserInfo", k.a.POST, "22bcec80-cb42-4fd3-b220-45630fc37259", new j("gamebooster_getuserxunyouorders"));
            if (!TextUtils.isEmpty(a2)) {
                JSONObject jSONObject = new JSONObject(a2);
                int optInt = jSONObject.optInt("code");
                int optInt2 = jSONObject.optInt("status");
                if (optInt == 0 && optInt2 == 0) {
                    this.f5123a.q.a(109, new Object());
                    return;
                }
                n = this.f5123a;
            } else {
                n = this.f5123a;
            }
            n.c(false);
        } catch (Exception e) {
            Log.i(N.f4939a, e.toString());
        }
    }
}
