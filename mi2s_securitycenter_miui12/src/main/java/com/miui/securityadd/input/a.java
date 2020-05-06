package com.miui.securityadd.input;

import com.xiaomi.stat.MiStat;
import org.json.JSONObject;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private String f7448a;

    /* renamed from: b  reason: collision with root package name */
    private int f7449b;

    /* renamed from: c  reason: collision with root package name */
    private long f7450c;

    public a() {
    }

    public a(String str, int i, long j) {
        this.f7448a = str;
        this.f7449b = i;
        this.f7450c = j;
    }

    public static a a(JSONObject jSONObject) {
        a aVar = new a();
        aVar.a(jSONObject.getString(MiStat.Param.CONTENT));
        aVar.a(jSONObject.getInt("type"));
        aVar.a(jSONObject.getLong("time"));
        return aVar;
    }

    public String a() {
        return this.f7448a;
    }

    public void a(int i) {
        this.f7449b = i;
    }

    public void a(long j) {
        this.f7450c = j;
    }

    public void a(String str) {
        this.f7448a = str;
    }

    public long b() {
        return this.f7450c;
    }

    public int c() {
        return this.f7449b;
    }

    public JSONObject d() {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(MiStat.Param.CONTENT, this.f7448a);
        jSONObject.put("type", this.f7449b);
        jSONObject.put("time", this.f7450c);
        return jSONObject;
    }
}
