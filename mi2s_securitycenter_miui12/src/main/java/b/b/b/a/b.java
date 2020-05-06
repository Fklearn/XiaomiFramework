package b.b.b.a;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import b.b.a.e.c;
import b.b.b.d.m;
import b.b.b.d.n;
import b.b.b.o;
import b.b.b.p;
import com.miui.analytics.AnalyticsUtil;
import java.util.HashMap;
import java.util.Map;

public class b {

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private static ArrayMap<String, String> f1468a = new ArrayMap<>();

        static {
            f1468a.put("00001", "SecurityCenter_home");
            f1468a.put("00002", "CleanMaster_rs");
            f1468a.put("00003", "launcher");
        }

        public static void a() {
            b.b("title_click");
        }

        public static void a(long j) {
            b.b("ns_scan_time_foreground", (long) (((float) j) / 1000.0f));
        }

        public static void a(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("pkg", str);
            b.b("dange_click", (Map<String, String>) hashMap);
        }

        public static void a(String str, String str2) {
            String str3;
            HashMap hashMap = new HashMap(1);
            hashMap.put("update_result", str2);
            if ("TENCENT".equals(str)) {
                str3 = "ns_tencent_update";
            } else if ("AVL".equals(str)) {
                str3 = "ns_antiy_update";
            } else if ("Avast".equals(str)) {
                str3 = "ns_avast_update";
            } else {
                return;
            }
            b.b(str3, (Map<String, String>) hashMap);
        }

        public static void b() {
            b.b("query");
        }

        public static void b(long j) {
            b.b("ns_risk_apps_num", j);
        }

        public static void b(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("pkg", str);
            b.b("dange_notify", (Map<String, String>) hashMap);
        }

        public static void c() {
            b.b("title_show");
        }

        public static void c(long j) {
            b.b("ns_apps_num", j);
        }

        public static void c(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("pkg", str);
            b.b("dange_uninstall", (Map<String, String>) hashMap);
        }

        public static void d() {
            b.b("ns_activity_dislike");
        }

        public static void d(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("action", str);
            b.b("ns_update_popup2_click", (Map<String, String>) hashMap);
        }

        public static void e() {
            b.b("ns_start_scan");
        }

        public static void e(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("scan_action", str);
            b.b("ns_scan_action", (Map<String, String>) hashMap);
        }

        public static void f(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("update_result", str);
            b.b("ns_update_popup_result", (Map<String, String>) hashMap);
        }

        public static void g(String str) {
            HashMap hashMap = new HashMap();
            if (!TextUtils.isEmpty(str)) {
                hashMap.put("enter_way", str);
                b.b("antivirus_enter_way", (Map<String, String>) hashMap);
            }
        }

        public static void h(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("action", str);
            b.b("ns_update_popup1_click", (Map<String, String>) hashMap);
        }

        public static void i(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("action", str);
            b.b("ns_update_popup_click", (Map<String, String>) hashMap);
        }

