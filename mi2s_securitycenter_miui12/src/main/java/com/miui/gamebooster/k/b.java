package com.miui.gamebooster.k;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import b.b.c.h.j;
import com.google.android.exoplayer2.C;
import com.miui.applicationlock.c.K;
import com.miui.gamebooster.m.C0373d;
import com.miui.securityscan.i.k;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static b f4438a;

    /* renamed from: b  reason: collision with root package name */
    private static HashMap<String, Integer> f4439b = new a();

    /* renamed from: c  reason: collision with root package name */
    private boolean f4440c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f4441d;
    private int e;
    private ArrayList<String> f = new ArrayList<>();

    private b() {
    }

    public static synchronized b b() {
        b bVar;
        synchronized (b.class) {
            if (f4438a == null) {
                f4438a = new b();
            }
            bVar = f4438a;
        }
        return bVar;
    }

    public void a(ArrayList<String> arrayList) {
        this.f = arrayList;
    }

    public void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.f4441d = jSONObject.optBoolean("signedToday");
            this.e = jSONObject.optInt("signDays");
            com.miui.common.persistence.b.b("key_gamebooster_support_sign_function", jSONObject.optBoolean("supportSign"));
        }
    }

    public boolean a() {
        return this.f4441d;
    }

    public boolean a(Map<String, String> map, Context context, boolean z) {
        if (map == null) {
            try {
                map = new HashMap<>();
            } catch (Exception e2) {
                Log.i("NetUtils", e2.toString());
                return false;
            }
        }
        map.put("miId", new String(Base64.encode(K.d(context).toString().getBytes(C.UTF8_NAME), 2), C.UTF8_NAME));
        map.put("type", z ? "signin" : "query");
        String a2 = k.a(map, "https://adv.sec.miui.com/info/signinXunyou", k.a.POST, "4e0b237f-c3ae-4663-b94f-1357130f5599", new j("gamebooster_signmodel_post"));
        if (a2 == null) {
            return false;
        }
        if (z) {
            b(new JSONObject(a2));
            return true;
        }
        a(new JSONObject(a2));
        return true;
    }

    public void b(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.f4440c = jSONObject.optBoolean("signResult");
            this.e = jSONObject.optInt("signDays");
            boolean z = this.f4440c;
            if (!z) {
                z = this.f4441d;
            }
            this.f4441d = z;
            if (this.f4440c) {
                C0373d.b(MiStat.Event.CLICK, "sign_in_right_now");
            }
        }
    }

    public int c() {
        return this.e;
    }

    public ArrayList<String> d() {
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator<String> it = this.f.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (f4439b.get(next) != null && this.e >= f4439b.get(next).intValue()) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public String toString() {
        return "SignModel{signSuccess=" + this.f4440c + ", haveSigned=" + this.f4441d + ", signDay=" + this.e + ", gifts=" + this.f + '}';
    }
}
