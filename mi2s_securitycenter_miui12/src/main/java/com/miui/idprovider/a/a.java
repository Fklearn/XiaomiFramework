package com.miui.idprovider.a;

import android.content.Context;
import android.provider.Settings;
import b.b.c.j.B;
import com.miui.analytics.AnalyticsUtil;

public class a {
    public static void a(Context context) {
        if (B.c() == 0) {
            AnalyticsUtil.recordNumericEvent("idprovider", "allow_oaid_used", (long) Settings.Secure.getInt(context.getContentResolver(), "allow_oaid_used", 1));
        }
    }
}
