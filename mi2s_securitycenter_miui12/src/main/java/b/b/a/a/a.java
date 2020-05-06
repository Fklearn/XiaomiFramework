package b.b.a.a;

import android.content.Context;
import b.b.a.e.c;
import b.b.a.e.n;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.analytics.AnalyticsUtil;
import com.miui.antispam.db.d;
import java.util.HashMap;
import java.util.Map;
import miui.telephony.SubscriptionManager;

public class a {
    public static void a() {
        g("show_sms_log");
    }

    public static void a(int i, long j) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("time", String.valueOf(j));
        a("intercept_time_" + i, (Map<String, String>) hashMap);
    }

    public static void a(long j) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("time", j < 500 ? "fast" : j < 1000 ? "medium" : j < 1500 ? "slow" : j < AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS ? "optimaze" : "timeout");
        a("intercept_time", (Map<String, String>) hashMap);
    }

    public static void a(Context context) {
        long j = 1;
        if (SubscriptionManager.getDefault().getSubscriptionInfoForSlot(0) != null) {
            if (!c.b(context, 1)) {
                j = 0;
            }
            a("toggle_antispam", j);
            if (c.b(context, 1)) {
                a(context, 1);
            }
            if (SubscriptionManager.getDefault().getSubscriptionInfoForSlot(1) == null) {
                return;
            }
            if (!c.e(context)) {
                a(context, 2);
                return;
            }
        } else if (SubscriptionManager.getDefault().getSubscriptionInfoForSlot(1) != null) {
            if (!c.b(context, 1)) {
                j = 0;
            }
            a("toggle_antispam", j);
            if (!c.b(context, 1)) {
                return;
            }
        } else {
            return;
        }
        a(context, 1);
    }

    private static void a(Context context, int i) {
        long j = 1;
        a("toggle_intercept_swindle", d.b(context, i) ? 1 : 0);
        a("toggle_intercept_intermediary", d.a(context, i) ? 1 : 0);
        a("toggle_intercept_peddle", d.d(context, i) ? 1 : 0);
        a("toggle_intercept_spam", d.c(context, i) ? 1 : 0);
        a("toggle_not_limited_repeated_calls", d.c(i) ? 1 : 0);
        a("toggle_intercept_call_transfer", d.b(i) ? 1 : 0);
        a("toggle_stranger_call", d.a(context, "stranger_call_mode", i, 1) == 1 ? 0 : 1);
        a("toggle_overseas_call", d.a(context, "oversea_call_mode", i, 1) == 1 ? 0 : 1);
        a("toggle_contacts_call", d.a(context, "contact_call_mode", i, 1) == 1 ? 0 : 1);
        a("toggle_empty_number", d.a(context, "empty_call_mode", i, 1) == 1 ? 0 : 1);
        a("toggle_sms_contact", d.a(context, "contact_sms_mode", i, 1) == 1 ? 0 : 1);
        a("toggle_black_number", n.a(context, i) > 0 ? 1 : 0);
        a("toggle_white_number", n.c(context, i) > 0 ? 1 : 0);
        a("toggle_sms_keyword_black", n.a(context, 1, i) > 0 ? 1 : 0);
        a("toggle_sms_keyword_white", n.a(context, 4, i) > 0 ? 1 : 0);
        if (!c.f(context)) {
            j = 0;
        }
        a("toggle_auto_update", j);
        String[] strArr = {"busy_tone", "absentee", "power_off", "halt"};
        int a2 = d.a(i);
        if (a2 < 0) {
            a2 = 0;
        }
        a("toggle_back_sound", strArr[a2]);
        a("toggle_noti", new String[]{"all", "non_black", "off"}[c.a(context, i)]);
        String[] strArr2 = {"intercept", "smart_intercept", "pass"};
        a("toggle_sms_stranger", strArr2[d.a(context, "stranger_sms_mode", i, 1)]);
        a("toggle_sms_noti", strArr2[d.a(context, "service_sms_mode", i, 1)]);
    }

    public static void a(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        a("homepage_click", (Map<String, String>) hashMap);
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordNumericEvent("antispam", str, j);
    }

    private static void a(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("antispam", str, str2);
    }

    public static void a(String str, String str2, String str3) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str2, str3);
        a(str, (Map<String, String>) hashMap);
    }

    private static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("antispam", str, map);
    }

    public static void b() {
        g("sms_log_delete_all_confirm");
    }

    public static void b(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("event", str);
        a("intercept_event", (Map<String, String>) hashMap);
    }

    public static void c() {
        g("sms_log_delete_all_click");
    }

    public static void c(String str) {
        a("antispam_main_open", str);
    }

    public static void d() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("short_link_check", "short_link");
        a("200_num_check", (Map<String, String>) hashMap);
    }

    public static void d(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("main_module", str);
        a("200_num_check_black", (Map<String, String>) hashMap);
    }

    public static void e() {
        g("sms_url_scan");
    }

    public static void e(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("name_list_result", str);
        a("200_num_check", (Map<String, String>) hashMap);
    }

    public static void f(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("main_module", str);
        a("200_num_check_unkown", (Map<String, String>) hashMap);
    }

    private static void g(String str) {
        AnalyticsUtil.recordCountEvent("antispam", str);
    }
}