        public static void j(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("action", str);
            b.b("ns_homepage_default_action", (Map<String, String>) hashMap);
        }
    }

    /* renamed from: b.b.b.a.b$b  reason: collision with other inner class name */
    public static class C0023b {
        public static String a(int i) {
            switch (i) {
                case 1:
                    return "risky_wifi_approve";
                case 2:
                    return "risky_root";
                case 3:
                    return "risky_sign";
                case 4:
                    return "risky_virus";
                case 5:
                    return "risky_messaging";
                case 6:
                    return "risky_wifi";
                default:
                    return "safe";
            }
        }

        public static void a() {
            HashMap hashMap = new HashMap();
            hashMap.put("toggle_antispam", "click_fix");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void a(Context context) {
            o a2 = o.a(context);
            HashMap hashMap = new HashMap();
            hashMap.put("network_security", a2.x() == m.SAFE ? "safe" : a2.x() == m.RISK ? "risk" : "dangerous");
            hashMap.put("root_status", a2.y() ? "root_got" : "root_not_yet");
            hashMap.put("update", a2.z() ? "update_available" : "update_not_found");
            String str = "on";
            hashMap.put("pay_environment_check", p.j() ? str : "off");
            hashMap.put("system_sms_default", a2.j() == null ? "system_sms" : "third_party_sms");
            if (!c.d(context)) {
                str = "off";
            }
            hashMap.put("toggle_antispam", str);
            hashMap.put("noti_sms_perm", a2.k() ? "allowed" : "no_allow");
            String str2 = "not_found";
            hashMap.put("virus_app", a2.t() > 0 ? str2 : "found");
            if (a2.r() > 0) {
                str2 = "found";
            }
            hashMap.put("unofficial_app", str2);
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void a(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("click_ok", str);
            b.b("ns_result_guidance", (Map<String, String>) hashMap);
        }

        public static void a(String str, String str2) {
            HashMap hashMap = new HashMap();
            hashMap.put("source_package", str);
            hashMap.put("pay_package", str2);
            b.b("pay_event", (Map<String, String>) hashMap);
        }

        public static void b() {
            HashMap hashMap = new HashMap();
            hashMap.put("system_sms_default", "click_fix");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void b(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("show", str);
            b.b("ns_result_guidance", (Map<String, String>) hashMap);
        }

        public static void c() {
            HashMap hashMap = new HashMap();
            hashMap.put("pay_environment_check", "click_add");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void c(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("action", str);
            b.b("ns_result_background_popup_click", (Map<String, String>) hashMap);
        }

        public static void d() {
            HashMap hashMap = new HashMap();
            hashMap.put("pay_environment_check", "click_fix");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void d(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_click", str);
            b.b("ns_result_action_activity", (Map<String, String>) hashMap);
            h("activity");
        }

        public static void e() {
            HashMap hashMap = new HashMap();
            hashMap.put("noti_sms_perm", "click_fix");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void e(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", str);
            b.b("ns_result_action_activity", (Map<String, String>) hashMap);
            m("activity");
        }

        public static void f() {
            HashMap hashMap = new HashMap();
            hashMap.put("root_status", "click_fix");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void f(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_click", str);
            b.b("ns_result_action_ad", (Map<String, String>) hashMap);
            h("ad");
        }

        public static void g() {
            HashMap hashMap = new HashMap();
            hashMap.put("root_status", "finish_fix");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void g(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", str);
            b.b("ns_result_action_ad", (Map<String, String>) hashMap);
            m("ad");
        }

        public static void h() {
            HashMap hashMap = new HashMap();
            hashMap.put("unofficial_app", "click_fix");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void h(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_click", str);
            b.b("ns_result_action", (Map<String, String>) hashMap);
        }

        public static void i() {
            HashMap hashMap = new HashMap();
            hashMap.put("update", "finish_update");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void i(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_click", str);
            b.b("ns_result_action_f", (Map<String, String>) hashMap);
            h("function");
        }

        public static void j() {
            HashMap hashMap = new HashMap();
            hashMap.put("update", "click_update");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void j(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", str);
            b.b("ns_result_action_f", (Map<String, String>) hashMap);
            m("function");
        }

        public static void k() {
            HashMap hashMap = new HashMap();
            hashMap.put("virus_app", "click_fix");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void k(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_click", str);
            b.b("ns_result_action_news", (Map<String, String>) hashMap);
            h("news");
        }

        public static void l() {
            HashMap hashMap = new HashMap();
            hashMap.put("network_security", "wifi_cut_off");
            b.b("ns_result_foreground", (Map<String, String>) hashMap);
        }

        public static void l(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", str);
            b.b("ns_result_action_news", (Map<String, String>) hashMap);
            m("news");
        }

        public static void m(String str) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", str);
            b.b("ns_result_action", (Map<String, String>) hashMap);
        }

        public static void n(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("result", str);
            b.b("ns_result_background", (Map<String, String>) hashMap);
        }
    }

    public static void a(Context context) {
        long j;
        String str;
        long j2 = 1;
        c("toggle_ns_antivirus_whitelist", p.o() ? 1 : 0);
        String a2 = p.a();
        a("toggle_ns_engine_one", a2);
        if ("TENCENT".equals(a2)) {
            c("toggle_ns_tencent_update", p.a("key_database_auto_update_enabled_TENCENT") ? 1 : 0);
            j = p.n() ? 1 : 0;
            str = "toggle_ns_cloud_scan";
        } else if ("AVL".equals(a2)) {
            j = p.a("key_database_auto_update_enabled_AVL") ? 1 : 0;
            str = "toggle_ns_antiy_update";
        } else {
            j = p.a("key_database_auto_update_enabled_Avast") ? 1 : 0;
            str = "toggle_ns_avast_update";
        }
        c(str, j);
        c("toggle_ns_installing_monitor", b.b.b.d.a.a(context) ? 1 : 0);
        c("toggle_ns_pay_environment_check", p.j() ? 1 : 0);
        if (p.j()) {
            c("toggle_ns_safe_input", p.l() ? 1 : 0);
        }
        c("toggle_ns_wlan_scan", p.p() ? 1 : 0);
        c("toggle_ns_root_scan", p.k() ? 1 : 0);
        c("toggle_ns_update_scan", p.m() ? 1 : 0);
        if (n.i(context) <= 0) {
            j2 = 0;
        }
        c("toggle_ns_genuine_whitelist", j2);
    }

    private static void a(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("antivirus", str, str2);
    }

    /* access modifiers changed from: private */
    public static void b(String str) {
        AnalyticsUtil.recordCountEvent("antivirus", str);
    }

    /* access modifiers changed from: private */
    public static void b(String str, long j) {
        AnalyticsUtil.recordCalculateEvent("antivirus", str, j);
    }

    /* access modifiers changed from: private */
    public static void b(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("antivirus", str, map);
    }

    private static void c(String str, long j) {
        AnalyticsUtil.recordNumericEvent("antivirus", str, j);
    }
}
