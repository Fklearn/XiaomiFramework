package com.miui.monthreport;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import b.b.c.j.i;
import b.b.c.j.y;
import b.b.c.j.z;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.luckymoney.config.Constants;
import com.miui.maml.data.VariableNames;
import com.miui.monthreport.b;
import com.miui.permcenter.permissions.C0466c;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.g;
import com.miui.securityscan.i.k;
import com.xiaomi.stat.a.j;
import com.xiaomi.stat.d;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class h {

    /* renamed from: a  reason: collision with root package name */
    private int f5642a = 0;

    /* renamed from: b  reason: collision with root package name */
    private List<String> f5643b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private long f5644c;

    /* renamed from: d  reason: collision with root package name */
    private JSONObject f5645d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
    private String k;
    private String l;
    private String m;
    private String n;
    private long o;
    private long p;
    private String q;
    private String r;
    private String s;
    private String t;
    private String u;
    private JSONObject v = null;
    private String w = null;
    private Context x = Application.d();

    private h(String str) {
        this.t = str;
    }

    public static h a(String str, Exception exc) {
        h hVar = new h(str);
        hVar.g();
        hVar.a(exc);
        return hVar;
    }

    private void a(Exception exc) {
        this.f5645d = h();
        JSONObject jSONObject = this.f5645d;
        if (jSONObject != null) {
            try {
                a(Base64.encodeToString(jSONObject.toString().getBytes(), 0));
                JSONObject jSONObject2 = new JSONObject();
                this.g = UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis();
                jSONObject2.put(d.V, this.e);
                jSONObject2.put("oa", this.u);
                jSONObject2.put("reportId", this.g);
                jSONObject2.put(VariableNames.VAR_DATE, this.f);
                jSONObject2.put(Constants.JSON_KEY_MODULE, this.t);
                if (exc != null) {
                    jSONObject2.put("exception", URLEncoder.encode(String.format("class:%s, msg:%s", new Object[]{exc.getClass().getName(), exc.getMessage()}), C.UTF8_NAME));
                }
                a(jSONObject2);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void a(String str) {
        this.w = str;
    }

    private void a(JSONObject jSONObject) {
        this.v = jSONObject;
    }

    private void g() {
        Calendar instance = Calendar.getInstance();
        int i2 = instance.get(1);
        int i3 = instance.get(2);
        int i4 = instance.get(5);
        instance.clear();
        instance.set(i2, i3, i4);
        this.f5644c = instance.getTimeInMillis();
        this.e = (Build.IS_INTERNATIONAL_BUILD || k.a()) ? "" : f.b(this.x);
        this.n = f.a(this.x);
        this.u = g.a(this.x);
        this.q = "";
        this.r = g.a(com.miui.activityutil.h.f2289a);
        this.h = y.a("ro.product.device", com.miui.activityutil.h.f2289a);
        this.i = y.a("ro.carrier.name", com.miui.activityutil.h.f2289a);
        this.j = Build.getRegion();
        this.k = Locale.getDefault().toString();
        this.l = "MIUI-" + Build.VERSION.INCREMENTAL;
        this.m = i.b();
        this.s = i.e(this.x);
    }

    private JSONObject h() {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray i2 = i();
            if (i2 != null) {
                if (i2.length() != 0) {
                    jSONObject.put("reportId", this.g);
                    jSONObject.put("d", this.h);
                    jSONObject.put(C0466c.f6254a, this.i);
                    jSONObject.put("r", this.j);
                    jSONObject.put("l", this.k);
                    jSONObject.put("v", this.l);
                    jSONObject.put(Constants.JSON_KEY_T, this.m);
                    jSONObject.put("a", this.n);
                    jSONObject.put(d.V, this.e);
                    jSONObject.put("startTime", this.o);
                    jSONObject.put("endTime", this.p);
                    jSONObject.put("areaCode", this.q);
                    jSONObject.put("area", this.r);
                    jSONObject.put(VariableNames.VAR_DATE, this.f);
                    jSONObject.put(j.f8382b, i2);
                    jSONObject.put("o", this.s);
                    jSONObject.put("moduleName", this.t);
                    return jSONObject;
                }
            }
            return null;
        } catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private JSONArray i() {
        List<b.C0054b> a2 = b.a().a(this.t, 500);
        if (a2 == null || a2.isEmpty()) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        this.o = Long.MAX_VALUE;
        this.p = 0;
        this.f = "";
        this.f5643b.clear();
        for (b.C0054b a3 : a2) {
            JSONObject a4 = a(a3);
            if (a4 != null) {
                jSONArray.put(a4);
            }
        }
        return jSONArray;
    }

    public List<String> a() {
        return this.f5643b;
    }

    public JSONObject a(b.C0054b bVar) {
        try {
            if (bVar.f5621b > 0 && bVar.f5622c >= 1 && !TextUtils.isEmpty(bVar.e)) {
                if (!TextUtils.isEmpty(bVar.f)) {
                    if (bVar.f5623d >= this.f5644c) {
                        return null;
                    }
                    String a2 = z.a(bVar.f5623d, "yyyyMMdd");
                    if (TextUtils.isEmpty(this.f)) {
                        this.f = a2;
                    }
                    if (!this.f.equals(a2)) {
                        return null;
                    }
                    if (bVar.f5623d < this.o) {
                        this.o = bVar.f5623d;
                    }
                    if (bVar.f5623d > this.p) {
                        this.p = bVar.f5623d;
                    }
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("eventId", bVar.f5620a);
                    jSONObject.put("eventTime", bVar.f5623d);
                    jSONObject.put("pkgName", bVar.e);
                    jSONObject.put("eventType", bVar.f5621b);
                    jSONObject.put("version", bVar.f5622c);
                    jSONObject.put(DataSchemeDataSource.SCHEME_DATA, new JSONObject(bVar.f.replace("\\", "")));
                    this.f5643b.add(bVar.f5620a);
                    return jSONObject;
                }
            }
            this.f5643b.add(bVar.f5620a);
            return null;
        } catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public int b() {
        return this.f5642a;
    }

    public String c() {
        return this.t;
    }

    public String d() {
        return this.w;
    }

    public JSONObject e() {
        return this.v;
    }

    public void f() {
        this.f5642a++;
    }

    public String toString() {
        return "Task : reportId :" + this.g + ", moduleName :" + this.t + ", date :" + this.f;
    }
}
