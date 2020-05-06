package com.miui.gamebooster.m;

import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.j;
import com.miui.common.persistence.b;
import com.miui.gamebooster.globalgame.util.d;
import com.miui.gamebooster.m.C0387s;
import com.miui.gamebooster.model.ActiveTrackModel;
import com.miui.securitycenter.Application;
import com.miui.securityscan.i.k;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;

class r implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0387s f4510a;

    r(C0387s sVar) {
        this.f4510a = sVar;
    }

    public void run() {
        try {
            this.f4510a.e();
            Map a2 = this.f4510a.a(this.f4510a.a(Application.d().getApplicationContext(), "gb_active_click_track"));
            Map a3 = this.f4510a.a(this.f4510a.a(Application.d().getApplicationContext(), "gb_active_view_track"));
            JSONArray jSONArray = new JSONArray();
            this.f4510a.a((Map<String, List<ActiveTrackModel>>) a2, jSONArray);
            this.f4510a.a((Map<String, List<ActiveTrackModel>>) a3, jSONArray);
            if (jSONArray.length() != 0) {
                HashMap hashMap = new HashMap();
                hashMap.put("report", jSONArray.toString());
                String a4 = k.a((Map<String, String>) hashMap, "https://data.sec.miui.com/adv/game_ad/report", new j("gamebooster_active_track"));
                if (!TextUtils.isEmpty(a4) && ((C0387s.a) d.a(a4, C0387s.a.class)).a() == 0) {
                    b.b("gamebooster_key_active_track", System.currentTimeMillis());
                    this.f4510a.f();
                }
            }
        } catch (JSONException unused) {
            Log.e(C0387s.f4511a, "post error with some exceptions");
        }
    }
}
