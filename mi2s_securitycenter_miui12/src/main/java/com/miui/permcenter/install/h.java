package com.miui.permcenter.install;

import miui.cloud.CloudPushConstants;
import org.json.JSONObject;

public class h {

    /* renamed from: a  reason: collision with root package name */
    private String f6154a;

    /* renamed from: b  reason: collision with root package name */
    private String f6155b;

    /* renamed from: c  reason: collision with root package name */
    private int f6156c;

    public h() {
    }

    public h(String str, JSONObject jSONObject) {
        this.f6154a = str;
        this.f6155b = jSONObject.optString(CloudPushConstants.XML_NAME);
        this.f6156c = jSONObject.optInt("mode");
    }

    public int a() {
        return this.f6156c;
    }

    public void a(int i) {
        this.f6156c = i;
    }

    public void a(String str) {
        this.f6155b = str;
    }

    public String b() {
        return this.f6155b;
    }

    public void b(String str) {
        this.f6154a = str;
    }

    public String c() {
        return this.f6154a;
    }

    public String d() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(CloudPushConstants.XML_NAME, this.f6155b);
            jSONObject.put("mode", this.f6156c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject.toString();
    }
}
