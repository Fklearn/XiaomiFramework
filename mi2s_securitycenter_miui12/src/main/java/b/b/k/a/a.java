package b.b.k.a;

import android.content.Context;
import com.miui.analytics.AnalyticsUtil;
import java.util.HashMap;
import java.util.Map;

public class a {
    public static void a() {
        c("privacy_apps_manage_page");
    }

    public static void a(Context context) {
        a("privacy_app_toggle", new b.b.k.b.a(context).e() ? 1 : 0);
    }

    public static void a(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("packagename", str);
        a("add_privacy_app_name", (Map<String, String>) hashMap);
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordNumericEvent("privacyapps", str, j);
    }

    private static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("privacyapps", str, map);
    }

    public static void b(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("packagename", str);
        a("open_privacy_app", (Map<String, String>) hashMap);
    }

    private static void c(String str) {
        AnalyticsUtil.recordCountEvent("privacyapps", str);
    }
}
