package com.miui.cleanmaster;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import miui.os.Build;

public class a {
    private static int a(Context context, String str, String str2, int i) {
        Class<?> cls = Class.forName("android.provider.MiuiSettings$SettingsCloudData");
        Class cls2 = Integer.TYPE;
        return ((Integer) e.a(cls, cls2, "getCloudDataInt", (Class<?>[]) new Class[]{ContentResolver.class, String.class, String.class, cls2}, context.getContentResolver(), str, str2, Integer.valueOf(i))).intValue();
    }

    public static void a(Context context) {
        c(context);
        if (a()) {
            e(context);
            return;
        }
        d(context);
        b(context);
    }

    private static boolean a() {
        return Build.IS_INTERNATIONAL_BUILD;
    }

    private static void b(Context context) {
        try {
            b.b("cm_general_clean_notification_cnt", a(context, "cmGeneralNotificationModule", "cmGeneralNotificationCnt", 5));
        } catch (Exception unused) {
        }
    }

    private static void c(Context context) {
        Log.i("CMCloudControlHelper", "loadNotificationPriorityConfig");
        try {
            int a2 = a(context, "CmNotificationPriorityConfig", "CnSizeThreshold", 32);
            int a3 = a(context, "CmNotificationPriorityConfig", "GlobalSizeThreshold", 0);
            d a4 = d.a(context);
            a4.a(a2);
            a4.b(a3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void d(Context context) {
        try {
            b.b("cm_wechat_notification_cnt", a(context, "cmWechatNotificationModule", "cmWechatNotificationCnt", 5));
        } catch (Exception unused) {
        }
    }

    private static void e(Context context) {
        try {
            b.b("cm_whatsapp_clean_notification_cnt", a(context, "cmWhatsAppNotificationModule", "cmWhatsAppNotificationCnt", 5));
        } catch (Exception unused) {
        }
    }
}
