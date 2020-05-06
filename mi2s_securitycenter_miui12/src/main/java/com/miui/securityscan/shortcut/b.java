package com.miui.securityscan.shortcut;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import b.b.o.g.c;
import com.miui.securityscan.shortcut.e;
import java.util.List;

public class b {
    public static void a(Context context, e.a aVar) {
        try {
            Object systemService = context.getSystemService("shortcut");
            if (a(systemService)) {
                Intent a2 = e.a(aVar);
                String c2 = e.c(aVar);
                int b2 = e.b(aVar);
                c.a a3 = c.a.a("android.graphics.drawable.Icon");
                a3.b("createWithResource", new Class[]{Context.class, Integer.TYPE}, context, Integer.valueOf(b2));
                Object d2 = a3.d();
                c.a a4 = c.a.a("android.content.pm.ShortcutInfo$Builder");
                a4.a(new Class[]{Context.class, String.class}, context, c2);
                Class[] clsArr = new Class[1];
                clsArr[0] = Class.forName("android.graphics.drawable.Icon");
                a4.a("setIcon", clsArr, d2);
                a4.e();
                a4.a("setShortLabel", new Class[]{CharSequence.class}, c2);
                a4.e();
                a4.a("setIntent", new Class[]{Intent.class}, a2);
                a4.e();
                a4.a("build", (Class<?>[]) null, new Object[0]);
                Object d3 = a4.d();
                c.a a5 = c.a.a(systemService);
                Class[] clsArr2 = new Class[2];
                clsArr2[0] = Class.forName("android.content.pm.ShortcutInfo");
                clsArr2[1] = IntentSender.class;
                a5.a("requestPinShortcut", clsArr2, d3, null);
            }
        } catch (Exception e) {
            Log.e("securityscan.ShortcutCompat", "createShortcut error ", e);
        }
    }

    private static boolean a(Object obj) {
        c.a a2 = c.a.a(obj);
        a2.a("isRequestPinShortcutSupported", (Class<?>[]) null, new Object[0]);
        return a2.a();
    }

    public static boolean b(Context context, e.a aVar) {
        try {
            List<Object> list = (List) c.a(context.getSystemService("shortcut"), List.class, "getPinnedShortcuts", (Class<?>[]) null, new Object[0]);
            if (list != null && !list.isEmpty()) {
                for (Object a2 : list) {
                    String str = (String) c.a(a2, String.class, "getId", (Class<?>[]) null, new Object[0]);
                    if (str != null && str.equals(e.c(aVar))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("securityscan.ShortcutCompat", "isInPinnedShortcutsList error ", e);
        }
        return false;
    }
}
