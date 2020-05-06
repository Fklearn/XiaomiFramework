package b.b.e.a;

import android.content.Context;
import b.b.c.j.g;
import com.miui.analytics.AnalyticsUtil;
import java.util.HashMap;

public class a {
    public static void a(Context context) {
        if (g.a(context) == 0) {
            boolean d2 = b.d(context);
            long j = 1;
            AnalyticsUtil.recordNumericEvent("settings_emergency", "sos_toggle_total", d2 ? 1 : 0);
            if (d2) {
                int length = b.a(context).split(";").length;
                String str = length != 1 ? length != 2 ? length != 3 ? "initial" : "three" : "two" : "one";
                HashMap hashMap = new HashMap();
                hashMap.put("emergency_contact", str);
                AnalyticsUtil.recordCountEvent("settings_emergency", "emergency_contact_number", hashMap);
                AnalyticsUtil.recordNumericEvent("settings_emergency", "call_for_help_total", b.c(context) ? 1 : 0);
                if (!b.b(context)) {
                    j = 0;
                }
                AnalyticsUtil.recordNumericEvent("settings_emergency", "send_call_record_total", j);
            }
        }
    }
}
