package com.miui.googlebase.a;

import android.content.Context;
import b.b.c.j.B;
import com.miui.activityutil.o;
import com.miui.analytics.AnalyticsUtil;
import com.miui.googlebase.b.b;
import miui.os.SystemProperties;

public class a {
    public static void a(Context context) {
        if (B.c() == 0 && o.f2310b.equals(SystemProperties.get("ro.miui.has_gmscore"))) {
            AnalyticsUtil.recordNumericEvent("googlebase", "google_toggle_total", (long) b.a(context));
        }
    }
}
