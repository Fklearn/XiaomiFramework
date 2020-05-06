package com.miui.securitycenter.utils;

import android.content.Context;
import android.util.Log;
import b.b.c.j.z;
import com.miui.activityutil.o;
import com.miui.googlebase.b.b;
import com.miui.securitycenter.h;
import java.util.Calendar;
import miui.os.SystemProperties;

public class c {
    public static void a(Context context) {
        if (!b(context)) {
            c(context);
        }
    }

    public static boolean b(Context context) {
        Log.d("GmsModelUtils", "start scan ");
        if (!o.f2310b.equals(SystemProperties.get("ro.miui.has_gmscore"))) {
            return true;
        }
        if (h.g() == -1) {
            if (Calendar.getInstance().get(1) <= 2014) {
                return true;
            }
            h.e(System.currentTimeMillis());
        }
        int a2 = z.a(h.g());
        Log.d("GmsModelUtils", "time realInterval : " + a2);
        if (a2 >= 7 && a2 <= 180 && b.a(context) == 1 && !b.b(context)) {
            Log.d("GmsModelUtils", "status danger ");
            return false;
        } else if (a2 <= 180 && a2 >= 0) {
            return true;
        } else {
            h.e(System.currentTimeMillis());
            return true;
        }
    }

    public static void c(Context context) {
        if (o.f2310b.equals(SystemProperties.get("ro.miui.has_gmscore"))) {
            Log.d("GmsModelUtils", "start optimize ");
            b.a(context, 2);
        }
    }
}
