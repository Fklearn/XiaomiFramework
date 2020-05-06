package com.miui.superpower.b;

import android.text.TextUtils;
import b.b.c.j.t;
import com.miui.analytics.AnalyticsUtil;
import com.miui.maml.elements.AdvancedSlider;
import miui.cloud.os.SystemProperties;
import org.json.JSONException;
import org.json.JSONObject;

public class h {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8088a = SystemProperties.get("ro.product.model", "");

    private static String a(int i, double d2, boolean z) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(AdvancedSlider.STATE, i);
            jSONObject.put("type", z ? 2 : 1);
            jSONObject.put("duration", d2);
            jSONObject.put("model", f8088a);
            jSONObject.put("ext", "");
            return jSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void a(double d2, boolean z) {
        if (t.a()) {
            String a2 = a(1, d2, z);
            if (!TextUtils.isEmpty(a2)) {
                t.a("securitycenter_power", a2, "mqs_super_power_saving_43081000", true);
            }
        }
    }

    public static void a(int i) {
        a("key_battery_level_exit_normal", String.valueOf(((i - 1) / 10) + 1));
    }

    public static void a(String str) {
        a("open_way", str);
    }

    private static void a(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("superpower", str, str2);
    }

    public static void b(double d2, boolean z) {
        if (t.a()) {
            String a2 = a(2, d2, z);
            if (!TextUtils.isEmpty(a2)) {
                t.a("securitycenter_power", a2, "mqs_super_power_saving_43081000", true);
            }
        }
    }

    public static void b(int i) {
        a("key_battery_level_open_normal", String.valueOf(((i - 1) / 10) + 1));
    }

    public static void c(int i) {
        a("key_battery_level_exit_super", String.valueOf(((i - 1) / 10) + 1));
    }

    public static void d(int i) {
        a("key_battery_level_open_super", String.valueOf(((i - 1) / 10) + 1));
    }
}
