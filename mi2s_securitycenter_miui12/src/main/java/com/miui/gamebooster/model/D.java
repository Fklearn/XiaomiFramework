package com.miui.gamebooster.model;

import com.miui.permission.PermissionContract;
import org.json.JSONObject;

public class D {

    /* renamed from: a  reason: collision with root package name */
    private String f4543a;

    /* renamed from: b  reason: collision with root package name */
    private String f4544b;

    /* renamed from: c  reason: collision with root package name */
    private int f4545c;

    /* renamed from: d  reason: collision with root package name */
    private int f4546d;
    private String e;
    private String f;

    public D(JSONObject jSONObject) {
        this.f4545c = jSONObject.optInt("promptMode");
        int i = this.f4545c;
        if (i == 1) {
            this.f4546d = jSONObject.optInt("period");
        } else if (i == 2) {
            this.f4543a = jSONObject.optString("activityName");
            this.f4544b = jSONObject.optString(PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION);
            this.e = jSONObject.optString("startTime");
            this.f = jSONObject.optString("endTime");
        }
    }

    public String a() {
        return this.f4544b;
    }

    public String b() {
        return this.f;
    }

    public int c() {
        return this.f4546d;
    }

    public int d() {
        return this.f4545c;
    }

    public String e() {
        return this.e;
    }
}
