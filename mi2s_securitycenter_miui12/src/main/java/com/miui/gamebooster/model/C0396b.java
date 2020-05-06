package com.miui.gamebooster.model;

import android.content.Context;
import android.text.TextUtils;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.c;
import org.json.JSONObject;

/* renamed from: com.miui.gamebooster.model.b  reason: case insensitive filesystem */
public class C0396b {

    /* renamed from: a  reason: collision with root package name */
    private String f4549a;

    /* renamed from: b  reason: collision with root package name */
    private String f4550b;

    /* renamed from: c  reason: collision with root package name */
    private String f4551c;

    /* renamed from: d  reason: collision with root package name */
    private String f4552d;
    private String e;
    private String f;
    private int g;

    private C0396b(JSONObject jSONObject) {
        this.f4550b = jSONObject.optString("packageName");
        this.f4549a = jSONObject.optString("appRef");
        this.f4551c = jSONObject.optString("appClientId");
        this.f4552d = jSONObject.optString("appSignature");
        this.e = jSONObject.optString("nonce");
        this.f = jSONObject.optString("appChannel");
        this.g = jSONObject.optInt("size");
    }

    public static C0396b a(JSONObject jSONObject) {
        if (jSONObject != null && !TextUtils.isEmpty(jSONObject.optString("packageName"))) {
            return new C0396b(jSONObject);
        }
        return null;
    }

    public int a() {
        return this.g;
    }

    public void a(Context context, String str, boolean z, int i, boolean z2) {
        if (!c.f(context)) {
            c.a(context, (int) R.string.toast_network_eror);
            return;
        }
        StringBuilder sb = new StringBuilder("market://details/detailfloat?");
        if (!TextUtils.isEmpty(this.f4550b)) {
            sb.append("packageName=");
            sb.append(this.f4550b);
            if (!TextUtils.isEmpty(this.f4549a)) {
                sb.append("&ref=");
                sb.append(this.f4549a);
            }
            if (!TextUtils.isEmpty(this.f4551c)) {
                sb.append("&appClientId=");
                sb.append(this.f4551c);
            }
            if (!TextUtils.isEmpty(str)) {
                sb.append("&senderPackageName=");
                sb.append(str);
            }
            if (!TextUtils.isEmpty(this.f4552d)) {
                sb.append("&appSignature=");
                sb.append(this.f4552d);
            }
            if (!TextUtils.isEmpty(this.e)) {
                sb.append("&nonce=");
                sb.append(this.e);
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("&show_cta=");
            String str2 = "true";
            sb2.append(z ? str2 : "false");
            sb.append(sb2.toString());
            sb.append("&overlayPosition=" + i);
            StringBuilder sb3 = new StringBuilder();
            sb3.append("&startDownload=");
            if (!z2) {
                str2 = "false";
            }
            sb3.append(str2);
            sb.append(sb3.toString());
            x.a(context, this.f4550b, this.f4549a, (String) null, this.f4551c, this.f4552d, this.e, this.f, sb.toString());
        }
    }
}
