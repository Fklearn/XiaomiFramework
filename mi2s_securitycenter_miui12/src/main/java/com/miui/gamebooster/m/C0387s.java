package com.miui.gamebooster.m;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.y;
import b.b.l.b;
import com.miui.activityutil.o;
import com.miui.gamebooster.globalgame.util.d;
import com.miui.gamebooster.model.ActiveModel;
import com.miui.gamebooster.model.ActiveTrackModel;
import com.miui.maml.data.VariableNames;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.g;
import com.miui.securitycenter.h;
import com.miui.securitycenter.n;
import com.miui.securityscan.i.c;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.cloud.CloudPushConstants;
import miui.os.Build;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: com.miui.gamebooster.m.s  reason: case insensitive filesystem */
public class C0387s {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f4511a = "com.miui.gamebooster.m.s";

    /* renamed from: b  reason: collision with root package name */
    private static final String f4512b = "gb_active_click_track";

    /* renamed from: c  reason: collision with root package name */
    private static final String f4513c = "gb_active_view_track";

    /* renamed from: d  reason: collision with root package name */
    private static C0387s f4514d;
    private final List<ActiveTrackModel> e = new ArrayList();
    private final List<ActiveTrackModel> f = new ArrayList();
    private final Object g = new Object();

    /* renamed from: com.miui.gamebooster.m.s$a */
    class a {

        /* renamed from: a  reason: collision with root package name */
        private int f4515a;

        public int a() {
            return this.f4515a;
        }
    }

    private C0387s() {
    }

    public static ActiveTrackModel a(String str, String str2) {
        ActiveModel g2 = b.b().g(str);
        if (g2 == null) {
            return null;
        }
        return new ActiveTrackModel(g2.getId(), str2, g2.getGamePkgName(), g2.getImgUrl(), g2.getActivityText(), g2.getGamePkgNameCn());
    }

    /* access modifiers changed from: private */
    public Map<String, List<ActiveTrackModel>> a(String str) {
        JSONArray jSONArray;
        int length;
        ActiveTrackModel activeTrackModel;
        if (str == null || (length = jSONArray.length()) == 0) {
            return null;
        }
        HashMap hashMap = new HashMap();
        for (int i = 0; i < length; i++) {
            String optString = (jSONArray = new JSONArray(str)).optString(i);
            if (!TextUtils.isEmpty(optString) && (activeTrackModel = (ActiveTrackModel) d.a(optString, ActiveTrackModel.class)) != null) {
                String str2 = activeTrackModel.getDate() + activeTrackModel.getId();
                if (hashMap.containsKey(str2)) {
                    ((List) hashMap.get(str2)).add(activeTrackModel);
                } else {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(activeTrackModel);
                    hashMap.put(str2, arrayList);
                }
            }
        }
        return hashMap;
    }

    private void a(List<ActiveTrackModel> list, boolean z) {
        try {
            if (!list.isEmpty()) {
                String a2 = a(Application.d().getApplicationContext(), z ? f4512b : f4513c);
                JSONArray jSONArray = TextUtils.isEmpty(a2) ? new JSONArray() : new JSONArray(a2);
                for (ActiveTrackModel a3 : list) {
                    jSONArray.put(d.a((Object) a3));
                }
                C0382m.a("gamebooster", z ? f4512b : f4513c, jSONArray.toString(), Application.d().getApplicationContext());
                a(z);
            }
        } catch (Exception e2) {
            String str = f4511a;
            Log.e(str, "write error " + e2);
        }
    }

    /* access modifiers changed from: private */
    public void a(Map<String, List<ActiveTrackModel>> map, JSONArray jSONArray) {
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, List<ActiveTrackModel>> value : map.entrySet()) {
                List list = (List) value.getValue();
                ActiveTrackModel activeTrackModel = (ActiveTrackModel) list.get(0);
                activeTrackModel.setTimes(list.size());
                jSONArray.put(b(activeTrackModel));
            }
        }
    }

    private void a(boolean z) {
        synchronized (this.g) {
            (z ? this.e : this.f).clear();
        }
    }

    public static synchronized C0387s b() {
        C0387s sVar;
        synchronized (C0387s.class) {
            if (f4514d == null) {
                f4514d = new C0387s();
            }
            sVar = f4514d;
        }
        return sVar;
    }

    private JSONObject b(ActiveTrackModel activeTrackModel) {
        String str;
        String h;
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(VariableNames.VAR_DATE, activeTrackModel.getDate());
            jSONObject.put("id", activeTrackModel.getId());
            jSONObject.put(CloudPushConstants.WATERMARK_TYPE.SUBSCRIPTION, activeTrackModel.getS());
            jSONObject.put("type", activeTrackModel.getType());
            jSONObject.put("times", activeTrackModel.getTimes());
            jSONObject.put("i", activeTrackModel.getI());
            jSONObject.put("a", activeTrackModel.getA());
            jSONObject.put("game", activeTrackModel.getGame());
            if (!c()) {
                jSONObject.put(com.xiaomi.stat.d.V, ExtraTextUtils.toHexReadable(DigestUtils.get(c.b(Application.d().getApplicationContext()), "MD5")));
            }
            if (Build.IS_INTERNATIONAL_BUILD) {
                str = CloudPushConstants.WATERMARK_TYPE.GLOBAL;
                h = g();
            } else {
                str = "oa";
                h = h();
            }
            jSONObject.put(str, h);
            return jSONObject;
        } catch (JSONException e2) {
            Log.e(f4511a, "to json error", e2);
            return null;
        }
    }

    public static boolean c() {
        return o.f2310b.equals(y.a("ro.miui.restrict_imei", ""));
    }

    /* access modifiers changed from: private */
    public void f() {
        C0382m.a("gamebooster", f4512b, Application.d().getApplicationContext());
        C0382m.a("gamebooster", f4513c, Application.d().getApplicationContext());
    }

    private String g() {
        try {
            return com.miui.securityscan.i.b.a(Application.d().getApplicationContext());
        } catch (Exception unused) {
            return null;
        }
    }

    private String h() {
        return g.a(Application.d().getApplicationContext());
    }

    private boolean i() {
        return System.currentTimeMillis() - com.miui.common.persistence.b.a("gamebooster_key_active_track", 0) > 43200000;
    }

    public String a(Context context, String str) {
        return C0382m.b("gamebooster", str, context);
    }

    public void a(ActiveTrackModel activeTrackModel) {
        List<ActiveTrackModel> list;
        if (activeTrackModel != null) {
            synchronized (this.g) {
                if (TextUtils.equals(MiStat.Event.CLICK, activeTrackModel.getType())) {
                    list = this.e;
                } else if (TextUtils.equals("view", activeTrackModel.getType())) {
                    list = this.f;
                }
                list.add(activeTrackModel);
            }
            n.a().b(new C0386q(this));
        }
    }

    public void d() {
        if (h.i() && i()) {
            n.a().b(new r(this));
        }
    }

    public void e() {
        a(this.e, true);
        a(this.f, false);
    }
}
